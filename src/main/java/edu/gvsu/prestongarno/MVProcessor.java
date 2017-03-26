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



import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Log;

import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class MVProcessor extends AbstractProcessor {
	
	
	public RoundEnvironment roundEnvironment;
	public Messager messager;
	
	//Rules annotation type element:
	public TypeElement annotationRule;
	public List<DeclaredType> mvp_annotations;
	
	/******************************************************************************************
	 * Hacks with the AST
	 ******************************************************************************************/
	//Context javaContext;
	private JavacProcessingEnvironment javacEnv;
	/******************************************************************************************/
	private static MVProcessor instance;
	
	private int roundCount;
	
	public MVProcessor() {
		this.roundCount = 0;
	}
	
	@Override
	public synchronized void init(ProcessingEnvironment pe) {
		super.init(pe);
		javacEnv = ((JavacProcessingEnvironment) pe);
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		this.initializeProc(roundEnvironment);
		//validating all that are tagged with annotation rules
		  /*mvp_annotations.stream().peek(roundEnvironment::getElementsAnnotatedWith)
                .filter(element -> element.getEnclosingElement()
                        .getAnnotationMirrors()
                        .stream()
                        .allMatch(o -> o.getAnnotationType()
                                .equals(this.annotationRule))).forEach(System.out::println);*/
		//.peek(element -> Checker.check(NEED_ABOVE_ELEMENT, element.getEnclosedElements().toString())).;
		
		AstTransformer transformer = new AstTransformer(this.javacEnv);
		
		roundEnvironment.getRootElements().stream()
				.filter(o -> o.getKind() == ElementKind.CLASS)
				.map(o -> (JCTree) transformer.getTrees().getTree(o)).forEach(tree -> tree.accept((TreeTranslator) transformer));
		
		List<Log> listLogs = new ArrayList<>();
		instance = null;
		return false;
	}
	
	/**
	 * Initializes all the stuff needed for the processing process
	 */
	private void initializeProc(RoundEnvironment roundEnvironment) {
		this.roundCount++;
		this.roundEnvironment = roundEnvironment;
		this.messager = this.processingEnv.getMessager();
		
		this.annotationRule = this.processingEnv.getElementUtils()
				.getTypeElement("edu.gvsu.prestongarno.annotations.meta.AnnotationRule");
		if (this.annotationRule == null)
			throw new LinkageError("Error: Module dynamic-mvp not found\nCheck build dependencies to resolve.");
		
/*		this.mvp_annotations = processingEnv.getElementUtils()
				.getPackageElement("edu.gvsu.prestongarno.annotations")
				.getEnclosedElements()
				.stream()
				.filter(o -> o.getKind() == ElementKind.ANNOTATION_TYPE)
				.map(Element::asType)
				.filter(typeMirror -> typeMirror instanceof DeclaredType)
				.map(typeMirror -> (DeclaredType) typeMirror)
				.peek(type -> this.printAllElementsOf(type.asElement()))
				.collect(toList());
		System.out.println("Processing round (" + this.roundCount + ") elements>>");*/
	}
	
	public static MVProcessor getInstance() {
		// This should never happen, but we want to know if it does
		if (instance == null)
			throw new Error("Unfortunately instance of MVProcessor " +
					"doesn't exist yet operations are still running.");
		return instance;
	}
	
	private void printAllElementsOf(Element element) {
		this.messager.printMessage(Diagnostic.Kind.NOTE,
				"\nDetails for TypeElement: " + element.toString()
						+ Util.prettyPrintElement(element), element);
		List<? extends Element> subElements = element.getEnclosedElements();
		for (Element e : subElements) {
			this.messager.printMessage(Diagnostic.Kind.NOTE, "++\n" + Util.prettyPrintElement(e), e);
		}
	}
}