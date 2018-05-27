package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.CLIElems;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UIMode;
import it.polimi.ingsw.common.immutables.CellContent;
import it.polimi.ingsw.common.immutables.LightConstraint;
import it.polimi.ingsw.common.immutables.LightDie;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

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

        HashMap list= new HashMap<Integer,CellContent>();
        list.put(0,new LightDie("FOUR","BLUE"));
        list.put(2,new LightDie("FOUR","RED"));
        list.put(3, new LightConstraint("BLUE"));
        list.put(6,new LightConstraint("FOUR"));
        CLI c;
        ArrayList<String> bigRow= (ArrayList<String>) (c=new CLI(new Client(UIMode.CLI,ConnectionMode.SOCKET),UILanguage.ita)).buildBigRow(list,0,4);

        System.out.println(""); System.out.println(""); System.out.println(""); System.out.println("");
        for(int i =0;i<bigRow.size();i++){
            System.out.println(bigRow.get(i));
        }
        for(int i =0;i<bigRow.size();i++){
            System.out.println(bigRow.get(i));
        }
        for(int i =0;i<bigRow.size();i++){
            System.out.println(bigRow.get(i));
        }
        for(int i =0;i<bigRow.size();i++){
            System.out.println(bigRow.get(i));
        }
        bigRow= (ArrayList<String>) c.appendBigRows(bigRow,bigRow);


    }
}