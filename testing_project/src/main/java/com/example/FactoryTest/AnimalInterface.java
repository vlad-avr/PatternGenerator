package com.example.FactoryTest;

import com.example.annotations.method.ToOverride;
import com.example.annotations.type.Decorator;

@Decorator(pkg = "com.example.pattern.decorator")
public interface AnimalInterface {

    @ToOverride
    public int move(int pos);
    @ToOverride
    public void speak(String sound);
    public static int getTwo(){
        return 2;
    }

}
