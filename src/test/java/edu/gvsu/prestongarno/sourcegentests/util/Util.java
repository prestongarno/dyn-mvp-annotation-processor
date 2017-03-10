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

package edu.gvsu.prestongarno.sourcegentests.util;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

/**
 * *************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.sourcegentests.util - by Preston Garno on 3/10/17
 ***************************************************/
public class Util {
    public static String loadFile(String className){
        File f = new File(System.getProperty("user.dir") + "/src/test/java/edu/gvsu/prestongarno/sourcegentests/SampleFiles/" + className + ".java");
        System.out.println(f.getAbsolutePath());
        if (!f.exists() || f.isDirectory()) {
            throw new IllegalArgumentException("File is not valid!");
        }
        try {
            String result = "";
            List<String> list = Files.readAllLines(f.toPath());
            for (String s :
                    list) {
                result = result.concat(s + "\n");
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
