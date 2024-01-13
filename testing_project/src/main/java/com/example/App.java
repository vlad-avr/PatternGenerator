package com.example;

import com.example.GetterSetterTest.GetterSetterForAll;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        GetterSetterForAll gsfa = new GetterSetterForAll();
        System.out.println(gsfa.toString());
    }
}
