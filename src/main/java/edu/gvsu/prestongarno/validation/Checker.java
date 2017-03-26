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
import javax.tools.Diagnostic;
import java.lang.reflect.Method;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.validation.exceptions - by Preston Garno on 3/12/17
 *
 * Uses reflection to run the correct validator for the
 * key that is specified by @AnnotationRule
 ***************************************************/
public class Checker {
    public static void check(Object element, String validatorKey) {
        Object validator = RULES.getValidator(validatorKey);
        System.out.println("Validating for: " + element + " with ::" + validator);
        Method m = validator.getClass().getMethods()[0];
        m.setAccessible(true);
        try {
            m.invoke(validator, element);
        } catch (Exception e) {
            MVProcessor.getInstance().messager.
                    printMessage(Diagnostic.Kind.ERROR, "Something went wrong with validating "
                            + element.toString() + " with validator "
                            + validator + " with method " + m.toString());
        }
    }
}
