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

package edu.gvsu.prestongarno;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Set;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno - by Preston Garno on 3/11/17
 ***************************************************/
public class Util {

    public static String prettyPrintElement(Element element){
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        String prettyMirrors = "";
        if(annotationMirrors.isEmpty())
            prettyMirrors = "none";
        else {
            for (AnnotationMirror mirror : annotationMirrors) {
                prettyMirrors = prettyMirrors.concat("\n\t\t\t" + mirror.getAnnotationType().toString());

                //annotation values
                for (AnnotationValue x : mirror.getElementValues().values()) {
                    prettyMirrors = prettyMirrors.concat("\n\t\t\t\t\\- " +
                            x.toString() + "\t value = " + x.getValue().toString());
                }
            }
        }
        Set<Modifier> modifiers = element.getModifiers();
        String modifierToString = "" + modifiers.size();
        for (Modifier mod : modifiers) {
            modifierToString = modifierToString.concat(mod.toString() + ",");
        }
        return "\nElement:\t" + element.getKind().toString() + "\n\t\tName = " + element.getSimpleName()
                + "\n\t\tAs type: " + element.asType().toString()
                + "\n\t\tModifiers: " + modifiers
                + "\n\t\tAnnotations: " + prettyMirrors
                + "\n\t\tEnclosing Element = " + element.getEnclosingElement().asType();
    }

}
