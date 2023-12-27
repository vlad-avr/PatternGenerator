package com.example.CodeFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.example.CodeFactory.SnippetLoader.PatternCode;
import com.example.InputHandler.InputHandler;

public class PatternFactory {

    public static enum Pattern {
        SINGLETON,
        FACTORY
    };

    public static void makePatternSnippet(Pattern pattern) throws IOException {
        switch (pattern) {
            case SINGLETON:
                makeSingleton();
                break;
            case FACTORY:
                makeFactory();
                break;
            default:
                return;
        }
    }

    private static void makeSingleton() throws IOException{
        Path path = getPath();
        String content;
        if(InputHandler.getBool("Enter '+' if you want a classic implementation or '-' for thread safe implementation: ")){
            content = SnippetLoader.loadPatternSnippet(PatternCode.SC);
        }else{
            content = SnippetLoader.loadPatternSnippet(PatternCode.STS);
        }
        if(content != null && content != ""){
            content = content.replaceAll("\\{classname\\}", getClassName(path));
            Files.write(path, content.getBytes());
        }else{
            System.out.println("Unable to load pattern snippet");
        }
    }

    private static void makeFactory() throws IOException{
        Path path = getPath();
        String content = SnippetLoader.loadPatternSnippet(PatternCode.F);
        if(InputHandler.getBool("Enter '+' to create raw factory or '-' to create customised factory" )){
            content = content.replaceAll("\\{classname\\}", getClassName(path)).replaceAll("\\{options\\}", "").replaceAll("\\{parent\\}", "Object").replaceAll("\\{case\\}", "");
        }else{
            System.out.println("Select classes from your environment that you want to produce from this factory");
            List<Class<?>> classes = new ArrayList<>();
            do{
                classes.add(InputHandler.getClass("Enter legal class name: "));
            }while(InputHandler.getBool("Do you want to add other classes to factory? (+ or -):"));
            Class<?> parentClass = findCommonParent(classes);
            if(parentClass == null){
                System.out.println("No common parent class found for selected classes!");
                return;
            }
            List<String> options = new ArrayList<>();
            String enumOptions = "";
            for(Class<?> c : classes){
                String str = c.getName().toUpperCase(); 
                options.add(str);
                enumOptions += str + ",\n";
            }
            content = content.replaceAll("\\{classname\\}", getClassName(path)).replaceAll("\\{options\\}", enumOptions).replaceAll("\\{parent\\}", parentClass.getName());
        }
        Files.write(path, content.getBytes());
    }

    private static Class<?> findCommonParent(List<Class<?>> classList) {
        if (classList == null || classList.isEmpty()) {
            return null;
        }

        Class<?> commonParent = classList.get(0);

        for (int i = 1; i < classList.size(); i++) {
            commonParent = findCommonParent(commonParent, classList.get(i));
        }

        return commonParent;
    }

    private static Class<?> findCommonParent(Class<?> class1, Class<?> class2) {
        while (class1 != null && !class1.isAssignableFrom(class2)) {
            class1 = class1.getSuperclass();
        }
        return class1;
    }

    private static Path getPath() {
        Path path = InputHandler.getFilePath("Enter valid file path: ");
        return path;
    }

    private static String getClassName(Path path){
        String name = path.getFileName().toString();
        int ind = name.lastIndexOf('.');
        if(ind > 0){
            return name.substring(0, ind);
        }else{
            return null;
        }
    }


}
