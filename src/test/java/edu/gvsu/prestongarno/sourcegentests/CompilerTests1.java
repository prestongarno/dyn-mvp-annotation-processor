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

package edu.gvsu.prestongarno.sourcegentests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import edu.gvsu.prestongarno.Event;
import edu.gvsu.prestongarno.MVProc;
import edu.gvsu.prestongarno.Presenter;
import edu.gvsu.prestongarno.annotations.TranslateView;
import org.junit.Test;

import java.util.Arrays;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static edu.gvsu.prestongarno.sourcegentests.CompilerUtil.*;
import static org.junit.Assert.assertTrue;


/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.sourcegentests - by Preston Garno on 3/10/17
 ****************************************/
public class CompilerTests1 {
	
	
	/*****************************************
	 * Simple test compile without any annotation processing
	 ****************************************/
	@Test
	public void simpleTestCompile() throws Exception {
		Compilation compilation = javac().compile(
				JavaFileObjects.forSourceString("HelloWorld", "final class HelloWorld {}"));
		assertThat(compilation).succeeded();
	}
	
	@Test
	public void testWithProcessor() throws Exception {
		Compilation compilation =
				javac()
						.withProcessors(new MVProc())
						.compile(loadClassSet(0));
		assertThat(compilation).succeededWithoutWarnings();
		
		outputDiagnostics(compilation);
	}
	
	@Test
	public void testMultipleFileCompilation() throws Exception {
		Compilation compilation = javac()
				.withProcessors(new MVProc())
				.compile(loadClassSet(2));
		outputDiagnostics(compilation);
	}
	
	/*****************************************
	 * FIRST custom, modified class loaded into memory and instance created!
	 ****************************************/
	@Test
	public void testClassloaderCreateFiles() throws Exception {
		final Compiler javac = javac();
		final MVProc proc = new MVProc();
		Compilation compilation = javac
				.withProcessors(proc)
				.compile(loadClassSet(2));
		
		assertThat(compilation).succeededWithoutWarnings();
		outputDiagnostics(compilation);
		
		ClassLoader loader = (new CompilerUtil().createClassLoader(compilation));
		
		final String fullName = CompilerUtil.getFullClassName("SampleView");
		
		Class clazz = loader.loadClass(fullName);
		
		Object instance = clazz.newInstance();
		
		assertTrue(instance instanceof TranslateView);
		final Presenter presenter = ((TranslateView) instance).create();
		assertTrue(presenter.getClass().getSimpleName().equals("SamplePresenter"));
		outputDiagnostics(compilation);
	}


	@Test
	public void changeLambdaCaptureToStaticMethod_Goal() throws Exception {

	}
}
