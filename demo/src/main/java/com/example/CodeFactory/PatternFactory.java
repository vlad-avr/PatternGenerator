package com.example.codeFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.example.annotations.field.ToBuild;
import com.example.annotations.type.Builder;
import com.example.annotations.type.Factory;
import com.example.annotations.type.MakeInterface;
import com.example.codeFactory.SnippetLoader.PatternCode;

/** Creates class files according to given data and selected pattern
 * @author vlad-avr
 */
public class PatternFactory {

    /** Generates Interface for Builder pattern 
     * @param clazz         Class from which Builder Interface is generated
     * @param fields        List of fields of the class whick are selected for building with Builder 
     * @param packagePath   Package of the class or "" if class is not in any package
     */
    public static void makeBuilderInterface(TypeElement clazz, List<VariableElement> fields, String packagePath){
        //Generate package name of this class
        String packageName = getPackageName(packagePath, "builders");
        //Make file path from package name
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        //Generate name for new class file
        String name;
        if((name = clazz.getAnnotation(Builder.class).name()).equals("-")){
            name = clazz.getSimpleName() + "BuilderInterface";
        }
        //Make File object
        File file = new File("src/main/java" + filePath + name + ".java");
        try {
            //Make package directories if they don't exist
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }
            //Create new file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                //Load code snippet via SnippetLoader
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.I);
                //Check if snippet loaded successfully
                if (content != null && !content.equals("")) {
                    System.out.println("Interace code snippet loaded successfully!");
                } else {
                    System.out.println("Interface code snippet could not be loaded");
                    return;
                }
                //Replace placeholders with actual values
                content = content.replaceAll("\\{base\\}", name);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                //Generate methods for the interface
                //Make reset method
                String methodStr = "public void reset();\n";
                //Make build method for each marked field
                for(VariableElement field : fields){
                    //Check if the field is marked (annotated with @ToBuild)
                    if(field.getAnnotation(ToBuild.class) == null){
                        continue;
                    }
                    //Get modifiers of the field
                    Set<Modifier> modifiers = field.getModifiers();
                    //If field is static and/or final -> skip it
                    if(modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.FINAL)){
                        continue;
                    }
                    //Make build method
                    String fieldName = field.getSimpleName().toString();
                    fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    methodStr += "\tpublic void set" + fieldName + "(" + field.asType() + " val);\n";
                }
                //Replace placeholder with actual generated methods
                content = content.replaceAll("\\{methods\\}", methodStr);
                //Write content to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(content);
                }
                System.out.println("Content written to the file.");
            } else {
                System.out.println(file.getAbsolutePath() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**Generates Interface from existing class
     * 
     * @param clazz         Class to generate interface from
     * @param methods       Methods of that class
     * @param packagePath   Package of the class or "" if class is not in any package
     */
    public static void makeInterface(TypeElement clazz, List<ExecutableElement> methods, String packagePath){
        //Generate package name of this class
        String packageName = getPackageName(packagePath, "interfaces");
        //Make file path from package name
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        //Generate name for new class file
        String name;
        if((name = clazz.getAnnotation(MakeInterface.class).name()).equals("-")){
            name = clazz.getSimpleName() + "Interface";
        }
        //Make File object
        File file = new File("src/main/java" + filePath + name + ".java");
        try {
            //Make package directories if they don't exist
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }
            //Create new file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                //Load code snippet via SnippetLoader
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.I);
                //Check if snippet loaded successfully
                if (content != null && !content.equals("")) {
                    System.out.println("Interace code snippet loaded successfully!");
                } else {
                    System.out.println("Interface code snippet could not be loaded");
                    return;
                }
                //Replace placeholders with actual values
                content = content.replaceAll("\\{base\\}", name);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                //Generate methods' definitions for interface
                String methodStr = "";
                //Iterate through class methods 
                for (ExecutableElement method : methods) {
                    //Get method modifiers
                    Set<Modifier> modifiers = method.getModifiers();
                    //If method is private, protected or static -> skip it
                    if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.PROTECTED)) {
                        continue;
                    }
                    //Build String for the method definition
                    methodStr += "\n\t";
                    String modString = "";
                    for (Modifier mod : modifiers) {
                        //If method is abstract -> skip its abstract modifier
                        if (!mod.equals(Modifier.ABSTRACT)) {
                            modString += mod.name().toLowerCase() + " ";
                        }
                    }
                    methodStr += modString + method.getReturnType() + " " + method.getSimpleName() + "(";
                    //Get methods parameters as VariableElements
                    List<? extends VariableElement> parameters = method.getParameters();
                    //Iterate through method parameters
                    for (int i = 0; i < parameters.size(); i++) {
                        //Extract parameter metadata and write it to method definition string
                        String parameterName = parameters.get(i).getSimpleName().toString();
                        TypeMirror parameterType = parameters.get(i).asType();
                        methodStr += parameterType + " " + parameterName;
                        if (i != parameters.size() - 1) {
                            methodStr += ", ";
                        }
                    }
                    methodStr += ");";
                }   
                //Replace placeholder with actual definitions
                content = content.replaceAll("\\{methods\\}", methodStr);
                //Write content to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(content);
                }
                System.out.println("Content written to the file.");
            } else {
                System.out.println(file.getAbsolutePath() + " already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param base          Interface to decorate or class to generate child class for
     * @param methods       Methods of that class selected for decorator
     * @param packagePath   Path to class or interface package
     */
    public static void makeDecorator(TypeElement base, List<ExecutableElement> methods, String packagePath) {
        //Generate package name of this class
        String packageName = getPackageName(packagePath, "decorator");
        //Make file path from package name
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        //Make File object
        File file = new File("src/main/java" + filePath + base.getSimpleName() + "Decorator.java");
        //Check if element is an interface or a class
        boolean isInterface = base.getKind().isInterface();
        try {
            //Make package directories if they don't exist
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }
            //Create new file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                //Load code snippet via SnippetLoader
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.D);
                //Check if snippet loaded successfully
                if (content != null && !content.equals("")) {
                    System.out.println("Decorator code snippet loaded successfully!");
                } else {
                    System.out.println("Decorator code snippet could not be loaded");
                    return;
                }
                //Replace placeholders with actual values
                String imports = "import " + base.getQualifiedName() + ";";
                //Replace placeholders with values denending on element type (interface or class)
                if (isInterface) {
                    //If element is an Interface
                    content = content.replaceAll("\\{interface_ref\\}", "protected {base} decorated{base};")
                            .replaceAll("\\{constr_arg\\}", "{base} obj")
                            .replaceAll("\\{constr_body\\}", "this.decorated{base} = obj;")
                            .replaceAll("\\{inheritance\\}", "implements");
                } else {
                    //If element is a Class
                    content = content.replaceAll("\\{interface_ref\\}", "")
                            .replaceAll("\\{constr_arg\\}", "")
                            .replaceAll("\\{constr_body\\}", "super();")
                            .replaceAll("\\{inheritance\\}", "extends");
                }
                //Replace common placeholders
                content = content.replaceAll("\\{base\\}", base.getSimpleName().toString())
                        .replaceAll("\\{decor\\}", base.getSimpleName() + "Decorator")
                        .replaceAll("\\{imports\\}", imports);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                //Generate overrides
                String overrides = "";
                //Iterate through marked methods
                for (ExecutableElement method : methods) {
                    //Get method modifiers
                    Set<Modifier> modifiers = method.getModifiers();
                    //Check if method is valid
                    if (!isInterface && (modifiers.contains(Modifier.PRIVATE)
                            || (modifiers.contains(Modifier.STATIC) && modifiers.contains(Modifier.PROTECTED)))) {
                        continue;
                    }
                    //Do no use @Override if method is Static
                    if (!modifiers.contains(Modifier.STATIC)) {
                        overrides += "\t@Override";
                    }
                    //Build method string
                    overrides += "\n\t";
                    String modString = "";
                    for (Modifier mod : modifiers) {
                        //Do not write absract modifier
                        if (!mod.equals(Modifier.ABSTRACT)) {
                            modString += mod.name().toLowerCase() + " ";
                        }
                    }
                    overrides += modString + method.getReturnType() + " " + method.getSimpleName() + "(";
                    String paramList = "";
                    //Get parameters
                    List<? extends VariableElement> parameters = method.getParameters();
                    for (int i = 0; i < parameters.size(); i++) {
                        //Write parameters metadata to parameters list string
                        String parameterName = parameters.get(i).getSimpleName().toString();
                        paramList += parameterName;
                        TypeMirror parameterType = parameters.get(i).asType();
                        overrides += parameterType + " " + parameterName;
                        if (i != parameters.size() - 1) {
                            overrides += ", ";
                            paramList += ", ";
                        }
                    }
                    overrides += "){\n\t\t";
                    //Do not add return statements if the method returns nothing
                    if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
                        overrides += "return ";
                    }
                    //Write method body according to enclosing element type and method modifiers
                    if (isInterface) {
                        if (!modifiers.contains(Modifier.STATIC)) {
                            overrides += "decorated";
                        }
                        overrides += base.getSimpleName() + ".";
                    } else {
                        if (modifiers.contains(Modifier.STATIC)) {
                            overrides += base.getSimpleName() + ".";
                        } else {
                            overrides += "super.";
                        }
                    }
                    //Add method string to overrides string
                    overrides += method.getSimpleName() + "(" + paramList + ");\n\t}\n\n";
                }
                //Replace overrides placeholder with actual data
                content = content.replaceAll("\\{overrides\\}", overrides);
                //Write content to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(content);
                }

                System.out.println("Content written to the file.");
            } else {
                System.out.println(file.getAbsolutePath() + " already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     * @param clazz         Class to put in Singleton
     * @param threadSafe    Include thread safety in Singleton pattern
     * @param packagePath   Path to class package
     */
    public static void makeSingleton(TypeElement clazz, boolean threadSafe, String packagePath) {
        //Generate package name of this class
        String packageName = getPackageName(packagePath, "singleton");
        //Make file path from package name
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        //Make File object
        File file = new File("src/main/java" + filePath + clazz.getSimpleName() + "Singleton.java");
        try {
            //Make package directories if they don't exist
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }
            //Create new file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                //Load code snippet via SnippetLoader
                String content;
                //Selected which snippet to load according to thread safety seletcion
                if (threadSafe) {
                    content = SnippetLoader.loadPatternSnippet(PatternCode.SC);
                } else {
                    content = SnippetLoader.loadPatternSnippet(PatternCode.STS);
                }
                //Check if snippet loaded successfully
                if (content != null && !content.equals("")) {
                    System.out.println("Singleton code snippet loaded successfully!");
                } else {
                    System.out.println("Singleton code snippet could not be loaded");
                    return;
                }
                //Replace placeholders with actual values
                content = content.replaceAll("\\{class\\}", clazz.getSimpleName().toString())
                        .replaceAll("\\{singleton\\}", clazz.getSimpleName() + "Singleton");
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                //Handle imports
                String imports = "import " + clazz.getQualifiedName() + ";";
                content = content.replaceAll("\\{imports\\}", imports);
                //Write content to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(content);
                }

                System.out.println("Content written to the file.");
            } else {
                System.out.println(file.getAbsolutePath() + " already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     * @param parent    Parent class (return type of the Factory)
     * @param children  Child Classes (options to produce in the Factory)
     */
    public static void makeFactory(TypeElement parent, List<TypeElement> children) {
        //Check if there are any child classes
        if (children == null || children.size() == 0) {
            return;
        }
        //Generate package name of this class
        String packagePath = "-";
        for (TypeElement element : children) {
            String tmp = element.getAnnotation(Factory.class).pkg();
            if (!tmp.equals(packagePath) && !tmp.equals("-")) {
                packagePath = tmp;
            }
        }
        String packageName = getPackageName(packagePath, "factory");
        //Make file path from package name
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        //Make File object
        File file = new File("src/main/java" + filePath + parent.getSimpleName() + "Factory.java");
        try {
            //Make package directories if they don't exist
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }
            //Create new file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                //Load code snippet via SnippetLoader
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.F);
                //Check if snippet loaded successfully
                if (content != null && !content.equals("")) {
                    System.out.println("Factory code snippet loaded successfully!");
                } else {
                    System.out.println("Factory code snippet could not be loaded");
                    return;
                }
                //Generate Enum with options to select what to make in the Factory
                List<String> options = new ArrayList<>();
                String enumOptions = "";
                for (TypeElement c : children) {
                    String str;
                    if ((str = c.getAnnotation(Factory.class).option()).equals("-")) {
                        str = c.getSimpleName().toString();
                    }
                    options.add(str.toUpperCase());
                    enumOptions += "\t\t" + str.toUpperCase() + ",\n";
                }
                //Replace placeholders with actual values
                content = content.replaceAll("\\{factory\\}", parent.getSimpleName() + "Factory")
                        .replaceAll("\\{options\\}", enumOptions)
                        .replaceAll("\\{parent\\}", parent.getSimpleName().toString());
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                //Generate switch_case statement
                String cases = "";
                for (int i = 0; i < children.size(); i++) {
                    cases += "\t\t\tcase " + options.get(i) + ":\n\t\t\t\treturn new "
                            + children.get(i).getSimpleName() + "();\n";
                }
                content = content.replaceAll("\\{case\\}", cases);
                //Handle imports
                String imports = "import " + parent.getQualifiedName() + ";";
                for (TypeElement child : children) {
                    imports += "\nimport " + child.getQualifiedName() + ";";
                }
                content = content.replaceAll("\\{imports\\}", imports);
                //Write content to the file
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println(content);
                }
                System.out.println("Content written to the file.");
            } else {
                System.out.println(file.getAbsolutePath() + " already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     * @param packagePath   Path to Package
     * @param defaultPkg    Default package if no package is given
     * @return              Qualified name of the generated package name
     */
    private static String getPackageName(String packagePath, String defaultPkg) {
        String packageName = "";
        //Check if user provided custom package
        if (packagePath == null || packagePath.equals("-")) {
            //If no pckage provided -> use default
            packageName = "pattern." + defaultPkg;
        } else {
            packageName = packagePath;
        }
        return packageName;
    }

}
