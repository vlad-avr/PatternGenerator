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

public class PatternFactory {

    public static void makeBuilderInterface(TypeElement clazz, List<VariableElement> fields, String packagePath){
        String packageName = getPackageName(packagePath, "builders");
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        String name;
        if((name = clazz.getAnnotation(Builder.class).name()).equals("-")){
            name = clazz.getSimpleName() + "BuilderInterface";
        }
        File file = new File("src/main/java" + filePath + name + ".java");
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.I);
                if (content != null && !content.equals("")) {
                    System.out.println("Interace code snippet loaded successfully!");
                } else {
                    System.out.println("Interface code snippet could not be loaded");
                    return;
                }
                // System.out.println("\nDEBUG : \n" + content + "\n");
                content = content.replaceAll("\\{base\\}", name);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                String methodStr = "public void reset();\n";
                for(VariableElement field : fields){
                    if(field.getAnnotation(ToBuild.class) == null){
                        continue;
                    }
                    Set<Modifier> modifiers = field.getModifiers();
                    if(modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.FINAL)){
                        continue;
                    }
                    String fieldName = field.getSimpleName().toString();
                    fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    methodStr += "\tpublic void set" + fieldName + "(" + field.asType() + " val);\n";
                }
                content = content.replaceAll("\\{methods\\}", methodStr);

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

    public static void makeInterface(TypeElement clazz, List<ExecutableElement> methods, String packagePath){
        String packageName = getPackageName(packagePath, "interfaces");
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        String name;
        if((name = clazz.getAnnotation(MakeInterface.class).name()).equals("-")){
            name = clazz.getSimpleName() + "Interface";
        }
        File file = new File("src/main/java" + filePath + name + ".java");
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.I);
                if (content != null && !content.equals("")) {
                    System.out.println("Interace code snippet loaded successfully!");
                } else {
                    System.out.println("Interface code snippet could not be loaded");
                    return;
                }
                // System.out.println("\nDEBUG : \n" + content + "\n");
                content = content.replaceAll("\\{base\\}", name);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                String methodStr = "";
                for (ExecutableElement method : methods) {
                    Set<Modifier> modifiers = method.getModifiers();
                    if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.PROTECTED)) {
                        continue;
                    }
                    methodStr += "\n\t";
                    String modString = "";
                    for (Modifier mod : modifiers) {
                        if (!mod.equals(Modifier.ABSTRACT)) {
                            modString += mod.name().toLowerCase() + " ";
                        }
                    }
                    methodStr += modString + method.getReturnType() + " " + method.getSimpleName() + "(";
                    List<? extends VariableElement> parameters = method.getParameters();
                    for (int i = 0; i < parameters.size(); i++) {
                        String parameterName = parameters.get(i).getSimpleName().toString();
                        TypeMirror parameterType = parameters.get(i).asType();
                        methodStr += parameterType + " " + parameterName;
                        if (i != parameters.size() - 1) {
                            methodStr += ", ";
                        }
                    }
                    methodStr += ");";
                }

                content = content.replaceAll("\\{methods\\}", methodStr);

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

    public static void makeDecorator(TypeElement base, List<ExecutableElement> methods, String packagePath) {
        String packageName = getPackageName(packagePath, "decorator");
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        File file = new File("src/main/java" + filePath + base.getSimpleName() + "Decorator.java");
        boolean isInterface = base.getKind().isInterface();
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.D);
                if (content != null && !content.equals("")) {
                    System.out.println("Decorator code snippet loaded successfully!");
                } else {
                    System.out.println("Decorator code snippet could not be loaded");
                    return;
                }
                String imports = "import " + base.getQualifiedName() + ";";
                if (isInterface) {
                    content = content.replaceAll("\\{interface_ref\\}", "protected {base} decorated{base};")
                            .replaceAll("\\{constr_arg\\}", "{base} obj")
                            .replaceAll("\\{constr_body\\}", "this.decorated{base} = obj;")
                            .replaceAll("\\{inheritance\\}", "implements");
                } else {
                    content = content.replaceAll("\\{interface_ref\\}", "")
                            .replaceAll("\\{constr_arg\\}", "")
                            .replaceAll("\\{constr_body\\}", "super();")
                            .replaceAll("\\{inheritance\\}", "extends");
                }
                content = content.replaceAll("\\{base\\}", base.getSimpleName().toString())
                        .replaceAll("\\{decor\\}", base.getSimpleName() + "Decorator")
                        .replaceAll("\\{imports\\}", imports);
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                String overrides = "";
                for (ExecutableElement method : methods) {
                    Set<Modifier> modifiers = method.getModifiers();
                    if (!isInterface && (modifiers.contains(Modifier.PRIVATE)
                            || (modifiers.contains(Modifier.STATIC) && modifiers.contains(Modifier.PROTECTED)))) {
                        continue;
                    }
                    if (!modifiers.contains(Modifier.STATIC)) {
                        overrides += "\t@Override";
                    }
                    overrides += "\n\t";
                    String modString = "";
                    for (Modifier mod : modifiers) {
                        if (!mod.equals(Modifier.ABSTRACT)) {
                            modString += mod.name().toLowerCase() + " ";
                        }
                    }
                    overrides += modString + method.getReturnType() + " " + method.getSimpleName() + "(";
                    String paramList = "";
                    List<? extends VariableElement> parameters = method.getParameters();
                    for (int i = 0; i < parameters.size(); i++) {
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
                    if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
                        overrides += "return ";
                    }
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

                    overrides += method.getSimpleName() + "(" + paramList + ");\n\t}\n\n";
                }

                content = content.replaceAll("\\{overrides\\}", overrides);

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

    public static void makeSingleton(TypeElement clazz, boolean threadSafe, String packagePath) {
        String packageName = getPackageName(packagePath, "singleton");
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        File file = new File("src/main/java" + filePath + clazz.getSimpleName() + "Singleton.java");
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                if (threadSafe) {
                    content = SnippetLoader.loadPatternSnippet(PatternCode.SC);
                } else {
                    content = SnippetLoader.loadPatternSnippet(PatternCode.STS);
                }
                if (content != null && !content.equals("")) {
                    System.out.println("Singleton code snippet loaded successfully!");
                } else {
                    System.out.println("Singleton code snippet could not be loaded");
                    return;
                }
                content = content.replaceAll("\\{class\\}", clazz.getSimpleName().toString())
                        .replaceAll("\\{singleton\\}", clazz.getSimpleName() + "Singleton");
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                String imports = "import " + clazz.getQualifiedName() + ";";
                content = content.replaceAll("\\{imports\\}", imports);
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

    public static void makeFactory(TypeElement parent, List<TypeElement> children) {
        if (children == null || children.size() == 0) {
            return;
        }
        String packagePath = "-";
        for (TypeElement element : children) {
            String tmp = element.getAnnotation(Factory.class).pkg();
            if (!tmp.equals(packagePath) && !tmp.equals("-")) {
                packagePath = tmp;
            }
        }
        String packageName = getPackageName(packagePath, "factory");
        String filePath = "/";
        if (!packagePath.equals("")) {
            filePath += packageName.replace(".", "/") + "/";
        }
        File file = new File("src/main/java" + filePath + parent.getSimpleName() + "Factory.java");
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.F);
                if (content != null && !content.equals("")) {
                    System.out.println("Factory code snippet loaded successfully!");
                } else {
                    System.out.println("Factory code snippet could not be loaded");
                    return;
                }
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
                content = content.replaceAll("\\{factory\\}", parent.getSimpleName() + "Factory")
                        .replaceAll("\\{options\\}", enumOptions)
                        .replaceAll("\\{parent\\}", parent.getSimpleName().toString());
                if (packagePath.equals("")) {
                    content = content.replaceAll("\\{path\\};", "");
                } else {
                    content = content.replaceAll("\\{path\\}", "package " + packageName);
                }
                String cases = "";
                for (int i = 0; i < children.size(); i++) {
                    cases += "\t\t\tcase " + options.get(i) + ":\n\t\t\t\treturn new "
                            + children.get(i).getSimpleName() + "();\n";
                }
                content = content.replaceAll("\\{case\\}", cases);
                String imports = "import " + parent.getQualifiedName() + ";";
                for (TypeElement child : children) {
                    imports += "\nimport " + child.getQualifiedName() + ";";
                }
                content = content.replaceAll("\\{imports\\}", imports);

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

    private static String getPackageName(String packagePath, String defaultPkg) {
        String packageName = "";
        // System.out.println("\n\nDEBUG : " + packagePath);
        if (packagePath == null || packagePath.equals("-")) {
            packageName = "pattern." + defaultPkg;
        } else {
            packageName = packagePath;
        }
        return packageName;
    }

}
