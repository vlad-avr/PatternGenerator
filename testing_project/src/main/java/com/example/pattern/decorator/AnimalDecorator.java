package com.example.pattern.decorator;

import com.example.FactoryTest.Animal;;

class AnimalDecorator extends Animal{
    

    public AnimalDecorator(){
        super();
    }

	@Override
	public int move(int pos){
		return super.move(pos);
	}

	@Override
	public synchronized void sayStr(java.lang.String str){
		super.sayStr(str);
	}

	@Override
	protected java.lang.String getMeme(int id, float idf){
		return super.getMeme(id, idf);
	}

	@Override
	public java.util.List<java.lang.String> getEmptyList(){
		return super.getEmptyList();
	}


}

