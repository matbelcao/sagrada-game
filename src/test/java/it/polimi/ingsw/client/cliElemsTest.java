package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIElems;
import it.polimi.ingsw.common.enums.Color;
import org.junit.jupiter.api.Test;

class cliElemsTest {
    private static CLIElems cliel=new CLIElems();

    @Test
    void testGetOne(){
        System.out.printf(cliel.getBigDie("ONE"));
        System.out.println("");
        System.out.printf(cliel.getBigDie("TWO"));
        System.out.println("");
        System.out.printf(cliel.getBigDie("THREE"));
        System.out.println("");
        System.out.printf(cliel.getBigDie("FOUR"));
        System.out.println("");
        System.out.printf(Color.BLUE.getUtf()+cliel.getBigDie("FIVE")+ Color.RESET);
        System.out.println("");
        System.out.printf(cliel.getBigDie("SIX"));
        System.out.println("");
        System.out.printf(cliel.getBigDie("FILLED"));
        System.out.println("");
        System.out.printf(cliel.getBigDie("EMPTI"));






    }
}