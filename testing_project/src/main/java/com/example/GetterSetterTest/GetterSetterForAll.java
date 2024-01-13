package com.example.GetterSetterTest;

import java.util.List;

import com.example.annotations.field.GetterSetter;
import com.example.annotations.type.ToString;

@ToString
@GetterSetter
public class GetterSetterForAll {
	public String toString(){
		return "[numToGetSet : " + this.numToGetSet + "\nstringToSet : " + this.stringToSet + "\nfinalDouble : " + this.finalDouble + "\nintList : " + this.intList + "]";
	}

	public int getNumToGetSet(){
		return this.numToGetSet;
	}
	public void setNumToGetSet(int numToGetSet){
		this.numToGetSet = numToGetSet;
	}
	public void setStringToSet(java.lang.String stringToSet){
		this.stringToSet = stringToSet;
	}
	public double getFinalDouble(){
		return this.finalDouble;
	}
	public java.util.List<java.lang.Integer> getIntList(){
		return this.intList;
	}

    public int numToGetSet;
    @GetterSetter(makeGetter = false)
    private String stringToSet;
    private final double finalDouble = 0.69;
    @GetterSetter(makeSetter = false)
    protected List<Integer> intList;
}








