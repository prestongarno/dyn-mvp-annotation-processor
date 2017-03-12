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

package edu.gvsu.prestongarno.sourcegentests.TestUtil;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.JavaFile;

import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.sourcegentests.TestUtil - by Preston Garno on 3/10/17
 ***************************************************/
public class TestUtil {

    private static final String TEST_CLASS_SET_DIRECTORY = "/src/test/java/edu/gvsu/prestongarno/sourcegentests/SampleClassSets/";

    public static List<JavaFileObject> loadClassSet(int index) throws IOException {
        return Files.list((new File(System.getProperty("user.dir") + TEST_CLASS_SET_DIRECTORY + "/set_" + index)
                .toPath()))
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(File::canRead)
                .map(File::toPath)
                .map(TestUtil.logPath::print)
                .map(TestUtil.converter::toJavaFileObject).collect(toList());
    }

    @FunctionalInterface
    private interface Logger {
        Path print(Path path);
    }

    private static final Logger logPath = path -> {
        System.out.println("Loading class file: " + path.toString());
        return path;
    };

    @FunctionalInterface
    private interface PathConverter {
        JavaFileObject toJavaFileObject(Path path);
    }

    private static final PathConverter converter = (Path path) ->{
        try {
            return JavaFileObjects.forResource(path.toUri().toURL());
        } catch (MalformedURLException e) {
            return null;
        }
    };

    public static void outputDiagnostics(Compilation compilation) {
        List<? extends Diagnostic> diagnostics = compilation.diagnostics();
        for(Diagnostic d : diagnostics){
            System.out.println("================================================");
            System.out.println("Diagnostic: " + d.getKind().toString());
            System.out.println("Source = " + d.getSource());
            System.out.println(d.getMessage(Locale.ENGLISH));
        }
    }
}
