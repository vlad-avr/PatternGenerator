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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.example.annotations.field.GetterSetter;
import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.Custom;
import com.example.annotations.type.Snippet;

/**
 * Manages existing class files, updates and saves custom code snippet adding
 * them to snippet catalogue
 * 
 * @author vlad-avr
 */
public class CodeCustomiser {
    // Path to snippets package
    private static final String snippetPath = "src/main/java/snippets/";
    // Path to generated java file with snippets catalogue
    private static final String cataloguePath = "src/main/java/snippets/Catalogue.java";

    /**
     * Generates equals function for the class
     * 
     * @param element         class to generate equals for
     * @param fields          fields of this class
     * @param methods         methods of this class
     * @param pathToClassFile path to class file
     */
    public static void makeEquals(TypeElement element, List<VariableElement> fields, List<ExecutableElement> methods,
            String pathToClassFile) {
        // Create File instance
        File classFile = new File(pathToClassFile);
        try {
            // Check if equals method already exists
            for (ExecutableElement method : methods) {
                if (method.getSimpleName().toString().equals("equals")) {
                    return;
                }
            }
            // Read contents of the class file
            String originalContent = readAllContent(pathToClassFile);
            // Manage tabulation
            String tabulation = "";
            Element enclosing = element;
            // For each enclosing element add 1 layer of tabulation
            while (enclosing != null && !(enclosing instanceof PackageElement)) {
                tabulation += "\t";
                enclosing = enclosing.getEnclosingElement();
            }
            // Build contnent for new method
            String toWrite = "\n" + tabulation + "public boolean equals(Object o){\n" + tabulation
                    + "\tif (this == o) return true;\n" + tabulation + "\tif (o == null) return false;\n" + tabulation
                    + "\tif (getClass() != o.getClass()) return false;\n" + tabulation
                    + "\t" + element.getSimpleName() + " other = (" + element.getSimpleName() + ")o;\n" + tabulation
                    + "\treturn";
            // Add equals statement for each field
            for (int i = 0; i < fields.size(); i++) {
                VariableElement field = fields.get(i);
                toWrite += " java.util.Objects.equals(" + "this." + field.getSimpleName() + ", other." + field.getSimpleName()
                        + ")";
                if (i < fields.size() - 1) {
                    toWrite += " && \n" + tabulation + "\t\t";
                } else {
                    toWrite += ";\n";
                }
            }
            toWrite += tabulation + "}";
            // Look for place in file original content to insert new method
            int pos = 0;
            do {
                pos = originalContent.indexOf(element.getSimpleName().toString(), pos);
            } while (pos != -1 && originalContent.charAt(pos - 1) != ' ');
            if (pos == -1) {
                // No suitable place was found
                throw new Exception("Unable to find place where to write new content");
            }
            // Look for the opening semicolumn of the class
            pos = originalContent.indexOf("{", pos);
            // Insert new methods right after it
            originalContent = originalContent.substring(0, pos + 1) + toWrite + originalContent.substring(pos + 1);
            // Update the contents of existing file
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

    /**
     * Generates ToString function for the class
     * 
     * @param element         Class for which method is generated
     * @param fields          List of fields of this class
     * @param pathToClassFile Path to class file
     */
    public static void makeToString(TypeElement element, List<VariableElement> fields, List<ExecutableElement> methods,
            String pathToClassFile) {
        // Create File instance
        File classFile = new File(pathToClassFile);
        // Check if there is no fields to make toString for
        if (fields.isEmpty()) {
            return;
        }
        try {
            // Check if toString method already exists
            for (ExecutableElement method : methods) {
                if (method.getSimpleName().toString().equals("toString")) {
                    return;
                }
            }
            // Read contents of the class file
            String originalContent = readAllContent(pathToClassFile);
            // Manage tabulation
            String tabulation = "";
            Element enclosing = element;
            // For each enclosing element add 1 layer of tabulation
            while (enclosing != null && !(enclosing instanceof PackageElement)) {
                tabulation += "\t";
                enclosing = enclosing.getEnclosingElement();
            }
            // Create String with ToString method
            String toWrite = "\n" + tabulation + "public String toString(){\n" + tabulation + "\treturn \"[";
            // Iterate through fields
            for (int i = 0; i < fields.size(); i++) {
                VariableElement field = fields.get(i);
                // If marked field is Static -> skip it
                if (field.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }
                // Build toString return value
                toWrite += field.getSimpleName() + " : \" + " + "this." + field.getSimpleName();
                if (i != fields.size() - 1) {
                    toWrite += " + \"\\n";
                }
            }
            toWrite += " + \"]\";" + "\n" + tabulation + "}";
            // Look for place in file original content to insert new method
            int pos = 0;
            do {
                pos = originalContent.indexOf(element.getSimpleName().toString(), pos);
            } while (pos != -1 && originalContent.charAt(pos - 1) != ' ');
            if (pos == -1) {
                // No suitable place was found
                throw new Exception("Unable to find place where to write new content");
            }
            // Look for the opening semicolumn of the class
            pos = originalContent.indexOf("{", pos);
            // Insert new methods right after it
            originalContent = originalContent.substring(0, pos + 1) + toWrite + originalContent.substring(pos + 1);
            // Update the contents of existing file
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

    /**
     * Generates getter and/or setter functions for selected fields of the class
     * 
     * @param element         Parent class of the fields
     * @param fields          Marked fields of the class
     * @param methods         Methods of the class (used to avoid duplication)
     * @param pathToClassFile Path to class for reading its content
     */
    public static void makeGetterSetters(TypeElement element, List<VariableElement> fields,
            List<ExecutableElement> methods, String pathToClassFile) {
        // Create File instance
        File classFile = new File(pathToClassFile);
        // Check if there is no fields to make getters setters for
        if (fields.isEmpty()) {
            return;
        }
        try {
            // Read contents of the class file
            String originalContent = readAllContent(pathToClassFile);
            // Manage tabulation
            String tabulation = "";
            Element enclosing = element;
            // For each enclosing element add 1 layer of tabulation
            while (enclosing != null && !(enclosing instanceof PackageElement)) {
                tabulation += "\t";
                enclosing = enclosing.getEnclosingElement();
            }
            // Create String with getters and setters
            String toWrite = "\n";
            // Iterate through fields
            for (VariableElement field : fields) {
                // If marked field is Static -> skip it
                if (field.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }
                // Decide whether to generate only getter, only setter, both or neither getter
                // nor setter
                boolean makeGetter = true;
                boolean makeSetter = true;
                // Extract @GetterSetter annotation from the field if it has it
                GetterSetter gsAnnot;
                if ((gsAnnot = field.getAnnotation(GetterSetter.class)) != null) {
                    // If field does -> update checker values according to it
                    makeGetter = gsAnnot.makeGetter();
                    makeSetter = gsAnnot.makeSetter();
                }
                // If either getter or setter is to be generated -> look for already defined
                // methods with same name and parameters
                if (makeGetter || makeSetter) {
                    for (ExecutableElement method : methods) {
                        // If there is at least one -> mark makeGetter or/and makeSetter as false to
                        // avoid duplication of methods
                        if (method.getSimpleName().toString()
                                .equals("get" + field.getSimpleName().toString().substring(0, 1).toUpperCase()
                                        + field.getSimpleName().toString().substring(1))) {
                            makeGetter = false;
                        }
                        if (method.getSimpleName().toString()
                                .equals("set" + field.getSimpleName().toString().substring(0, 1).toUpperCase()
                                        + field.getSimpleName().toString().substring(1))) {
                            makeSetter = false;
                        }
                    }
                }
                // If field is final -> do not make setter for it
                if (field.getModifiers().contains(Modifier.FINAL) && makeSetter) {
                    makeSetter = false;
                }
                // Generate getter if makeGetter is true
                if (makeGetter) {
                    toWrite += tabulation + "public " + field.asType().toString() + " get"
                            + field.getSimpleName().toString().substring(0, 1).toUpperCase()
                            + field.getSimpleName().toString().substring(1)
                            + "(){\n" + tabulation + "\treturn this." + field.getSimpleName() + ";\n" + tabulation
                            + "}\n";
                }
                // Generate setter if makeSetter is true
                if (makeSetter) {
                    toWrite += tabulation + "public void set"
                            + field.getSimpleName().toString().substring(0, 1).toUpperCase()
                            + field.getSimpleName().toString().substring(1)
                            + "(" + field.asType().toString() + " " + field.getSimpleName() + "){\n" + tabulation
                            + "\tthis." + field.getSimpleName() + " = " + field.getSimpleName() + ";\n" + tabulation
                            + "}\n";
                }
            }
            // If there is nothing to write -> don't write just return
            if (toWrite.equals("\n")) {
                return;
            }
            // Look for place in file original content to insert new methods
            int pos = 0;
            do {
                pos = originalContent.indexOf(element.getSimpleName().toString(), pos);
            } while (pos != -1 && originalContent.charAt(pos - 1) != ' ');
            if (pos == -1) {
                // No suitable place was found
                throw new Exception("Unable to find place where to write new content");
            }
            // Look for the opening semicolumn of the class
            pos = originalContent.indexOf("{", pos);
            // Insert new methods right after it
            originalContent = originalContent.substring(0, pos + 1) + toWrite + originalContent.substring(pos + 1);
            // Update the contents of existing file
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

    /**
     * Generates constructors for selected fields in the class marked
     * wtih @MakeConstructor
     * 
     * @param element         Class to generate constructors for
     * @param pathToClassFile Path to class file
     */
    public static void makeConstructors(TypeElement element, String pathToClassFile) {
        // Map out fields and constructor ids
        Map<Integer, List<VariableElement>> fieldMap = getFieldMap(element);
        // Return if there are no fields to make constructor from
        if (fieldMap.isEmpty()) {
            System.out.println("No Fields found to make constructor of.");
            return;
        }
        // Create File instance
        File classFile = new File(pathToClassFile);
        try {
            // Read original content of the file
            String originalContent = readAllContent(pathToClassFile);
            // Clear @MakeConstructor annotation from the class to avoid constructor
            // duplication on each build
            int substrStart = 0;
            int substrEnd;
            String tmp;
            do {
                // Find annotation
                substrStart = originalContent.indexOf("@MakeConstructor", substrStart);
                if (substrStart == -1) {
                    throw new Exception("Invalid code structure");
                }
                // Find code block start
                substrEnd = originalContent.indexOf("{", substrStart);
                if (substrEnd == -1) {
                    throw new Exception("Invalid code structure");
                }
                // Get content inbetween those indeces
                tmp = originalContent.substring(substrStart, substrEnd + 1);
                // If it is the class we are looking for -> stop
            } while (!(tmp.contains("@MakeConstructor") && tmp.contains("class")
                    && tmp.contains(element.getSimpleName())));
            // Remove @MakeConstructor annotation
            int cutLen;
            originalContent = originalContent.substring(0, substrStart)
                    + originalContent.substring((cutLen = originalContent.indexOf("\n", substrStart)));
            // Calculate offset after removing chunk of content
            cutLen -= substrStart;
            // Manage tabulation
            String tabulation = "";
            Element enclosing = element;
            // For each enclosing elemet add 1 layer of tabulation
            while (enclosing != null && !(enclosing instanceof PackageElement)) {
                tabulation += "\t";
                enclosing = enclosing.getEnclosingElement();
            }
            // Generate constructors String
            String toWrite = "\n";
            // Iterate throught keys in fields map
            for (Integer i : fieldMap.keySet()) {
                // Create constructor for each key in the map
                toWrite += tabulation + "public " + element.getSimpleName() + "(";
                List<VariableElement> fieldsToConstruct = fieldMap.get(i);
                // Generate constructor body
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
            // Insert generated code into original file content
            originalContent = originalContent.substring(0, substrEnd - cutLen + 1) + toWrite
                    + originalContent.substring(substrEnd - cutLen + 1);
            // Write update content to the file
            try (PrintWriter writer = new PrintWriter(new FileWriter(classFile, false))) {
                writer.println(originalContent);
            } catch (IOException e) {
                System.out.println("Unable to update class file");
                return;
            }
            System.out.println("Content written to the file.");
        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
        }
    }

    /**
     * Use to extract body of a single code block (inbetween semiclumns)
     * 
     * @deprecated
     * @param content Content of the file
     * @return Body of the class
     */
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

    /**
     * Load Custom Code Snippet into existing class updating its content
     * 
     * @param element         Class to load snippet into
     * @param pathToClassFile Path to class content
     */
    public static void loadSnippet(TypeElement element, String pathToClassFile) {
        String content;
        String classContent;
        // Load Code Snippet content
        try {
            content = readAllContent(snippetPath + element.getAnnotation(Snippet.class).snippet() + "Snippet.txt");
        } catch (IOException e) {
            System.out.println("Unable to reach Snippet Record");
            return;
        }
        // Load class content
        try {
            classContent = readAllContent(pathToClassFile);
        } catch (Exception e) {
            System.out.println("Unable to reach class code");
            return;
        }
        // Get class package
        String packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
        if (packageName == null || packageName.equals("")) {
            // Remove package statement if class is not in any package
            content = content.replace("package {pkg};", "");
        } else {
            content = content.replace("{pkg}", packageName);
        }
        // Get content of the class from the file
        String internal = getContent(classContent);
        // Replace Snippet internal placeholder with content of the class
        content = content.replaceAll("\\{internal\\}", internal).replaceAll("\\{class\\}",
                element.getSimpleName().toString());
        // Create File instance
        File classFile = new File(pathToClassFile);
        // Update class file
        try (PrintWriter writer = new PrintWriter(new FileWriter(classFile, false))) {
            writer.println(content);
        } catch (IOException e) {
            System.out.println("Unable to update class file");
            return;
        }
        System.out.println("Content written to the file.");
    }

    /**
     * Map out fields according to the constructor ids (fields annotated with same
     * constructor ids will be Initialized in the same constructors)
     * 
     * @param element Class that we need to map out
     * @return Map of fields according to constructor ids
     */
    private static Map<Integer, List<VariableElement>> getFieldMap(TypeElement element) {
        // Initialize map
        Map<Integer, List<VariableElement>> fieldMap = new HashMap<>();
        // Get enclosed elements of the class
        List<? extends Element> enclosed = element.getEnclosedElements();
        // Initialize list of fields
        List<VariableElement> allFields = new ArrayList<>();
        // Iterate throught list of enclosed elements
        for (Element e : enclosed) {
            // If enclosed element is a field -> map it out!
            if (e.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) e;
                // Add to the list of fields
                allFields.add(fieldElement);
                // Extract @ToConstruct annotation if there is one
                ToConstruct annot;
                if ((annot = fieldElement.getAnnotation(ToConstruct.class)) != null) {
                    // If there is -> get constructor id values (there can be more than 1)
                    int[] ids = annot.id();
                    // Iterate through extracted ids
                    for (int id : ids) {
                        // If this id is already mapped -> add field to the list of fields that will be
                        // Initialized in that constructor
                        if (fieldMap.containsKey(id)) {
                            // Check whether list of parameters contains this field to avoid duplication
                            if (!fieldMap.get(id).contains(fieldElement)) {
                                fieldMap.get(id).add(fieldElement);
                            }
                        } else {
                            // If the key is not in the map -> create new list of fields and add new pair
                            // into the map
                            List<VariableElement> fieldList = new ArrayList<>();
                            // Add current field to new list
                            fieldList.add(fieldElement);
                            fieldMap.put(id, fieldList);
                        }
                    }
                }
            }
        }
        // If there are no fields marked for construction but the class is annotated
        // with @MakeConstructor -> all fields are to be Initialized in a single
        // constructor
        if (fieldMap.isEmpty()) {
            // If there are no fields -> return empty map
            if (allFields.isEmpty()) {
                return fieldMap;
            }
            // Map all fields to constructor with id 0 (default)
            fieldMap.put(0, allFields);
        }
        return fieldMap;
    }

    /**
     * Return content between open and closed semicolumns
     * 
     * @param content String to extract content from
     * @return Content in between {}
     */
    private static String getContent(String content) {
        int startPos = content.indexOf("{") + 1;
        int endPos = content.lastIndexOf("}") - 1;
        return content.substring(startPos, endPos);
    }

    /**
     * Save custom code snippet
     * 
     * @param element         Class to extract snippet from
     * @param pathToClassFile Path to class file
     */
    public static void saveSnippet(TypeElement element, String pathToClassFile) {
        // Generate snippet name
        String snippetName;
        if ((snippetName = element.getAnnotation(Custom.class).name()).equals("-")) {
            // If no custom name is given
            snippetName = element.getSimpleName().toString();
        }
        // Create File instance to write code snippet into
        File snippetsFile = new File(snippetPath + snippetName + "Snippet.txt");
        // Initialize Catalogue File instace
        File catalogueFile = new File(cataloguePath);
        try {
            // Initialize Catalogue class
            // Create package for snippets if it does not exist
            if (catalogueFile.getParentFile().mkdirs()) {
                System.out.println("Package for Custom Templates created: " + catalogueFile.getParentFile());
            }
            // Create Catalogue class if it does not exist
            if (catalogueFile.createNewFile()) {
                System.out.println("Catalogue of saved Custom Templates created: " + catalogueFile.getAbsolutePath());
                writeCatalogue(catalogueFile);
            }
            // Create Option for code snippet and update catalogue file
            addOption(snippetName, catalogueFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        try {
            // Create snippet file
            if (snippetsFile.getParentFile().mkdirs()) {
                System.out.println("Snippets package created: " + snippetsFile.getParentFile());
            }
            // Check whether file with the same name exists
            if (!snippetsFile.exists()) {
                // If doesn't -> create new file
                System.out.println("Snippet file created: " + snippetsFile.getAbsolutePath());
                snippetsFile.createNewFile();
            } else {
                // If it does -> check if snippet is marked for update on each build
                if (!element.getAnnotation(Custom.class).update()) {
                    // Return if it is not
                    System.out.println(snippetsFile.getAbsolutePath()
                            + " already exists. Set @Custom(update = true) to update snippet on each build");
                    return;
                }
            }
            // Generate code snippet
            String content = "package {pkg};\n"
                    + processClass(pathToClassFile).replaceAll(element.getSimpleName().toString(),
                            "\\{class\\}");
            // Save code snippet
            try (PrintWriter writer = new PrintWriter(new FileWriter(snippetsFile, false))) {
                writer.println(content);
            }
            System.out.println("Content written to the file.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Writes Catalogue class file
     * 
     * @param catalogueFile File instance of Catalogue file
     * @throws FileNotFoundException Throws if Catalogue file is not found
     */
    private static void writeCatalogue(File catalogueFile) throws FileNotFoundException {
        // Create Catalogue base content
        String content = "package snippets;\n" +
                "\npublic class Catalogue{\n" +
                "\t//Options\n" +
                "}";
        // Write content to the file
        PrintWriter writer = new PrintWriter(catalogueFile);
        writer.write(content);
        // Close writer
        writer.close();
    }

    /**
     * Reads all content of the file into a string (for small and moderately-sized
     * files only)
     * 
     * @param path Path to the file to read from
     * @return String containing file content
     * @throws IOException Throws if unable to read from the file
     */
    private static String readAllContent(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    /**
     * Updates fields in Catalogue class adding new field representing new code
     * snippet
     * 
     * @param className     Name of the code Snippet
     * @param catalogueFile File instance of Catalogue file
     * @throws IOException Throws if unable to update Catalogue file
     */
    private static void addOption(String className, File catalogueFile) throws IOException {
        // Read catalogue file content
        String content = readAllContent(cataloguePath);
        // Check if such option already present to avoid duplicates
        if (content.contains("String " + className)) {
            System.out.println(className + " is already in a catalogue.");
            return;
        }
        // Add new field
        content = content.replaceFirst("\t//Options\n",
                "\t//Options\n\t public final static String " + className + " = \"" + className + "\";\n");
        // Update contents of Catalogue file
        try (PrintWriter writer = new PrintWriter(catalogueFile)) {
            writer.println(content);
        }
    }

    /**
     * Converts class contents into ready to save code snippet
     * 
     * @param path Path to class file
     * @return Processed into code snippet contents of the file
     * @throws IOException Throws if unable to read from the file
     */
    private static String processClass(String path) throws IOException {
        // Read class content
        String content = readAllContent(path);
        // Remove useless imports
        content = content.replaceAll("import com.example.annotations.field.CustomField;", "")
                .replaceAll("import com.example.annotations.method.CustomMethod;", "")
                .replaceAll("import com.example.annotations.type.Custom;", "")
                .replaceAll("import com.example.annotations.type.CustomEnum;", "");
        // Remove package declaration if there is any
        String processed = "";
        int startPos = 0;
        if (content.startsWith("package")) {
            startPos = content.indexOf(";") + 1;
        }
        // Find starting @Custom annotation
        processed += content.substring(startPos, content.indexOf("@Custom"));
        // Remove this annotation
        content = content.replaceFirst(processed + "@Custom", "");
        processed += "\n" + content.substring(content.indexOf("\n"), content.indexOf("{") + 1);
        // Process fields marked with @CustomField
        // Clear @CustomField annotations from snippet
        int pos = 0;
        while ((pos = content.indexOf("@CustomField")) != -1) {
            String extracted = content.substring(pos, content.indexOf(";", pos) + 1);
            content = content.replaceFirst("@CustomField", "");
            extracted = extracted.replaceAll("\\@CustomField()", "").replaceAll("\\@CustomField", "");
            processed += "\n" + extracted + "\n";
        }
        // Process methods marked with @CustomMethod
        // Clear @CustomMethod annotations from snippet
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
        // Process enums marked with @CustomEnum
        // Clear @CustomEnum annotations from snippet
        while ((pos = content.indexOf("@CustomEnum")) != -1) {
            String extracted = content.substring(pos, content.indexOf("}", pos) + 1);
            content = content.replaceFirst("@CustomEnum", "");
            extracted = extracted.replaceAll("\\@CustomEnum()", "").replaceAll("\\@CustomEnum", "");
            processed += "\n" + extracted + "\n";
        }
        // Process inner classes marked with @Custom
        // Clear @Custom annotations from snippet
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
        // Add internal placeholder
        processed += "\n{internal}\n}";
        return processed;
    }

}
