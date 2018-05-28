package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIElems;
import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.PrivObjectiveCard;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class cliElemsTest {
    private static CLIElems cliel=new CLIElems();
    private static CLIView cliview;
    private static LightPlayer player0;
    private static LightPlayer player1;
    private static LightPlayer player2;
    private static LightPlayer player3;
    private static LightSchemaCard schema0;
    private static LightSchemaCard schema1;
    private static LightSchemaCard schema2;
    private static LightSchemaCard schema3;
    private static HashMap<Integer,LightDie> draftpool;
    private static ArrayList<LightCard> obj= new ArrayList<>();

    @BeforeAll
    static void setup(){

        schema0=LightSchemaCard.toLightSchema(new SchemaCard(1,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema1=LightSchemaCard.toLightSchema(new SchemaCard(2,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema2=LightSchemaCard.toLightSchema(new SchemaCard(3,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema3=LightSchemaCard.toLightSchema(new SchemaCard(4,MasterServer.XML_SOURCE +"SchemaCard.xml"));

        draftpool=new HashMap<>();

        draftpool.put(0,new LightDie("FOUR","RED"));
        draftpool.put(1,new LightDie("SIX","RED"));
        draftpool.put(2,new LightDie("FOUR","GREEN"));
        draftpool.put(3,new LightDie("TWO","RED"));
        draftpool.put(4,new LightDie("ONE","YELLOW"));

        player0= new LightPlayer("ciccio",0);
        player1= new LightPlayer("bubba",1);
        player2= new LightPlayer("boby",2);
        player3= new LightPlayer("cocco",3);

        player0.setSchema(schema0);
        player1.setSchema(schema1);
        player2.setSchema(schema2);
        player3.setSchema(schema3);

        cliview= new CLIView(UILanguage.ita,1,4);
        cliview.setClientInfo(ConnectionMode.SOCKET,"ciccio");

        cliview.updateSchema(player0);
        cliview.updateSchema(player1);
        cliview.updateSchema(player2);
        cliview.updateSchema(player3);

        cliview.updateRoundTurn(1,0);

        cliview.updateDraftPool(draftpool);

        obj.add(LightCard.toLightCard(new PubObjectiveCard(2,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        obj.add(LightCard.toLightCard(new PubObjectiveCard(3,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        obj.add(LightCard.toLightCard(new PubObjectiveCard(4,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        LightPrivObj privobj =LightPrivObj.toLightPrivObj(new PrivObjectiveCard(2,MasterServer.XML_SOURCE+"PrivObjectiveCard.xml"));

        cliview.updateObjectives(obj,privobj);

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


System.out.printf(cliview.printView());




    }
}