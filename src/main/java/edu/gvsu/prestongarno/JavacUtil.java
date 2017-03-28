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

import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno - by Preston Garno on 3/28/17
 * ***************************************************/
public class JavacUtil {
	
	
	/*****************************************
	 * Helper class using Reflection to prevent Javac from finalizing/completing
	 * symbols before transformation
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
		
		static List<Symbol> getMembersList(Symbol symbol) {
			Scope c = getField(membersField, Scope.class, symbol);
			Scope.Entry[] entries = getField(scopeEntryArray, Scope.Entry[].class, c);
			return Arrays
					.stream(entries)
					.filter(Objects::nonNull)
					.map(entry -> entry.sym)
					.collect(Collectors.toList());
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
