package com.example.constructorTest;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.MakeConstructor;


public class SomeClassWithLocalClass {
	public SomeClassWithLocalClass(java.lang.String name, int age){
		this.name = name;
		this.age = age;
	}

    private String name;
    private int age;

    
    class LocalClass{
		public LocalClass(java.lang.String localName){
			this.localName = localName;
		}
		public LocalClass(int localAge){
			this.localAge = localAge;
		}

        @ToConstruct(id = {1})
        private String localName;
        @ToConstruct(id = {2})
        private int localAge;
    }
}


