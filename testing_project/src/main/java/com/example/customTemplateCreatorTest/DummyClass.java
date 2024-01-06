package com.example.customTemplateCreatorTest;








public class DummyClass {

    private int brainCells = 2;


    public static final String msg = "Bismark";


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

