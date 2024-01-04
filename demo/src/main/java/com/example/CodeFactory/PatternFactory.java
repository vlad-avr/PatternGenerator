package com.example.codeFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.example.codeFactory.SnippetLoader.PatternCode;

public class PatternFactory {

    public static void makeDecorator(TypeElement base, List<ExecutableElement> methods, String packagePath) {
        String packageName = "";
        String filePath = "";
        if (packagePath.equals("-")) {
            PackageElement pkg = (PackageElement) base.getEnclosingElement();
            packageName = pkg.getQualifiedName().toString();
        } else {
            packageName = packagePath;
        }
        filePath = packageName.replace(".", "/");
        File file = new File("src/main/java/" + filePath + "/" + base.getSimpleName() + "Decorator.java");
        try {
            if (file.getParentFile().mkdirs()) {
                System.out.println("Directory created: " + file.getParentFile());
            }

            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
                String content;
                content = SnippetLoader.loadPatternSnippet(PatternCode.D);
                if (content != null && !content.equals("")) {
                    System.out.println("Singleton code snippet loaded successfully!");
                } else {
                    System.out.println("Singleton code snippet could not be loaded");
                    return;
                }
                String imports = "import " + base.getQualifiedName() + ";";
                content = content.replaceAll("\\{base\\}", base.getSimpleName().toString())
                        .replaceAll("\\{decor\\}", base.getSimpleName() + "Decorator")
                        .replaceAll("\\{path\\}", "package " + packageName).replaceAll("\\{imports\\}", imports);
                String overrides = "";
                for (ExecutableElement method : methods) {
                    Set<Modifier> modifiers = method.getModifiers();
                    if (modifiers.contains(Modifier.STATIC)) {
                        continue;
                    } 
                    overrides += "\t@Override\n\t";
                    String modString = "";
                    for (Modifier mod : modifiers) {
                        modString += mod.name().toLowerCase() + " ";
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

                    if (modifiers.contains(Modifier.PROTECTED) || modifiers.contains(Modifier.PRIVATE)) {
                        overrides += "super.";
                    } else {
                        overrides += "decorated" + base.getSimpleName() + ".";
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
        String packageName = "";
        String filePath = "";
        if (packagePath.equals("-")) {
            PackageElement pkg = (PackageElement) clazz.getEnclosingElement();
            packageName = pkg.getQualifiedName().toString();
        } else {
            packageName = packagePath;
        }
        filePath = packageName.replace(".", "/");
        File file = new File("src/main/java/" + filePath + "/" + clazz.getSimpleName() + "Singleton.java");
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
                        .replaceAll("\\{singleton\\}", clazz.getSimpleName() + "Singleton")
                        .replaceAll("\\{path\\}", "package " + packageName);
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

    public static void makeFactory(TypeElement parent, List<TypeElement> children, String packagePath) {
        if (children == null || children.size() == 0) {
            return;
        }
        String packageName = "";
        String filePath = "";
        if (packagePath.equals("-")) {
            PackageElement pkg = (PackageElement) children.get(0).getEnclosingElement();
            packageName = pkg.getQualifiedName().toString();
        } else {
            packageName = packagePath;
        }
        filePath = packageName.replace(".", "/");
        File file = new File("src/main/java/" + filePath + "/" + parent.getSimpleName() + "Factory.java");
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
                    String str = c.getSimpleName().toString();
                    options.add(str.toUpperCase());
                    enumOptions += "\t\t" + str.toUpperCase() + ",\n";
                }
                content = content.replaceAll("\\{factory\\}", parent.getSimpleName() + "Factory")
                        .replaceAll("\\{options\\}", enumOptions)
                        .replaceAll("\\{parent\\}", parent.getSimpleName().toString())
                        .replaceAll("\\{path\\}", "package " + packageName);

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

}
