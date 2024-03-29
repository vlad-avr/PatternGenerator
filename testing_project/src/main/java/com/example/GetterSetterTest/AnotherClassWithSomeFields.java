package com.example.GetterSetterTest;

import java.time.LocalDate;

import com.example.annotations.field.GetterSetter;
import com.example.annotations.type.Equals;
import com.example.annotations.type.ToString;

@ToString
@Equals
public class AnotherClassWithSomeFields {
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		AnotherClassWithSomeFields other = (AnotherClassWithSomeFields)o;
		return java.util.Objects.equals(this.num, other.num)
			&& java.util.Objects.equals(this.str, other.str)
			&& java.util.Objects.equals(this.justDate, other.justDate)
			&& java.util.Objects.equals(this.specialDate, other.specialDate)
			&& java.util.Objects.equals(this.verySpecialDate, other.verySpecialDate);
	}
	public String toString(){
		return "[num : " + this.num + "\nstr : " + this.str + "\njustDate : " + this.justDate + "\nspecialDate : " + this.specialDate + "\nverySpecialDate : " + this.verySpecialDate + "]";
	}

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

    /**
     * InnerAnotherClassWithSomeFields
     */
    @GetterSetter
	@Equals
    public class InnerClassWithSomeFields {
		public boolean equals(Object o){
			if (this == o) return true;
			if (o == null) return false;
			if (getClass() != o.getClass()) return false;
			InnerClassWithSomeFields other = (InnerClassWithSomeFields)o;
			return java.util.Objects.equals(this.field1, other.field1)
				&& java.util.Objects.equals(this.finalField, other.finalField)
				&& java.util.Objects.equals(this.field2, other.field2);
		}
		public int getField1(){
			return this.field1;
		}
		public void setField1(int field1){
			this.field1 = field1;
		}
		public int getFinalField(){
			return this.finalField;
		}
		public int getField2(){
			return this.field2;
		}
		public void setField2(int field2){
			this.field2 = field2;
		}

        public int field1;
        public final int finalField = 2;
        public int field2;
    }
}












