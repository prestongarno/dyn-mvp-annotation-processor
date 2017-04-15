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
import edu.gvsu.prestongarno.MVProc;
import edu.gvsu.prestongarno.Presenter;
import edu.gvsu.prestongarno.annotations.TranslateView;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static edu.gvsu.prestongarno.sourcegentests.CompilerUtil.*;
import static org.junit.Assert.assertTrue;

/*****************************************
 * Created by preston on 4/15/17.
 *
 * Test cases for translating lambdas to their appropriate definition at compile time
 ****************************************/
public class LambdaTransTests {
	/**
	 * Steps:
	 * 	1) Get Lambda type in order to infer method parameters
	 */
}
