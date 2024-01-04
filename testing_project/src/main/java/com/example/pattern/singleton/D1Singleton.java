package com.example.pattern.singleton;

import com.example.dummy.D1;

class D1Singleton
{
    private static D1 obj;
 
    private D1Singleton() {}
 
    public static D1 getInstance()
    {
        if (obj==null)
            obj = new D1();
        return obj;
    }
}

