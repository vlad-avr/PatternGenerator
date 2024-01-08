package com.example.codeFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.Custom;
import com.example.annotations.type.Snippet;

public class CodeCustomiser {
    private static final String snippetPath = "src/main/java/snippets/";
    private static final String cataloguePath = "src/main/java/snippets/Catalogue.java";

    public static void makeConstructors(TypeElement element, String pathToClassFile) {
        Map<Integer, List<VariableElement>> fieldMap = getFieldMap(element);
        if (fieldMap.isEmpty()) {
            System.out.println("No Fields found to make constructor of.");
            return;
        }
        File classFile = new File(pathToClassFile);
        try {
            String originalContent = readAllContent(pathToClassFile);
            int substrPos;
            originalContent = originalContent.substring(0, (substrPos = originalContent.indexOf("@MakeConstructor")))
                    + originalContent.substring(originalContent.indexOf("\n", substrPos));
            String content = getEnclosedContent(originalContent);
            String tabulation = "\t";
            Element enclosing = element;
            while (enclosing != null && !(enclosing instanceof PackageElement)) {
                tabulation += "\t";
                enclosing = enclosing.getEnclosingElement();
            }
            if (content == null) {
                // Should not trigger but better safe then sorry
                throw new Exception("Invalid file layout!");
            }
            String toWrite = "";
            for (int i = 0; i < fieldMap.size(); i++) {
                toWrite += tabulation + " public " + element.getSimpleName() + "(";
                List<VariableElement> fieldsToConstruct = fieldMap.get(i);
                String constructorBody = "{\n";
                for (int j = 0; j < fieldsToConstruct.size(); j++) {
                    VariableElement field = fieldsToConstruct.get(j);
                    if (!field.getModifiers().contains(Modifier.STATIC)) {
                        toWrite += field.asType().toString() + " " + field.getSimpleName();
                        constructorBody += tabulation + "\tthis." + field.getSimpleName() + " = "
                                + field.getSimpleName() + ";\n";
                        if (j != fieldsToConstruct.size() - 1) {
                            toWrite += ", ";
                        }
                    }
                }
                toWrite += ")" + constructorBody + tabulation + "}\n";
            }
            originalContent = originalContent.substring(0, originalContent.lastIndexOf("}")) + toWrite + "}";
            try (PrintWriter writer = new PrintWriter(new FileWriter(classFile, false))) {
                writer.println(originalContent);
            } catch (IOException e) {
                System.out.println("Unable to update class file");
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getEnclosedContent(String content) {
        int startPos = content.indexOf("{");
        int balance = 1;
        if (startPos == -1) {
            return null;
        }
        int curPos = startPos + 1;
        while (balance > 0) {
            int nextOpen = content.indexOf("{", curPos);
            int nextClose = content.indexOf("}", curPos);
            if (nextOpen == -1) {
                curPos = nextClose;
                balance--;
            } else {
                if (nextClose < nextOpen) {
                    if (nextClose == -1) {
                        return null;
                    } else {
                        curPos = nextClose;
                        balance--;
                    }
                } else {
                    curPos = nextOpen;
                    balance++;
                }
            }
            curPos++;
        }
        return content.substring(startPos, curPos + 1);
    }

    public static void loadSnippet(TypeElement element, String pathToClassFile) {
        String content;
        String classContent;
        try {
            content = readAllContent(snippetPath + element.getAnnotation(Snippet.class).snippet() + "Snippet.txt");
        } catch (IOException e) {
            System.out.println("Unable to reach Snippet Record");
            return;
        }
        try {
            classContent = readAllContent(pathToClassFile);
        } catch (Exception e) {
            System.out.println("Unable to reach class code");
            return;
        }
        String packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
        if (packageName == null || packageName.equals("")) {
            content = content.replace("package {pkg};", "");
        } else {
            content = content.replace("{pkg}", packageName);
        }
        String internal = getContent(classContent);
        content = content.replaceAll("\\{internal\\}", internal).replaceAll("\\{class\\}",
                element.getSimpleName().toString());
        File classFile = new File(pathToClassFile);
        try (PrintWriter writer = new PrintWriter(new FileWriter(classFile, false))) {
            writer.println(content);
        } catch (IOException e) {
            System.out.println("Unable to update class file");
            return;
        }
    }

    private static Map<Integer, List<VariableElement>> getFieldMap(TypeElement element) {
        Map<Integer, List<VariableElement>> fieldMap = new HashMap<>();
        List<? extends Element> enclosed = element.getEnclosedElements();
        List<VariableElement> allFields = new ArrayList<>();
        for (Element e : enclosed) {
            if (e.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) e;
                allFields.add(fieldElement);
                ToConstruct annot;
                if ((annot = fieldElement.getAnnotation(ToConstruct.class)) != null) {
                    int[] ids = annot.id();
                    for (int id : ids) {
                        if (fieldMap.containsKey(id)) {
                            if (!fieldMap.get(id).contains(fieldElement)) {
                                fieldMap.get(id).add(fieldElement);
                            }
                        } else {
                            List<VariableElement> fieldList = new ArrayList<>();
                            fieldList.add(fieldElement);
                            fieldMap.put(id, fieldList);
                        }
                    }
                }
            }
        }
        if (fieldMap.isEmpty()) {
            if (allFields.isEmpty()) {
                return fieldMap;
            }
            fieldMap.put(0, allFields);
        }
        return fieldMap;
    }

    private static String getContent(String content) {
        int startPos = content.indexOf("{") + 1;
        int endPos = content.lastIndexOf("}") - 1;
        return content.substring(startPos, endPos);
    }

    public static void saveSnippet(TypeElement element, String pathToClassFile) {
        String snippetName;
        if ((snippetName = element.getAnnotation(Custom.class).name()).equals("-")) {
            snippetName = element.getSimpleName().toString();
        }
        File snippetsFile = new File(snippetPath + snippetName + "Snippet.txt");
        File catalogueFile = new File(cataloguePath);
        try {
            if (catalogueFile.getParentFile().mkdirs()) {
                System.out.println("Package for Custom Templates created: " + catalogueFile.getParentFile());
            }

            if (catalogueFile.createNewFile()) {
                System.out.println("Catalogue of saved Custom Templates created: " + catalogueFile.getAbsolutePath());
                writeCatalogue(catalogueFile);
            }
            addOption(snippetName, catalogueFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        try {
            if (snippetsFile.getParentFile().mkdirs()) {
                System.out.println("Snippets package created: " + snippetsFile.getParentFile());
            }

            if (!snippetsFile.exists()) {
                System.out.println("Snippet file created: " + snippetsFile.getAbsolutePath());
                snippetsFile.createNewFile();
            } else {
                if (!element.getAnnotation(Custom.class).update()) {
                    System.out.println(snippetsFile.getAbsolutePath()
                            + " already exists. Set @Custom(update = true) to update snippet on each build");
                    return;
                }
            }
            String content = "package {pkg};\n"
                    + processClass(pathToClassFile).replaceAll(element.getSimpleName().toString(),
                            "\\{class\\}");
            try (PrintWriter writer = new PrintWriter(new FileWriter(snippetsFile, false))) {
                writer.println(content);
            }
            System.out.println("Content written to the file.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeCatalogue(File catalogueFile) throws FileNotFoundException {
        String content = "package snippets;\n" +
                "\npublic class Catalogue{\n" +
                "\t//Options\n" +
                "}";
        PrintWriter writer = new PrintWriter(catalogueFile);
        writer.write(content);
        writer.close();
    }

    private static String readAllContent(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private static void addOption(String className, File catalogueFile) throws IOException {
        String content = readAllContent(cataloguePath);
        if (content.contains("String " + className)) {
            System.out.println(className + " is already in a catalogue.");
            return;
        }
        content = content.replaceFirst("\t//Options\n",
                "\t//Options\n\t public final static String " + className + " = \"" + className + "\";\n");
        try (PrintWriter writer = new PrintWriter(catalogueFile)) {
            writer.println(content);
        }
    }

    private static String processClass(String path) throws IOException {
        String content = readAllContent(path);
        content = content.replaceAll("import com.example.annotations.field.CustomField;", "")
                .replaceAll("import com.example.annotations.method.CustomMethod;", "")
                .replaceAll("import com.example.annotations.type.Custom;", "")
                .replaceAll("import com.example.annotations.type.CustomEnum;", "");
        String processed = "";
        int startPos = 0;
        if (content.startsWith("package")) {
            startPos = content.indexOf(";") + 1;
        }
        processed += content.substring(startPos, content.indexOf("@Custom"));

        content = content.replaceFirst(processed + "@Custom", "");
        processed += "\n" + content.substring(content.indexOf("\n"), content.indexOf("{") + 1);
        int pos = 0;
        while ((pos = content.indexOf("@CustomField")) != -1) {
            String extracted = content.substring(pos, content.indexOf(";", pos) + 1);
            content = content.replaceFirst("@CustomField", "");
            extracted = extracted.replaceAll("\\@CustomField()", "").replaceAll("\\@CustomField", "");
            processed += "\n" + extracted + "\n";
        }
        while ((pos = content.indexOf("@CustomMethod")) != -1) {
            int extractionStart = Math.min(Math.max(pos, content.indexOf("{", pos) + 1),
                    Math.max(pos, content.indexOf(";", pos) + 1));
            String extracted = content.substring(pos, extractionStart);
            if (!extracted.contains("abstract")) {
                int bracketsBalance = 1;
                int curPos = content.indexOf("{", pos);
                while (bracketsBalance > 0) {
                    int nextOpenBracketPos = content.indexOf("{", curPos + 1);
                    int nextCloseBracketPos = content.indexOf("}", curPos + 1);
                    if (nextOpenBracketPos == -1 || nextOpenBracketPos > nextCloseBracketPos) {
                        bracketsBalance--;
                        curPos = nextCloseBracketPos;
                    } else {
                        bracketsBalance++;
                        curPos = nextOpenBracketPos;
                    }
                }
                extracted += content.substring(extractionStart, curPos + 1);
            }
            content = content.replaceFirst("@CustomMethod", "");
            extracted = extracted.replaceAll("\\@CustomMethod()", "").replaceAll("\\@CustomMethod", "");
            processed += "\n" + extracted + "\n";
        }
        while ((pos = content.indexOf("@CustomEnum")) != -1) {
            String extracted = content.substring(pos, content.indexOf("}", pos) + 1);
            content = content.replaceFirst("@CustomEnum", "");
            extracted = extracted.replaceAll("\\@CustomEnum()", "").replaceAll("\\@CustomEnum", "");
            processed += "\n" + extracted + "\n";
        }
        while ((pos = content.indexOf("@Custom")) != -1) {
            int extractionStart = content.indexOf("{", pos) + 1;
            String extracted = content.substring(pos, extractionStart);
            int bracketsBalance = 1;
            int curPos = content.indexOf("{", pos);
            while (bracketsBalance != 0) {
                int nextOpenBracketPos = content.indexOf("{", curPos + 1);
                int nextCloseBracketPos = content.indexOf("}", curPos + 1);
                if (nextOpenBracketPos == -1 || nextOpenBracketPos > nextCloseBracketPos) {
                    bracketsBalance--;
                    curPos = nextCloseBracketPos;
                } else {
                    bracketsBalance++;
                    curPos = nextOpenBracketPos;
                }
            }
            extracted += content.substring(extractionStart, curPos + 1);
            content = content.replaceFirst("@Custom", "");
            extracted = extracted.substring(0, extracted.indexOf("@Custom"))
                    + extracted.substring(extracted.indexOf("\n"));
            processed += "\n" + extracted + "\n";
        }
        processed += "\n{internal}\n}";
        return processed;
    }

}
