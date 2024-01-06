package com.example.codeFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import com.example.annotations.type.Custom;
import com.example.annotations.type.Snippet;

public class CustomSnippetManager {
    private static final String snippetPath = "src/main/java/snippets/";
    private static final String cataloguePath = "src/main/java/snippets/Catalogue.java";


    public static void loadSnippet(TypeElement element, String pathToClassFile){
        String content;
        String classContent;
        try{
            content = new String(Files.readAllBytes(Paths.get(snippetPath + element.getAnnotation(Snippet.class).snippet() + "Snippet.txt")));
        }catch(IOException e){
            System.out.println("Unable to reach Snippet Record");
            return;
        }
        try {
            classContent = new String(Files.readAllBytes(Paths.get(pathToClassFile)));
        } catch (Exception e) {
            System.out.println("Unable to reach class code");
            return;
        }
        String packageName = ((PackageElement)element.getEnclosingElement()).getQualifiedName().toString();
        if(packageName == null || packageName.equals("")){
            content = content.replace("package {pkg};", "");
        }else{
            content = content.replace("{pkg}", packageName);
        }
        String internal = getContent(classContent);
        content = content.replace("\\{internal\\}", internal).replaceAll("\\{class\\}", element.getSimpleName().toString());
        File classFile = new File(pathToClassFile);
        try (PrintWriter writer = new PrintWriter(new FileWriter(classFile, false))) {
            writer.println(content);
        }catch(IOException e){
            System.out.println("Unable to update class file");
            return;
        }
    }

    private static String getContent(String content){
        int startPos = content.indexOf("{")+1;
        int endPos = content.lastIndexOf("}")-1;
        return content.substring(startPos, endPos);
    }

    public static void saveSnippet(TypeElement element, String pathToClassFile) {

        File snippetsFile = new File(snippetPath + element.getSimpleName() + "Snippet.txt");
        File catalogueFile = new File(cataloguePath);
        try {
            if (catalogueFile.getParentFile().mkdirs()) {
                System.out.println("Package for Custom Templates created: " + catalogueFile.getParentFile());
            }

            if (catalogueFile.createNewFile()) {
                System.out.println("Catalogue of saved Custom Templates created: " + catalogueFile.getAbsolutePath());
                writeCatalogue(catalogueFile);
            }
            addOption(element.getSimpleName().toString(), catalogueFile);
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
            }else{
                if(!element.getAnnotation(Custom.class).update()){
                    System.out.println(snippetsFile.getAbsolutePath() + " already exists. Set @Custom(update = true) to update snippet on each build");
                    return;
                }
            }
            String content = "package {pkg};\n" + processClass(pathToClassFile).replaceAll(element.getSimpleName().toString(),
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
                "\tpublic static enum Option{\n" +
                "\t\tNull,\n" +
                "\t}\n" +
                "}";
        PrintWriter writer = new PrintWriter(catalogueFile);
        writer.write(content);
        writer.close();
    }

    private static void addOption(String className, File catalogueFile) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(cataloguePath)));
        if (content.contains(className)) {
            System.out.println(className + " is already in a catalogue.");
            return;
        }
        content = content.replaceFirst("\t\tNull,\n", "\t\t" + className + ",\n\t\tNull,\n");
        try (PrintWriter writer = new PrintWriter(catalogueFile)) {
            writer.println(content);
        }
    }

    private static String processClass(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        content = content.replaceAll("import com.example.annotations.field.CustomField;", "")
                .replaceAll("import com.example.annotations.method.CustomMethod;", "")
                .replaceAll("import com.example.annotations.type.Custom;", "")
                .replaceAll("import com.example.annotations.type.CustomEnum;", "");
        String processed = "";
        int startPos = 0;
        if(content.startsWith("package")){
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
            extracted = extracted.substring(0, extracted.indexOf("@Custom")) + extracted.substring(extracted.indexOf("\n"));
            processed += "\n" + extracted + "\n";
        }
        processed += "\n{internal}\n}";
        return processed;
    }

}
