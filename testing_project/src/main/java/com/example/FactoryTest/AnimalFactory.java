package com.example.FactoryTest;
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

