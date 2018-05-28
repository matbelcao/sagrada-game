package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIElems;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UIMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class cliElemsTest {
    private static CLIElems cliel=new CLIElems();
    private static Client client;

    @BeforeAll
    static void setup(){
        client=new Client(UIMode.CLI,ConnectionMode.SOCKET);
    }
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

        System.out.println("");System.out.println("");System.out.println("");







    }
}