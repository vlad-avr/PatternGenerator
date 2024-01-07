package com.example.BuilderTest;

import java.time.LocalDate;
import java.util.List;

import com.example.FactoryTest.Human;
import com.example.annotations.field.ToBuild;
import com.example.annotations.type.Builder;

@Builder
public class House {
    @ToBuild
    public int numberOfRooms;
    @ToBuild
    public double squareMeters;
    @ToBuild
    public static int someValue;
    @ToBuild
    public final int someFinalValue = 148;
    @ToBuild
    private boolean ebal;
    @ToBuild
    public LocalDate dateOfFounding;
    @ToBuild
    public List<Human> inhabitants;
    public String notToBeBuilt = "NOT BUILT";
}
