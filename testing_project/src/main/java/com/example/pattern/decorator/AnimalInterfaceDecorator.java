package com.example.pattern.decorator;

import com.example.FactoryTest.AnimalInterface;;

class AnimalInterfaceDecorator implements AnimalInterface{
    protected AnimalInterface decoratedAnimalInterface;

    public AnimalInterfaceDecorator(AnimalInterface obj){
        this.decoratedAnimalInterface = obj;
    }

	@Override
	public int move(int pos){
		return decoratedAnimalInterface.move(pos);
	}

	@Override
	public void speak(java.lang.String sound){
		decoratedAnimalInterface.speak(sound);
	}


}

