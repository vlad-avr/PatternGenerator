package com.example.constructorTest;

import java.util.List;

import com.example.annotations.type.MakeConstructor;


public class SomeClass {
	public SomeClass(int num, int privateNum, int protectedNum, java.util.List<java.lang.String> privateStringList){
		this.num = num;
		this.privateNum = privateNum;
		this.protectedNum = protectedNum;
		this.privateStringList = privateStringList;
	}

    public int num;
    private int privateNum;
    protected int protectedNum;
    public static int staticNum;
    private List<String> privateStringList;
}

