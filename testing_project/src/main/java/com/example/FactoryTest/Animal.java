package com.example.FactoryTest;

import com.example.annotations.method.ToOverride;
import com.example.annotations.type.Decorator;

@Decorator(pkg = "com.example.pattern.decorator")
public class Animal {
    @ToOverride
    public int move(){
        return 0;
    }

    @ToOverride
    public static void sayStr(String str){
        System.out.println(str);
    }

    @ToOverride
    private String getMeme(int id, float idf){
        return "Meme " + id + " " + idf;
    }

    public void nothing(){}
}
