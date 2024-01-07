package com.example.customTemplateCreatorTest;

import com.example.annotations.field.CustomField;
import com.example.annotations.method.CustomMethod;
import com.example.annotations.type.Custom;
import com.example.annotations.type.CustomEnum;
@Custom(update = true, name = "TestTemplate")
public class AnotherTemplate {
    @CustomField
    private int brainCells = 2;
    @CustomField
    public static final String msg = "Bismark";

    private final String unimportantField = "NOT IMPORTANT";

    @CustomEnum
    public static enum CoolEnum{
        COOL,
        VERY_COOL,
    }

    @CustomEnum
    public static enum LameEnum{
        LAME,
        VERY_LAME,
    }

    @CustomMethod
    public AnotherTemplate(int numberOfBrainCells){
        this.brainCells = numberOfBrainCells;
    }

    @CustomMethod
    public AnotherTemplate(){
        System.out.println("CONSTRUCTING SOMETHING HERE!!!!");
    }
    @CustomMethod
    public AnotherTemplate(String number){
        System.out.println(number);
    }

    @CustomMethod
    public static int getTwoIfTrue(boolean bool){
        if(bool){
            return 2;
        }else{
            return 69;
        }
    }

    @Custom(createSnippet = true)
    class InnerClass{
        int innerClassVar = 420;
    }

}
