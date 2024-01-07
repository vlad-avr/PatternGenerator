package com.example.customTemplateCreatorTest;

import com.example.annotations.field.CustomField;
import com.example.annotations.method.CustomMethod;
import com.example.annotations.type.Custom;
import com.example.annotations.type.CustomEnum;

@Custom(update = true, name = "AnotherTestTemplate")
public class CustomTemplate extends Object {
    @CustomField
    private int brainCells = 2;
    @CustomField
    public static final String msg = "Bismark";
    @CustomField
    private final String unimportantField = "NOT IMPORTANT";

    @CustomEnum
    public static enum CoolEnum{
        COOL,
        VERY_COOL,
    }

    @Custom
    public static enum LameEnum{
        LAME,
        VERY_LAME,
    }

    @CustomMethod
    public CustomTemplate(int numberOfBrainCells){
        this.brainCells = numberOfBrainCells;
    }

    @CustomMethod
    public CustomTemplate(){
        System.out.println("CONSTRUCTING SOMETHING HERE!!!!");
    }

    public CustomTemplate(String number){
        System.out.println(number);
    }


}
