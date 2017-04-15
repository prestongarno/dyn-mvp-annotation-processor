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

import com.sun.source.util.SimpleTreeVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Infer;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;

import java.util.ArrayList;
import java.util.List;

import static com.sun.tools.javac.tree.TreeInfo.*;


/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.transformation - by Preston Garno on 4/3/17
 * <p>
 * Strategy for lambdas:
 * 1) Need to determine type of the parameters (do this by finding the fuctional interface type) THIS IS WAY HARDER THAN IT LOOKS!
 * 2) create a complete static method that returns an instance of the interface with everything in the body of the lambda resolved (CAPTURES AND ALL)
 * 3) replace the lambdas with method invocation statements
 * 4) resolve these by case/switch logic on which method to invoke to get which instance of the interface based off of what event (also harder than it looks...)
 ***************************************************/
public final class LambdaTranslator extends SimpleTreeVisitor {

	private final Context javacCompilationContext;
	private final CompileContext CTX;
	private final Infer INF;
	// Compiler state is at processing which occurs right after Enter
	// so we shoult only really use those definitions and then look
	// at inference engine to construct a resolver for type vars generics, etTree.JCCompilationUnitc

	LambdaTranslator(CompileContext comp) {
		javacCompilationContext = comp.env.getContext();
		this.CTX = comp;
		this.INF = Infer.instance(comp.env.getContext());
		Todo.instance(javacCompilationContext);
	}

	/**
	 * @param lambda the lambda to get the type of
	 */
	private Type getType(JCTree.JCLambda lambda) {
		throw new UnsupportedOperationException();
	}

}
