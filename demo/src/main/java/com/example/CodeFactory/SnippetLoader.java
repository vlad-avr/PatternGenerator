package com.example.CodeFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SnippetLoader {

    public static enum PatternCode{
        SC,
        STS
    }

    private static final String patternPath = "demo\\src\\main\\java\\com\\example\\Snippets\\PatternSnippets.txt";
    private static final String structPath = "demo\\src\\main\\java\\com\\example\\Snippets\\StructureSnippets.txt";

    public static String loadPatternSnippet(PatternCode code) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(patternPath));
        String res = "";
        switch (code) {
            case SC:
                res = readChunk(PatternCode.SC.toString(), "~", reader);
                break;
            case STS:
                res = readChunk(PatternCode.STS.toString(), "~", reader);
                break;        
            default:
                break;
        }
        reader.close();
        return res;
    }

    private static String readChunk(String start, String end, BufferedReader reader) throws IOException{
        String res = "";
        String line = "";
        do{
            line = reader.readLine();
        }while(!line.equals(start));
        line = reader.readLine();
        do{
            res += line;
            res += "\n";
            line = reader.readLine();
        }while(!line.equals(end));
        return res;
    }
    
}
