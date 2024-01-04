package com.example.pattern.factory;

import java.lang.Object;
import com.example.FactoryTest.Human;
import com.example.FactoryTest.Builder;

class ObjectFactory{

    public static enum Option{
		HUMAN,
		BUILDER,

    }

    public static Object makeObject(Option option){
        switch(option){
			case HUMAN:
				return new Human();
			case BUILDER:
				return new Builder();

            default:
                return null;
        }
    }
}

