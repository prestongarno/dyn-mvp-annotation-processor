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

package edu.gvsu.prestongarno.SourceBuilders;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.SourceBuilders - by Preston Garno on 3/10/17
 *
 * Generates source for accessing Callback implementations from View objects
 ***************************************************/
final class CallbackAccessorHelper {

    private final Element VIEW_ANNOTATION_ELEMENT;

    CallbackAccessorHelper(RoundEnvironment roundEnvironment) {
        roundEnvironment.getElementsAnnotatedWith(Annotation.class);
        VIEW_ANNOTATION_ELEMENT = null;
    }

    /**
     * Build a java file with static accessor methods for the given view class
     * @param viewElement
     * @return
     */
    public JavaFile build(Element viewElement)
    {
        List<? extends AnnotationMirror> annotationMirrors = viewElement.getAnnotationMirrors();
        for (AnnotationMirror a :
                annotationMirrors) {
        }
        return null;
    }

    boolean isView(Element element, RoundEnvironment environment) {
//        Element VIEW
        return false;
    }
}
