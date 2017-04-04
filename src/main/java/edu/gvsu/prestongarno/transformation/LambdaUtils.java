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

package edu.gvsu.prestongarno.transformation;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.ArrayList;
import java.util.List;


/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.transformation - by Preston Garno on 4/3/17
 *
 * Strategy for lambdas:
 * 	1) Need to determine type of the parameters (do this by finding the fuctional interface type) THIS IS WAY HARDER THAN IT LOOKS!
 * 	2) create a complete static method that returns an instance of the interface with everything in the body of the lambda resolved (CAPTURES AND ALL)
 * 	3) replace the lambdas with method invocation statements
 *		4) resolve these by case/switch logic on which method to invoke to get which instance of the interface based off of what event (also harder than it looks...)
 * ***************************************************/
public class LambdaUtils {
	
	
	public static List<JCTree.JCLambda> getLambdasFor(JCTree.JCClassDecl classDecl) {
		ArrayList<JCTree.JCLambda> lambdas = new ArrayList<>();
		classDecl.accept(new TreeTranslator(){
			@Override
			public void visitLambda(JCTree.JCLambda lambda) {
				super.visitLambda(lambda);
				lambdas.add(lambda);
			}
		});
		return lambdas;
	}
	
}
