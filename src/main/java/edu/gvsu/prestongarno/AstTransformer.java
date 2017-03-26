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

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import java.lang.reflect.Field;
import java.util.Map;


/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.asttools - by Preston Garno on 3/25/17
 * ***************************************************/
public class AstTransformer extends TreeTranslator {
	
	
	Trees trees;
	TreeMaker make;
	Name.Table names;
	
	public AstTransformer(JavacProcessingEnvironment environment) {
		trees = Trees.instance(environment);
		make = TreeMaker.instance(environment.getContext());
	}
	
	static Map getSourceMap(JavacProcessingEnvironment environment) {
		Context context = environment.getContext();
		Field f = null; //NoSuchFieldException
		try {
			f = context.getClass().getDeclaredField("ht");
			f.setAccessible(true);
			return (Map) f.get(context); //IllegalAccessException
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void visitLambda(JCTree.JCLambda lambda) {
		super.visitLambda(lambda);
		System.out.println(lambda.body.toString());
	}
	
	/**
	 * Getter for property 'trees'.
	 *
	 * @return Value for property 'trees'.
	 */
	public Trees getTrees() {
		return trees;
	}
}
