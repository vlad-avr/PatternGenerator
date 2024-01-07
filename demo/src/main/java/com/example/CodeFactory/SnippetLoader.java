package com.example.codeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SnippetLoader {
    
    public static enum PatternCode{
        SC,
        STS,
        F,
        D,
        I
    }

    private static final String patternPath = "/PatternSnippets.txt";
    private static final String structPath = "/StructureSnippets.txt";

    public static String loadPatternSnippet(PatternCode code) throws IOException{
        InputStream inputStream = SnippetLoader.class.getResourceAsStream(patternPath);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        String res = "";
        switch (code) {
            case SC:
                res = readChunk(PatternCode.SC.toString(), "~", reader);
                break;
            case STS:
                res = readChunk(PatternCode.STS.toString(), "~", reader);
                break;
            case F:
                res = readChunk(PatternCode.F.toString(), "~", reader);
                break;
            case D:
                res = readChunk(PatternCode.D.toString(), "~", reader);
                break;
            case I:
                res = readChunk(PatternCode.I.toString(), "~", reader);        
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
