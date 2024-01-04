package com.example.pattern.decorator;

import com.example.FactoryTest.Animal;;

class AnimalDecorator extends Animal{
    protected Animal decoratedAnimal;

    public AnimalDecorator(Animal obj){
        this.decoratedAnimal = obj;
    }

	@Override
	public int move(int pos){
		return decoratedAnimal.move(pos);
	}

	@Override
	public synchronized void sayStr(java.lang.String str){
		return decoratedAnimal.sayStr(str);
	}

	@Override
	protected java.lang.String getMeme(int id, float idf){
		return decoratedAnimal.getMeme(id, idf);
	}

	@Override
	public java.util.List<java.lang.String> getEmptyList(){
		return decoratedAnimal.getEmptyList();
	}


}

