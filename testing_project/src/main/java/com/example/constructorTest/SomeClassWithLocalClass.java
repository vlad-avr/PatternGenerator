package com.example.constructorTest;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.MakeConstructor;


public class SomeClassWithLocalClass {
    private String name;
    private int age;

    @MakeConstructor
    class LocalClass{
        @ToConstruct(id = {1})
        private String localName;
        @ToConstruct(id = {2})
        private int localAge;
    }

	public SomeClassWithLocalClass(java.lang.String name, int age){
		this.name = name;
		this.age = age;
	}
}
