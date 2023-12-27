package com.example.CodeFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            System.out.println("Unable to perform operation");
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
