package com.example.customTemplateCreatorTest;


import java.util.ArrayList;
import java.util.List;






import java.lang.String;



public class DummyClass {

    private int brainCells = 2;


    public static final String msg = "Bismark";


    public List<String> stringList = new ArrayList<>();


    public DummyClass(int numberOfBrainCells){
        this.brainCells = numberOfBrainCells;
    }


    public DummyClass(){
        System.out.println("CONSTRUCTING SOMETHING HERE!!!!");
    }


    public DummyClass(String number){
        System.out.println(number);
    }


    public static int getTwoIfTrue(boolean bool){
        if(bool){
            return 2;
        }else{
            return 69;
        }
    }


    public static enum CoolEnum{
        COOL,
        VERY_COOL,
    }


    public static enum LameEnum{
        LAME,
        VERY_LAME,
    }


    class InnerClass{
        int innerClassVar = 420;
    }



}

