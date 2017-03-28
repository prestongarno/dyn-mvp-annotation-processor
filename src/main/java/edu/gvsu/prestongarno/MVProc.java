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
 *        limitations under the License
 *        .
 */

package edu.gvsu.prestongarno;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import edu.gvsu.prestongarno.annotations.View;

import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.SimpleJavaFileObject;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class MVProc extends AbstractProcessor {
	
	
	
	RoundEnvironment roundEnvironment;
	public Messager messager;
	
	//Rules annotation type element:
	TypeElement annotationRule;
	
	/******************************************************************************************
	 * Hacks with the AST
	 ******************************************************************************************/
	//Context javaContext;
	JavacProcessingEnvironment javacEnv;
	/******************************************************************************************/
	private static MVProc instance;
	
	private int roundCount;
	private ViewTransformer viewTransformer;
	
	public MVProc() {
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
		viewTransformer = new ViewTransformer(this.javacEnv);
		
		for (Element o : roundEnvironment.getElementsAnnotatedWith(View.class)) {
			this.viewTransformer.getTree(o).accept(viewTransformer);
		}
		
		instance = null;
		return false;
	}
	
	/**
	 * Initializes all the stuff needed for the processing proces
	 */
	private void initializeProc(RoundEnvironment roundEnvironment) {
		this.roundCount++;
		this.roundEnvironment = roundEnvironment;
		this.messager = this.processingEnv.getMessager();
		
		this.annotationRule = this.processingEnv.getElementUtils()
				.getTypeElement("edu.gvsu.prestongarno.annotations.meta.AnnotationRule");
		if (this.annotationRule == null)
			throw new LinkageError("Error: Module dynamic-mvp not found\nCheck build dependencies to resolve.");
	}
	
	public static MVProc getInstance() {
		if (instance == null)
			throw new Error("Unfortunately instance of MVProcessor " +
					"doesn't exist yet operations are still running.");
		return instance;
	}
	
	public List<SimpleJavaFileObject> getFileObject() {
		return this.viewTransformer.getFileObjects();
	}
}