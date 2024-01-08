package com.example.constructorTest;

import java.time.LocalDate;

import com.example.annotations.field.ToConstruct;
import com.example.annotations.type.MakeConstructor;

@MakeConstructor
public class SomeClassWithManyConstructors {
    @ToConstruct(id = {1,2})
    public int someNum;
    @ToConstruct(id = {1,3})
    public int anotherNum;
    @ToConstruct(id = {2})
    public double coolDouble;
    @ToConstruct(id = {3})
    public float lameFloat;
    @ToConstruct
    private LocalDate reallyLocalDate;
}
