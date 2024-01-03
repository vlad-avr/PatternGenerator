package com.example.codeFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import com.example.codeFactory.SnippetLoader.PatternCode;

public class PatternFactory {

    public static void makeSingleton(TypeElement clazz, boolean threadSafe) {
        PackageElement pkg = (PackageElement) clazz.getEnclosingElement();
        String packageName = pkg.getQualifiedName().toString();
        String filePath = packageName.replace('.', '/');
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
        if(children == null || children.size() == 0){
            return;
        }
        PackageElement pkg = (PackageElement) children.get(0).getEnclosingElement();
        String packageName = pkg.getQualifiedName().toString();
        String filePath = packageName.replace('.', '/');
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
