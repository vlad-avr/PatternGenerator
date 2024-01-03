package com.example.dummy;
class D2Singleton
{
    private static volatile D2 obj  = null;
    private D2Singleton() {}
    
    public static D2 getInstance()
    {
        if (obj == null)
        {
            // To make thread safe
            synchronized (D2Singleton.class)
            {
                // check again as multiple threads
                // can reach above step
                if (obj==null)
                    obj = new D2();
            }
        }
        return obj;
    }
}

