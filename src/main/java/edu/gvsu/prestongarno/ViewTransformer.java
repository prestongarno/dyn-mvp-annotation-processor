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



import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.lang.reflect.Field;
import java.util.*;


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
	private final ArrayList<SimpleJavaFileObject> fileObjects;
	
	ViewTransformer(JavacProcessingEnvironment environment) {
		this.env = environment;
		trees = Trees.instance(environment);
		mod = TreeMaker.instance(environment.getContext());
		
		this.fileObjects = new ArrayList<>();
		this.sourceMap = getSourceMap(environment);
	}
	
	JCTree getTree(Element element) {
		return ((JCTree) trees.getTree(element));
	}
	
	@Override
	public void visitLambda(JCTree.JCLambda lambda) {
		super.visitLambda(lambda);
		System.out.println(lambda.body.toString());
	}
	
	@Override
	public void visitClassDef(JCTree.JCClassDecl decl) {
		super.visitClassDef(decl);
		
		Symbol.ClassSymbol translateSym =
				env.getElementUtils()
						.getTypeElement("edu.gvsu.prestongarno.annotations.TranslateView");
		
		final JCTree.JCExpression[] ts = new JCTree.JCExpression[decl.implementing.size() + 1];
		
		// trying to tack on an implementation here
		JCTree.JCExpression exp = mod.Ident(translateSym);
		
		//Types.instance(this.env.getContext()).boxedClass(((Type) t));
		ts[decl.implementing.size()]  = exp;
		Iterator<JCTree.JCExpression> it = decl.implementing.iterator();
		for (int i = 0; i < decl.implementing.size(); i++) {
			ts[i] = it.next();
		}
		
		List<JCTree.JCExpression> implementsList = List.from(ts);
		
		Symbol.MethodSymbol m = (Symbol.MethodSymbol) translateSym.members().getElements().iterator().next();
		
		JCTree.JCMethodDecl meth = mod.MethodDef(m, null);
		meth.sym.owner = decl.sym;
		JCTree[] contents = new JCTree[decl.defs.size() + 1];
		contents[contents.length - 1] = meth;
		
		for (int i = 0; i < decl.defs.size(); i++) {
			contents[i] = decl.defs.get(i);
		}
		
		result =  mod.ClassDef(
				decl.mods,
				decl.name,
				decl.typarams,
				decl.extending,
				implementsList,
				List.from(contents)
		);
		
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
			
			sourceMap.keySet().forEach(object -> {
				try {
					Field field = object.getClass().getSuperclass().getDeclaredField("clientFileObject");
					field.setAccessible(true);
					this.fileObjects.add((SimpleJavaFileObject) field.get(object));
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			});
			
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
	
	ArrayList<SimpleJavaFileObject> getFileObjects() {
		return this.fileObjects;
	}
/*
	Type t = decl.extending == null ? null : decl.extending.type;
		if (t != null && t.tsym != null && t.tsym.toString().equals("edu.gvsu.prestongarno.Event")) {
				//modify the call() method from this
				}*/
}
