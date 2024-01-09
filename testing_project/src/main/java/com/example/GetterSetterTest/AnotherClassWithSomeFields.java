package com.example.GetterSetterTest;

import java.time.LocalDate;

import com.example.annotations.field.GetterSetter;

public class AnotherClassWithSomeFields {
	public java.time.LocalDate getVerySpecialDate(){
		return this.verySpecialDate;
	}

	public java.time.LocalDate getSpecialDate(){
		return this.specialDate;
	}

	public void setStr(java.lang.String str){
		this.str = str;
	}

	public int getNum(){
		return this.num;
	}
	public void setNum(int num){
		this.num = num;
	}

    @GetterSetter
    public int num;
    @GetterSetter(makeGetter = false)
    private String str;
    
    private LocalDate justDate = LocalDate.of(11, 9, 2001);
    @GetterSetter(makeSetter = false)
    private LocalDate specialDate = LocalDate.of(10, 9, 2004);
    @GetterSetter
    private final LocalDate verySpecialDate = LocalDate.now();
}




