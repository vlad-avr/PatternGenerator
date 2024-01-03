package com.example.FactoryTest;
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

