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
 ****************************************/

package edu.gvsu.prestongarno;



import com.sun.source.tree.ReturnTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import sun.tools.tree.ReturnStatement;
import com.sun.tools.javac.model.JavacElements;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import java.io.OptionalDataException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sun.tools.javac.code.Symbol.*;
import static com.sun.tools.javac.code.Type.*;
import static com.sun.tools.javac.tree.JCTree.*;
import static com.sun.tools.javac.util.List.nil;
import static edu.gvsu.prestongarno.JavacUtil.SymbolScopeUtil.enter;


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
	
	
	private final Map<String, ArrayList<JCTree>> sourceMap;
	private JavacProcessingEnvironment env;
	private Trees trees;
	private TreeMaker mod;
	private Names names;
	private JavacElements elements;
	private Symtab symtab;
	
	ViewTransformer(JavacProcessingEnvironment environment) {
		this.env = environment;
		trees = Trees.instance(environment);
		mod = TreeMaker.instance(environment.getContext());
		elements = JavacElements.instance(environment.getContext());
		names = Names.instance(environment.getContext());
		this.symtab = Symtab.instance(environment.getContext());
		this.sourceMap = getSourceMap(environment);
	}
	
	JCTree getTree(Element element) {
		return ((JCTree) trees.getTree(element));
	}
	
	@Override
	public void visitClassDef(JCClassDecl decl) {
		super.visitClassDef(decl);
		
		ClassSymbol translateSym =
				env.getElementUtils()
						.getTypeElement("edu.gvsu.prestongarno.annotations.TranslateView");
		
		
		
		JCFieldAccess iae = mod.Select(
				mod.Select(
						mod.Ident(elements.getName("java")),
						elements.getName("lang")),
				elements.getName("IllegalArgumentException"));
		
		MethodSymbol m = (MethodSymbol)
				JavacUtil.SymbolScopeUtil.getMembersList(translateSym).get(0);
		
		JCModifiers modifiers = mod.Modifiers(Flags.PUBLIC);
		Name name = m.name;
		JCExpression methodReturnType = mod.Type(m.getReturnType());
		List<JCTypeParameter> methodGenerics = List.nil();
		List<JCTree.JCVariableDecl> methodParams = List.nil();
		List<JCTree.JCExpression> methodThrows = List.nil();
		final List<JCStatement> of = List.of(mod.Throw(mod.NewClass(
				null,
				List.nil(),
				iae,
				List.nil(),
				null
		)));
		JCBlock methBlock = mod.Block(0, of);
		
		
		JCMethodDecl meth = mod.MethodDef(
				modifiers,
				name,
				methodReturnType,
				methodGenerics,
				methodParams,
				methodThrows,
				methBlock,
				null
		);
		
		meth.sym = m;
		meth.sym.owner = decl.sym;
		decl.defs = decl.defs.append(meth);
		
		ClassSymbol symbol = decl.sym;
		ClassType TYPE = (ClassType) symbol.type;
		decl.implementing = decl.implementing.append(mod.Ident(translateSym));
		TYPE.all_interfaces_field = TYPE.all_interfaces_field.append(translateSym.type);
		TYPE.interfaces_field = TYPE.interfaces_field.append(translateSym.type);
		
		fixMethodMirror(
				symbol,
				meth.getModifiers().flags,
				meth.name,
				List.from(methodParams.stream().map(decl1 -> decl1.type).collect(Collectors.toList())),
				methodReturnType.type);
		
		// change the interfaces on the type
		TYPE.tsym = symbol;
		result.type = TYPE;
		
		System.out.println(result);
	}
	
	
	/*****************************************
	 * Fixes other references/scopes to this method without closing the class for modification
	 * @param cs
	 * @param access
	 * @param methodName
	 * @param paramTypes
	 * @param returnType
	 ****************************************/
	private void fixMethodMirror( ClassSymbol cs,
												long access,
												Name methodName,
												List<Type> paramTypes,
												Type returnType) {
		
		final MethodType methodSym = new MethodType(paramTypes,
				returnType,
				List.nil(),
				symtab.methodClass);
		
		MethodSymbol methodSymbol = new MethodSymbol(
				access,
				methodName,
				methodSym,
				cs);
		
		enter(cs, methodSymbol);
	}
	/*****************************************
	 * Getter for property 'trees'.
	 *
	 * @return Value for property 'trees'.
	 ****************************************/
	public Trees getTrees() {
		return trees;
	}
	
	/*****************************************
	 * Get all AST nodes through reflection
	 * @param environment the javac environment
	 * @return a map of all compiling classes and all nodes in the AST for each class
	 ****************************************/
	private Map<String, ArrayList<JCTree>> getSourceMap(JavacProcessingEnvironment environment) {
		Context context = environment.getContext();
		Field f;
		try {
			f = context.getClass().getDeclaredField("ht");
			f.setAccessible(true);
			
			//noinspection unchecked
			Map<Context.Key, Object> map = (Map<Context.Key, Object>) f.get(context);
			
			final Log logger = map.entrySet().stream()
					.filter(entry -> entry.getValue() instanceof Log)
					.map(entry -> ((Log) entry.getValue()))
					.findAny().orElseThrow(IllegalStateException::new);
			
			f = logger.getClass()
					.getSuperclass()
					.getDeclaredField("sourceMap");
			f.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			Map<JavaFileObject, DiagnosticSource> sourceMap =
					(Map<JavaFileObject, DiagnosticSource>) f.get(logger);
			
			Map<String, ArrayList<JCTree>> classes = new HashMap<>();
			
			for (DiagnosticSource ds : sourceMap.values()) {
				String className = ds.getFile().getName();
				
				f = ds.getClass().getDeclaredField("endPosTable");
				f.setAccessible(true);
				EndPosTable obj = (EndPosTable) f.get(ds);
				f = obj.getClass().getDeclaredField("endPosMap");
				f.setAccessible(true);
				IntHashTable table = (IntHashTable) f.get(obj);
				f = table.getClass().getDeclaredField("objs");
				f.setAccessible(true);
				
				Object[] source = (Object[]) f.get(table);
				
				ArrayList<JCTree> trees = new ArrayList<JCTree>();
				
				for (int i = 0; i < source.length; i++) {
					trees.add((JCTree) source[i]);
				}
				classes.put(className, trees);
			}
			
			return classes;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
