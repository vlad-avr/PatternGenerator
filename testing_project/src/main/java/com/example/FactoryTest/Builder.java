package com.example.FactoryTest;

import com.example.annotations.type.Factory;
import com.example.annotations.type.MakeInterface;

@Factory(id = "F")
@MakeInterface
public class Builder extends Worker{
    public int pay(){
        return 100;
    }

    public void getPaid(int salary){
        System.out.println("RECIEVED : " + salary);
    }

    private void secretAct(){}
    public static String build(){return "All done";}
}
