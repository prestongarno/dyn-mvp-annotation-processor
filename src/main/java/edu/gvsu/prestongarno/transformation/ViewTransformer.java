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



import com.sun.source.util.Trees;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.util.Name;

import javax.lang.model.element.*;

import static com.sun.tools.javac.code.Symbol.*;
import static com.sun.tools.javac.tree.JCTree.*;


/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.asttools - by Preston Garno on 3/25/17
 *
 * TreeTranslator class for rewriting the compilation AST nodes
 *
 * By convention, a transformation method must be implemented as follows:
        	• Call super to ensure the transformation is applied to the node’s children
 				as well.
 			• Perform the actual transformation.
 			• Assign the transformed result to TreeTranslator.result. The type
 				of the result does not have to be the same as passed in. Instead,
 				we are free to return any type of tree node that is allowed by Java’s
 				grammar at that location. This constraint is not verified by the tree
 				translator itself, but return
 * ***************************************************/
public class ViewTransformer extends TreeTranslator {

	private final JavacProcessingEnvironment env;
	private final Trees trees;
	private final TreeMaker MODIFIER;
	private final Names names;
	private final JavacElements elements;
	private final Symtab symtab;
	private final ClassSymbol translateSym;
	
	public ViewTransformer(CompileContext context) {
		this.env = context.env;

		// symbol for the "TranslateView" interface that returns a presenter
		translateSym = env.getElementUtils().getTypeElement("edu.gvsu.prestongarno.annotations.TranslateView");

		trees = context.trees;
		MODIFIER = context.mod;
		elements = context.elements;
		names = context.names;
		symtab = context.symtab;
	}
	
	public JCTree getTree(Element element) {
		return ((JCTree) trees.getTree(element));
	}
	
	@Override
	public void visitLambda(JCLambda lambda) {
		super.visitLambda(lambda);
		printLambda(lambda);
	}

	private void printLambda(JCLambda lambda) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<============>>>>>>>>>>>>>>>>");
		System.out.println(lambda);
		System.out.println(lambda.getBodyKind());
		System.out.println("============");
		lambda.getParameters().forEach(e -> System.out.println("Parameter: " + e + " Name: " + e.getName() + " Kind: " + e.getKind() + " Type: " + e.getType()));
		System.out.println("============");
		System.out.println("Can complete Normally: " + lambda.canCompleteNormally);
		System.out.println("============");
		System.out.println(lambda.getKind());
		System.out.println("============");
		System.out.println(lambda.paramKind);
		System.out.println("============");
		System.out.println("isStandalone" + lambda.isStandalone());
		System.out.println("============");
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<============>>>>>>>>>>>>>>>>");
	}
	
	@Override
	public void visitClassDef(JCClassDecl decl) {
		super.visitClassDef(decl);
		
		JCFieldAccess iae = MODIFIER.Select(
				MODIFIER.Select( MODIFIER.Ident(elements.getName("java")), elements.getName("lang")),
				elements.getName("IllegalArgumentException"));
		
		MethodSymbol m = (MethodSymbol)
				JavacUtil.SymbolScopeUtil.getMembersList(translateSym).get(0);
		
		Type.ClassType annotationValue = (Type.ClassType) decl.sym.type.tsym.getAnnotationMirrors().stream()
				//.peek(compound -> System.out.println(compound.getAnnotationType().asElement()))
				.filter(compound -> compound.getAnnotationType()
						.asElement()
						.asType()
						.toString()
						.equals("edu.gvsu.prestongarno.annotations.View"))
				.findAny()
				.orElseThrow(IllegalStateException::new)
				.getElementValues()
				.values()
				.iterator()
				.next()
				.getValue();
		
		JCExpression jcf = MODIFIER.Ident(annotationValue.tsym);
		
		JCModifiers modifiers = MODIFIER.Modifiers(Flags.PUBLIC);
		Name name = m.name;
		JCExpression methodReturnType = MODIFIER.Type(m.getReturnType());
		List<JCTypeParameter> methodGenerics = List.nil();
		List<JCTree.JCVariableDecl> methodParams = List.nil();
		List<JCTree.JCExpression> methodThrows = List.nil();
		final List<JCStatement> of = List.of(MODIFIER.Return(MODIFIER.NewClass(
				null,
				List.nil(),
				jcf,
				List.nil(),
				null
		)));
		JCBlock methBlock = MODIFIER.Block(0, of);
		
		
		JCMethodDecl meth = MODIFIER.MethodDef(
				modifiers,
				name,
				methodReturnType,
				methodGenerics,
				methodParams,
				methodThrows,
				methBlock,
				null
		);
		
		JavacUtil.implementInterface(env.getContext(), decl, translateSym.type, List.of(meth));
	}
	
	
	/*****************************************
	 * Getter for property 'trees'.
	 *
	 * @return Value for property 'trees'.
	 ****************************************/
	public Trees getTrees() {
		return trees;
	}
	
	
}
