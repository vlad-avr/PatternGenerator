package com.example.constructorTest;

import java.util.List;

import com.example.annotations.type.MakeConstructor;

@MakeConstructor
public class SomeClass {
    public int num;
    private int privateNum;
    protected int protectedNum;
    public static int staticNum;
    private List<String> privateStringList;
}
