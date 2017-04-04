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
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import static com.sun.tools.javac.tree.JCTree.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.tools.javac.code.Symbol.*;
import static com.sun.tools.javac.code.Type.*;



/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno - by Preston Garno on 3/28/17
 *
 * Class to add behaviour to types
 * ***************************************************/
public class JavacUtil {
	
	
	public static JCClassDecl implementInterface(Context context,
																JCClassDecl classDecl,
																Type interfaceToAdd,
																List<JCMethodDecl> methods) {
		
		TreeMaker maker = TreeMaker.instance(context);
		
		classDecl.implementing = classDecl.implementing.append(maker.Ident(interfaceToAdd.tsym));
		
		for (JCMethodDecl tr : methods) {
			classDecl = injectMethod(context, classDecl, tr);
		}
		
		ClassSymbol symbol = classDecl.sym;
		ClassType TYPE = (ClassType) symbol.type;
		TYPE.all_interfaces_field = TYPE.all_interfaces_field.append(interfaceToAdd);
		TYPE.interfaces_field = TYPE.interfaces_field.append(interfaceToAdd);
		
		// change the interfaces on the type
		TYPE.tsym = symbol;
		classDecl.type = TYPE;
		return classDecl;
	}
	
	public static JCClassDecl injectMethod(Context context, JCClassDecl classDecl, JCMethodDecl method) {
		
		classDecl.defs = classDecl.defs.append(method);
		
		ClassSymbol symbol = classDecl.sym;
		
		method.sym = fixMethodMirror(
				context,
				symbol,
				method.getModifiers().flags,
				method.name,
				List.from(method.getParameters().stream()
						.map(decl1 -> decl1.type)
						.collect(Collectors.toList())),
				method.getReturnType().type);
		
		method.sym.owner = classDecl.sym;
		
		return classDecl;
	}
	
	
	/*****************************************
	 * Fixes other references/scopes to this method without closing the class for modification
	 * @param cs
	 * @param access
	 * @param methodName
	 * @param paramTypes
	 * @param returnType
	 ****************************************/
	private static MethodSymbol fixMethodMirror( Context context,
													 ClassSymbol cs,
													 long access,
													 Name methodName,
													 List<Type> paramTypes,
													 Type returnType) {
		
		final MethodType methodSym = new MethodType( paramTypes,
				returnType,
				List.nil(),
				Symtab.instance(context).methodClass);
		
		MethodSymbol methodSymbol = new MethodSymbol(	access,
																		methodName,
																		methodSym,
																		cs);
		
		JavacUtil.SymbolScopeUtil.enter(cs, methodSymbol);
		
		return methodSymbol;
	}
	/*****************************************
	 * Helper class using Reflection to prevent Javac from
	 * finalizing/completing symbols before transformation
	 ****************************************/
	static class SymbolScopeUtil {
		private static final Field membersField;
		private static final Field scopeEntryArray;
		private static final Method removeMethod;
		private static final Method enterMethod;
		
		static {
			Field f = null;
			Method r = null;
			Method e = null;
			Field d = null;
			try {
				f = Symbol.ClassSymbol.class.getField("members_field");
				r = f.getType().getMethod("remove", Symbol.class);
				e = f.getType().getMethod("enter", Symbol.class);
				d = Scope.class.getDeclaredField("table");
			} catch (Exception ex) {}
			membersField = f;
			removeMethod = r;
			enterMethod = e;
			scopeEntryArray = d;
		}
		
		static void remove(Symbol.ClassSymbol from, Symbol toRemove) {
			if (from == null) return;
			try {
				Scope scope = getField(membersField, Scope.class, from);
				removeMethod.invoke(scope, toRemove);
			} catch (Exception e) { e.printStackTrace();}
		}
		
		static void enter(Symbol.ClassSymbol from, Symbol toEnter) {
			if (from == null) return;
			try {
				Scope scope = getField(membersField, Scope.class, from);
				enterMethod.invoke(scope, toEnter);
			} catch (Exception e) {e.printStackTrace();}
		}
		
		static ArrayList<Symbol> getMembersList(Symbol symbol) {
			Scope c = getField(membersField, Scope.class, symbol);
			Scope.Entry[] entries = getField(scopeEntryArray, Scope.Entry[].class, c);
			return Arrays
					.stream(entries)
					.filter(Objects::nonNull)
					.map(entry -> entry.sym)
					.collect(Collectors.toCollection(ArrayList::new));
		}
		
		@SuppressWarnings("unchecked")
		private static <V> V getField(Field field, Class<V> type, Object object) {
			try {
				field.setAccessible(true);
				return (V) field.get(object);
			} catch (IllegalAccessException e) { e.printStackTrace(); }
			throw new IllegalArgumentException("No such Field " + field.getName() + " of type " + type);
		}
	}
	
	
}
