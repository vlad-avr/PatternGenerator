package com.example.customTemplateCreatorTest;









public class DummyClassWithStuff extends Object {

    private int brainCells = 2;


    public static final String msg = "Bismark";


    private final String unimportantField = "NOT IMPORTANT";


    public DummyClassWithStuff(int numberOfBrainCells){
        this.brainCells = numberOfBrainCells;
    }


    public DummyClassWithStuff(){
        System.out.println("CONSTRUCTING SOMETHING HERE!!!!");
    }


    public static enum CoolEnum{
        COOL,
        VERY_COOL,
    }


    public static enum LameEnum{
        LAME,
        VERY_LAME,
    }


    private int two = 2;

    public int getNumber(){
        return two;
    }
}

