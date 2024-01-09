package com.example.codeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Loads code snippets from file 
 * @author vlad-avr
 */
public class SnippetLoader {
    // Enum with options for snippet selection
    public static enum PatternCode{
        SC,
        STS,
        F,
        D,
        I
    }

    // Defined relative path to the file with code snippets
    private static final String patternPath = "/PatternSnippets.txt";
    //private static final String structPath = "/StructureSnippets.txt";

    /**
     * 
     * @param code          Option of SnippetLoader.PatternCode enum 
     * @return              Code snippet as a String object
     * @throws IOException  Thrown if problems occur while accessing/reading file with code snippets
     */
    public static String loadPatternSnippet(PatternCode code) throws IOException{
        //Creating reader for file reading
        InputStream inputStream = SnippetLoader.class.getResourceAsStream(patternPath);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        //Initialising result String
        String res = "";
        //Switch case to run through possible cases
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
        //Closing reader
        reader.close();
        
        return res;
    }

    /**
     * 
     * @param start         Marks from where data chunk should be read 
     * @param end           Marks end of the data chunk
     * @param reader        BufferedReader for reading data
     * @return              Chunk of selected code snippet
     * @throws IOException  Thrown if error occurs while reading file
     */
    private static String readChunk(String start, String end, BufferedReader reader) throws IOException{
        //Result string
        String res = "";
        //String for reading each line separately
        String line = "";
        //Looks for start marker
        do{
            line = reader.readLine();
        }while(!line.equals(start));
        //Reads data chunk until end marker is read
        line = reader.readLine();
        do{
            res += line;
            res += "\n";
            line = reader.readLine();
        }while(!line.equals(end));

        return res;
    }
    
}
