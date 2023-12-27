package com.example.CodeFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.InputHandler.InputHandler;

public class PatternFactory {

    public static enum Patterns {
        SINGLETON,
        FACTORY
    };

    public static void makePatternSnippet(Patterns pattern) {
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

    private static void makeSingleton(){
        String content;
        if(InputHandler.getBool("Enter '+' if you want a classic implementation or '-' for thread safe implementation: ")){

        }
    }

    private static Path getPath() {
        Path path = InputHandler.getFilePath("Enter valid file path: ");
        return path;
    }


}
