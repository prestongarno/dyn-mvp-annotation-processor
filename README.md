<!--
  ~       Copyright (c) 2017.  Preston Garno
  ~
  ~        Licensed under the Apache License, Version 2.0 (the "License");
  ~        you may not use this file except in compliance with the License.
  ~        You may obtain a copy of the License at
  ~
  ~            http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~        Unless required by applicable law or agreed to in writing, software
  ~        distributed under the License is distributed on an "AS IS" BASIS,
  ~        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~        See the License for the specific language governing permissions and
  ~        limitations under the License.
  -->
__Notes.  This is an experimental project - with no goal in particular except learning to push javac to the limit__

Backwards compatible lambdas exist but this lib needs to do it no matter what, fake define a final inner class could get interesting.  Had this idea and then noticed JDK9 is working on the same thing. Here's what they say (can't find original source):

   1. Make up a new name nobody has used yet.
   2. Inspect the tail-header of the class to find the this_class index.
   3. Patch the CONSTANT_Class for this_class to the new name.
   4. Add other CP entries required by (e.g.) string patches.
   5. Flatten Class constants down to their names, making sure that
      the host class loader can pick them up again accurately.
   6. Generate the edited class file bytes.
       
Potential limitations:
   * The class won't be truly anonymous, and may interfere with others.
   * Flattened class constants might not work, because of loader issues. (although lambdas require effectively final var anyways to capture though...)
   * Pseudo-string constants will not flatten down to real strings.
   * Method handles will (of course) fail to flatten to linkage strings.

Pros:

1) composition rather than inheritance for complicated GUI systems<br><br>
"Inheritance is most useful for grouping related sets of concepts, identifying families of classes, and in general organizing the names and concepts that describe the domain. As we delve deeper into the implementation of a system, we may find that our original generalizations about the domain concepts, captured in our inheritance hierarchies, are beginning to shred. Don’t be afraid to disassemble inheritance hierarchies into sets of complementary cooperating interfaces and components when the code leads you in that direction" <br> <br>
2) start off with stateless events/event handlers - worry about 2-way events later



#Important Javac classes to know:
- Obviously `JCTree`
- `com.sun.tools.javac.com.Lower`: Contains a perfect AST with full scope, iterators etc.
- `com.sun.tools.javac.comp.Env`: In the AST map - contains important data like top level compilation loc. etc.
- `com.sun.tools.javac.code.Symtab`: Contains default/Useful symbols like null, void, common exceptions (for compiler)
- `com.sun.tools.javac.code.TypeEnvs`: The Holy Grail of Javac AST - everything here. Need to use reflection to get this for easy navigation around tree because it's package private and no interface, sub/super, public field anywhere.
- `com.sun.tools.javac.comp.Resolve`: Algorithms for locating symbols in scope environment or even entire compilation unit!
- `com.sun.tools.javac.comp.TreeScanner`: Easily see contents/relationships between AST tree structures
- `com.sun.tools.javac.comp.JCTree.Tag`: Each node is tagged with more information, this also contains methods that denote information such as "isAssignmentOperation" (in obfuscated code ofc)
- `com.sun.tools.javac.comp.Flow`: Control flow algorithms on AST. From docs: "<b>Finally, local variable capture analysis (see CaptureAnalyzer) determines that local variables accessed within the scope of an inner class/lambda are either final or effectively-final"


___[Primary Javac Tree types cheat sheet](https://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/):___<br>
(I need this because it's difficult to think of the names of stuff when you're programming a program to program stuff)

| Name | Javac Trees/AST class | Usages | Example |
|  --- | :---: | :---------:|:----------------: |
|Operators| JCUnary | Assignment | = or ++ |
|Binary operation | JCBinary | Comparing data | if(1 > 2)
|Declaration| JCDeclaration | Declare a variable | int i; |
|Statement| JCStatement | Complete line of code | this.toString(); |
|Expression| JCExpressionStatement | Wrap these with ^^ | this.toString |
|Methods|JCMethodDef | Declare | this.toString |
|Class| JCClassDecl | class declaration| public class Pi{} |
|Method invocation|JCMethodInvokation| invoke a method (wrap in JCStatement or use with JCIdent) | this.toString() |
|Primitives| JCPrimitiveTypeTree | primitives (int, long)| 35 |


// todo account for MethodHandle types!

enum {
    JVM_CONSTANT_Utf8                   = 1,
    JVM_CONSTANT_Unicode                = 2, /* unused */
    JVM_CONSTANT_Integer                = 3,
    JVM_CONSTANT_Float                  = 4,
    JVM_CONSTANT_Long                   = 5,
    JVM_CONSTANT_Double                 = 6,
    JVM_CONSTANT_Class                  = 7,
    JVM_CONSTANT_String                 = 8,
    JVM_CONSTANT_Fieldref               = 9,
    JVM_CONSTANT_Methodref              = 10,
    JVM_CONSTANT_InterfaceMethodref     = 11,
    JVM_CONSTANT_NameAndType            = 12,
    JVM_CONSTANT_MethodHandle           = 15,  // JSR 292
    JVM_CONSTANT_MethodType             = 16,   // JSR 292
    JVM_CONSTANT_InvokeDynamic          = 18
};

/* JVM_CONSTANT_MethodHandle subtypes */
enum {
    JVM_REF_getField                = 1,
    JVM_REF_getStatic               = 2,
    JVM_REF_putField                = 3,
    JVM_REF_putStatic               = 4,
    JVM_REF_invokeVirtual           = 5,
    JVM_REF_invokeStatic            = 6,
    JVM_REF_invokeSpecial           = 7,
    JVM_REF_newInvokeSpecial        = 8,
    JVM_REF_invokeInterface         = 9
};
/**
     * Lambda expressions come in two forms: (i) expression lambdas, whose body
     * is an expression, and (ii) statement lambdas, whose body is a block
     */
