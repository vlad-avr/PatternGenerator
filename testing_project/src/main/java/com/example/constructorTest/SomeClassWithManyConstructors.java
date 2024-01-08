package com.example.constructorTest;

import java.time.LocalDate;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.MakeConstructor;


public class SomeClassWithManyConstructors {
    @ToConstruct(id = {1,2})
    public int someNum;
    @ToConstruct(id = {1,3})
    public int anotherNum;
    @ToConstruct(id = {2})
    public double coolDouble;
    @ToConstruct(id = {3})
    public float lameFloat;
    @ToConstruct
    private LocalDate reallyLocalDate;

	public SomeClassWithManyConstructors(java.time.LocalDate reallyLocalDate){
		this.reallyLocalDate = reallyLocalDate;
	}
	public SomeClassWithManyConstructors(int someNum, int anotherNum){
		this.someNum = someNum;
		this.anotherNum = anotherNum;
	}
	public SomeClassWithManyConstructors(int someNum, double coolDouble){
		this.someNum = someNum;
		this.coolDouble = coolDouble;
	}
	public SomeClassWithManyConstructors(int anotherNum, float lameFloat){
		this.anotherNum = anotherNum;
		this.lameFloat = lameFloat;
	}
}
