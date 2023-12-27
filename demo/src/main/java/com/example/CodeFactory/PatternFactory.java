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
            content = content.replaceAll("\\{classname\\}", getClassName(path)).replaceAll("\\{options\\}", "").replaceAll("\\{parent\\}", "Object");
        }else{
            System.out.println("Select classes from your environment that you want to produce from this factory");
            List<Class<?>> classes = new ArrayList<>();
            do{
                classes.add(InputHandler.getClass("Enter legal class name: "));
            }while(InputHandler.getBool("Do you want to add other classes to factory? (+ or -):"));
        }
    }

    private static Object getCommonParent(List<Class<?>> classes){
        if(classes.size() == 0){
            return Object.class;
        }
        Class<?> parentClass = classes.get(0).getSuperclass();
        while (true) {
            for(int i = 1; i < classes.size(); i++){

            }
        }
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
