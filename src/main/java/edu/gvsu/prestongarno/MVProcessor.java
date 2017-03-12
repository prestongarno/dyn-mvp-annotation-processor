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

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class MVProcessor extends AbstractProcessor {

    private RoundEnvironment roundEnvironment;
    private Messager messager;

    //Rules annotation type element:
    TypeElement rulesAnnType;

    private int roundCount;

    public MVProcessor(){
        this.roundCount = 0;
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        this.roundCount++;
        this.roundEnvironment = roundEnvironment;
        this.messager = this.processingEnv.getMessager();

        this.rulesAnnType = this.processingEnv.getElementUtils().getTypeElement("edu.gvsu.prestongarno.annotations.meta.AnnotationRule");
        if(this.rulesAnnType == null)
            throw new LinkageError("Error: Module dynamic-mvp not found\nCheck build dependencies to resolve.");

        //Diagnostics: count the rounds
        this.messager.printMessage(Diagnostic.Kind.NOTE, "\n\nANNOTATION PROCESSING, round #" + this.roundCount
                + "\n\n>>>>>>>>>>>>>>>>>>>>>>");
        for (TypeElement aSet : set) {
            this.printAllElements(aSet);
        }

        roundEnvironment.getRootElements().forEach(this::printAllElements);

        return false;
    }

    private void printAllElements(TypeElement element) {
        this.messager.printMessage(Diagnostic.Kind.NOTE,
                "\nDetails for TypeElement: " + element.toString()
                + Util.prettyPrintElement(element), element);
        List<? extends Element> subElements = this.processingEnv.getElementUtils().getAllMembers(element);
        for (Element e : subElements) {
            this.messager.printMessage(Diagnostic.Kind.NOTE, Util.prettyPrintElement(e), e);
        }
    }

    private void printAllElements(Element element) {
        this.messager.printMessage(Diagnostic.Kind.NOTE,
                "\nDetails for TypeElement: " + element.toString()
                        + Util.prettyPrintElement(element), element);
        List<? extends Element> subElements = element.getEnclosedElements();
        for (Element e : subElements) {
            this.messager.printMessage(Diagnostic.Kind.NOTE, "++\n" + Util.prettyPrintElement(e), e);
        }
    }
}