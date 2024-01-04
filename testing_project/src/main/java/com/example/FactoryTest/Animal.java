package com.example.FactoryTest;

import java.util.ArrayList;
import java.util.List;

import com.example.annotations.method.ToOverride;
import com.example.annotations.type.Decorator;

@Decorator(pkg = "com.example.pattern.decorator")
public class Animal {
    @ToOverride
    public int move(int pos){
        return pos*pos;
    }

    @ToOverride
    public synchronized void sayStr(String str){
        System.out.println(str);
    }

    @ToOverride
    protected String getMeme(int id, float idf){
        return "Meme " + id + " " + idf;
    }

    @ToOverride 
    public List<String> getEmptyList(){
        return new ArrayList<>();
    } 

    public void nothing(){}
}
