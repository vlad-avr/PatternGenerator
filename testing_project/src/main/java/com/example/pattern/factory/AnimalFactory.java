package com.example.pattern.factory;

import com.example.FactoryTest.Animal;
import com.example.FactoryTest.Bird;
import com.example.FactoryTest.Dog;
import com.example.FactoryTest.Cat;

class AnimalFactory{

    public static enum Option{
		BIRD,
		DOG,
		CAT,

    }

    public static Animal makeAnimal(Option option){
        switch(option){
			case BIRD:
				return new Bird();
			case DOG:
				return new Dog();
			case CAT:
				return new Cat();

            default:
                return null;
        }
    }
}

