package com.example.constructorTest;

import java.time.LocalDate;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.MakeConstructor;
import com.example.annotations.type.ToString;

@ToString
public class SomeClassWithManyConstructors {
	public String toString(){
		return "[someNum : " + this.someNum + "\nanotherNum : " + this.anotherNum + "\ncoolDouble : " + this.coolDouble + "\nlameFloat : " + this.lameFloat + "\nreallyLocalDate : " + this.reallyLocalDate + "]";
	}
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
}


