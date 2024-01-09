package com.example.GetterSetterTest;

import java.time.LocalDate;

import com.example.annotations.field.GetterSetter;

public class AnotherClassWithSomeFields {
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
