package com.example.GetterSetterTest;

import java.util.List;

import com.example.annotations.field.GetterSetter;

@GetterSetter
public class GetterSetterForAll {
    public int numToGetSet;
    @GetterSetter(makeGetter = false)
    private String stringToSet;
    private final double finalDouble = 0.69;
    @GetterSetter(makeSetter = false)
    protected List<Integer> intList;
}
