/*
 *       Copyright (c) 2017.  Preston Garno
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */

package edu.gvsu.prestongarno.validation;

import edu.gvsu.prestongarno.MVProcessor;
import edu.gvsu.prestongarno.validation.exceptions.SyntaxException;

import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.validation - by Preston Garno on 3/11/17
 * <p>
 * Class containing annotation validation implementations.
 * (Say that 5 times fast)
 ***************************************************/
public final class RULES {

    public static void validate(String name, Element toValidate) {
        switch (name) {
            case "TYPE_IMMUTABLE":
                if (toValidate instanceof TypeElement) {
                    TYPE_IMMUTABLE.validateType((TypeElement) toValidate);
                } else {
                    throw new ClassCastException("You had one job!\n"
                            + toValidate.asType().toString()
                            + " is not a TypeElement.");
                }
        }
        throw new IllegalArgumentException("No such rule exists!");
    }


    private static final TypeValidator TYPE_IMMUTABLE = (TypeElement typeElement) -> {
        Modifier modifier;

        String name = typeElement.getSimpleName().toString();
        if (name.isEmpty()) {
            name = typeElement.asType().toString()
                    + " in " + typeElement.getEnclosingElement().toString();
        }

        // check final class
        if (!typeElement.getModifiers().contains(Modifier.FINAL)) {
            throw new SyntaxException("Type " + name + " must be declared final!");
        }


        //check fields of the class
        Element[] nonFinalElements = typeElement.getEnclosedElements()
                .stream()
                .filter(o -> o.getKind() == ElementKind.FIELD)
                .filter(o -> !o.getModifiers().contains(Modifier.FINAL))
                .toArray(Element[]::new);
        if (!(nonFinalElements.length == 0)) {
            String message = "Syntax error: Contract for Type " + name + " is declared as Immutable";
            for (Element nn : nonFinalElements) {
                message = message.concat("\n\t\tField " + nn.getSimpleName() + " must be declared final");
            }
            throw new NotImmutableException(message);
        }
    };


    /**
     * This method has no type safety, only tries to get the field in this class that equals validatorname
     *
     * @param validatorName
     * @return functionalinterface that can be used to invoke on an element
     */
    public static Object getValidator(String validatorName) {
        try {
        Field match = null;
            match = Arrays.stream(RULES.class.getDeclaredFields())
                    .filter(field -> field.getName().equals(validatorName))
                    .findFirst()
                    .orElseThrow(InstantiationException::new);
        match.setAccessible(true);
            return match.get(match.getClass().newInstance());
        } catch (Exception e) {
            MVProcessor.getInstance().messager.
                    printMessage(Diagnostic.Kind.ERROR, "No such validator for key: " + validatorName);
            //compiler will exit on ^^
            return null;
        }
    }

    private static final class NotImmutableException extends SyntaxException {

        NotImmutableException(String logMessage) {
            super(logMessage);
        }
    }
}
