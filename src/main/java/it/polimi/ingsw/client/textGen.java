package it.polimi.ingsw.client;

import java.util.Random;

//DEBUG CLASS
public class textGen {
    private static Random randomGen = new Random();

    public static String getRandomString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< 9;i++) {
            sb.append((char) (randomGen.nextInt(23) + 97));
        }
        return sb.toString();
    }
}
