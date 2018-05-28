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
    private static ArrayList<LightDie> draftpool;
    private static ArrayList<IndexedCellContent> roundtrack ;
    private static ArrayList<LightCard> obj= new ArrayList<>();

    @BeforeAll
    static void setup(){

        schema0=LightSchemaCard.toLightSchema(new SchemaCard(1,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema1=LightSchemaCard.toLightSchema(new SchemaCard(2,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema2=LightSchemaCard.toLightSchema(new SchemaCard(3,MasterServer.XML_SOURCE +"SchemaCard.xml"));
        schema3=LightSchemaCard.toLightSchema(new SchemaCard(4,MasterServer.XML_SOURCE +"SchemaCard.xml"));

        draftpool=new ArrayList<>();

        draftpool.add(new LightDie("FOUR","RED"));
        draftpool.add(new LightDie("SIX","RED"));
        draftpool.add(new LightDie("FOUR","GREEN"));
        draftpool.add(new LightDie("TWO","RED"));
        draftpool.add(new LightDie("ONE","YELLOW"));

        player0= new LightPlayer("aaaaaaaaaaaaaaaa",0);
        player1= new LightPlayer("bubba",1);
        player2= new LightPlayer("boby",2);
        player3= new LightPlayer("cocco",3);

        player0.setSchema(schema0);
        player1.setSchema(schema1);
        player2.setSchema(schema2);
        player3.setSchema(schema3);

        cliview= new CLIView(UILanguage.ita);

        cliview.setMatchInfo(1,4);
        cliview.setClientInfo(ConnectionMode.SOCKET,player1.getUsername());
        cliview.updateSchema(player0);
        cliview.updateSchema(player1);
        cliview.updateSchema(player2);
        cliview.updateSchema(player3);

        cliview.updateRoundTurn(5,0);

        cliview.updateDraftPool(draftpool);

        roundtrack= new ArrayList<>();
        roundtrack.add(new IndexedCellContent(0,"ONE","YELLOW"));
        roundtrack.add(new IndexedCellContent(1,"TWO","YELLOW"));
        roundtrack.add(new IndexedCellContent(1,"ONE","BLUE"));
        roundtrack.add(new IndexedCellContent(1,"FIVE","RED"));
        roundtrack.add(new IndexedCellContent(2,"SIX","YELLOW"));
        roundtrack.add(new IndexedCellContent(3,"THREE","PURPLE"));
        roundtrack.add(new IndexedCellContent(3,"ONE","RED"));
        roundtrack.add(new IndexedCellContent(4,"FOUR","GREEN"));
        roundtrack.add(new IndexedCellContent(5,"ONE","YELLOW"));


        cliview.updateRoundTrack(roundtrack);

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


        System.out.printf(cliview.printMainView());




    }
}