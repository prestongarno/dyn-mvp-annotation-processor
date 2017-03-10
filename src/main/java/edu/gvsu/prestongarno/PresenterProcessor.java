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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class PresenterProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processor initialized...");
        System.out.println("lol");
        /*for(Element e : roundEnvironment.getRootElements()) {
            //iterate through elements
           BufferedWriter writer = null;
           if(e.getKind() == ElementKind.CLASS && !e.getSimpleName().toString().equals("runnerGenerated")) {
               TypeElement classElement = (TypeElement) e;
               try {
                   JavaFileObject file = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName() + "Generated");
                   writer = new BufferedWriter(file.openWriter());
                   writer.append("package edu.gvsu.prestongarno.example;");
                   writer.append("\npublic class runnerGenerated {\n//got them niggas!\n");
                   writer.append("\tprivate static final String secretString = \"This thing is fucking awesome!\";");
                   writer.append("\n\tpublic static String getHackedString(){\n");
                   writer.append("\t\treturn runnerGenerated.secretString;\n\t}\n}");
                   writer.close();
               } catch (IOException e1){
                   processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error generating source for: " + e.getSimpleName());
               } finally {
                   if (writer != null){
                       try {
                           writer.close();
                       } catch (IOException e1) {
                           processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error closing writer!");
                       }
                   }
               }
           }*/


        return false;
    }

    /**
     * Checks all types annotated with edu.gvsu.prestongarno.annotations
     * for the correct types (i.e. methods only for method annotations, etc)
     */
    protected boolean checkTypesAnnotated(RoundEnvironment environment)
	{
        return false;
    }


    /**
     * Master method that runs checks on a view-presenter pair to make sure that all events and
     * callbacks are handled
     * @param view the view element
     * @param presenter the presenter element
     * @param environment the roundenviroment
     * @return true if the syntax is correct, false if not <br>
     *     note: the messager will print a detailed log message from here
     */
    protected boolean checkViewPresenterPair(Element view, Element presenter, RoundEnvironment environment)
	{
        return false;
    }

    protected boolean checkRawCallsHandled(Element view, Element presenter, RoundEnvironment environment)
	{
        return false;
    }

    protected boolean checkCallbackExists(Element handlerMethod, Element view, RoundEnvironment environment)
	{
        return false;
    }
}