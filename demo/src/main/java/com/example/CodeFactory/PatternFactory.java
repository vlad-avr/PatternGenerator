package com.example.CodeFactory;

import java.io.File;

import com.example.InputHandler.InputHandler;

public class PatternFactory {

    public static enum Patterns{
        SINGLETON,
        FACTORY
    };


    public static void makePatternSnippet(Patterns pattern){
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

    }

    private static File initClassFile(){
        return null;
    }
    
}
