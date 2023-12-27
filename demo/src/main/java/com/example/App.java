package com.example;

import java.io.IOException;

import com.example.CodeFactory.PatternFactory;
import com.example.CodeFactory.PatternFactory.Pattern;

public final class App {
    private App() {
    }

    /**
     * Will be used mainly for testing
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        try {
            PatternFactory.makePatternSnippet(Pattern.SINGLETON);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
