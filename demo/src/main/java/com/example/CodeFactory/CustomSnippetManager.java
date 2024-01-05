package com.example.codeFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.lang.model.element.TypeElement;

public class CustomSnippetManager {
    private static final String snippetPath = "src/main/java/snippets/";
    private static final String cataloguePath = "src/main/java/snippets/Catalogue.java";

    public static void saveSnippet(TypeElement element, String pathToClassFile) {

        File snippetsFile = new File(snippetPath + element.getSimpleName() + "Snippet.txt");
        File catalogueFile = new File(cataloguePath);
        try {
            if (catalogueFile.getParentFile().mkdirs()) {
                System.out.println("Package for Custom Templates created: " + catalogueFile.getParentFile());
            }

            if (snippetsFile.createNewFile()) {
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
            }
            String content = processClass(pathToClassFile).replaceAll(element.getSimpleName().toString(),
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
                "\tpublic static enum Options{\n" +
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
        String processed = "";
        processed += content.substring(0, content.indexOf("@Custom"));
        content = content.replaceFirst(processed + "@Custom", "");
        processed += "\n" + content.substring(0, content.indexOf("{") + 1);
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
                    System.out.println("\nDEBUG: open - " + nextOpenBracketPos + "\t close - " + nextCloseBracketPos
                            + "\t bal - " + bracketsBalance);
                    // curPos = Math.min(nextCloseBracketPos, nextOpenBracketPos);
                    if (nextOpenBracketPos == -1 || nextOpenBracketPos > nextCloseBracketPos) {
                        bracketsBalance--;
                        curPos = nextCloseBracketPos;
                    } else {
                        bracketsBalance++;
                        curPos = nextOpenBracketPos;
                    }
                }
                System.out.println("\nCHARAT: " + content.charAt(curPos) + "\n");
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
            extracted = extracted.replaceAll("\\@Custom()", "").replaceAll("\\@Custom", "");
            processed += "\n" + extracted + "\n";
        }
        processed += "}";
        return processed;
    }

}
