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
        for(Element e : roundEnvironment.getRootElements()) {
            //iterate through elements
           processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Element needs to be rewritten: " + e.getSimpleName());
           BufferedWriter writer = null;
           if(e.getKind() == ElementKind.CLASS && !e.getSimpleName().toString().equals("runnerGenerated")){
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
               } catch (IOException e1) {
                   processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error generating source for: " + e.getSimpleName());
               } finally {
                   if (writer != null) {
                       try {
                           writer.close();
                       } catch (IOException e1) {
                           processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error closing writer!");
                       }
                   }
               }
           }
        }

        return false;
    }
}