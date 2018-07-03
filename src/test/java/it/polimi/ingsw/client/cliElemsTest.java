package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controller.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIFormatter;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIObjects;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.CLIFormats;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.PrivObjectiveCard;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.ToolCard;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.view.clientUI.uielements.enums.CLIFormats.FILLED;
import static it.polimi.ingsw.common.enums.Shade.*;

class cliElemsTest {
    private static CLIFormatter cliel;
    private static String OS = null;
    static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }
    static boolean isWindows()
    {
        return getOsName().startsWith("Windows");
    }


    static {
        try {
            cliel = new CLIFormatter();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static CLIObjects cliview;
    private static LightPlayer player0;
    private static LightPlayer player1;
    private static LightPlayer player2;
    private static LightPlayer player3;
    private static LightSchemaCard schema0;
    private static LightSchemaCard schema1;
    private static LightSchemaCard schema2;
    private static LightSchemaCard schema3;
    private static List<LightDie> draftpool;
    private static List<List<LightDie>> roundtrack ;
    private static List<LightCard> obj= new ArrayList<>();
    private static List<LightTool> tools= new ArrayList<>();
    private static List<Integer> list= new ArrayList<>();
    @BeforeAll
    static void setup(){

        getOsName();
        if(isWindows()){
            AnsiConsole.systemInstall();
        }

        schema0=LightSchemaCard.toLightSchema(new SchemaCard(1,false));
        schema1=LightSchemaCard.toLightSchema(new SchemaCard(2,false));
        schema2=LightSchemaCard.toLightSchema(new SchemaCard(3,false));
        schema3=LightSchemaCard.toLightSchema(new SchemaCard(4,false));

        draftpool=new ArrayList<>();

        draftpool.add(new LightDie("FOUR","RED"));
        draftpool.add(new LightDie("SIX","RED"));
        draftpool.add(new LightDie("FOUR","GREEN"));
        draftpool.add(new LightDie("TWO","RED"));
        draftpool.add(new LightDie("ONE","YELLOW"));

        player0= new LightPlayer("aaaaaaaaaaaaaaaa",0);
        player1= new LightPlayer("bubba354627yhdge",1);
        player2= new LightPlayer("boby",2);
        player3= new LightPlayer("cocco",3);

        player0.setSchema(schema0);
        player1.setSchema(schema1);
        player2.setSchema(schema2);
        player3.setSchema(schema3);
        player3.setStatus(LightPlayerStatus.DISCONNECTED);


            cliview= new CLIObjects(UILanguage.ita);


        cliview.setMatchInfo(1,4);
        cliview.setClientInfo(ConnectionMode.SOCKET,player1.getUsername());
        cliview.updateSchema(player0);
        cliview.updateSchema(player1);
        cliview.updateSchema(player2);
        cliview.updateSchema(player3);

        cliview.updateRoundNumber(5);
        cliview.updateIsFirstTurn(true);
        cliview.updateNowPlaying(2);
        cliview.updateDraftPool(draftpool);

        list.add(0);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(6);
        list.add(9);
        list.add(13);
        list.add(15);
        list.add(16);
        cliview.updateMenuListPlacements(list,new LightDie("FOUR","GREEN"));

        roundtrack= new ArrayList<>();
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());
        roundtrack.add(new ArrayList<>());

        roundtrack.get(0).add(new LightDie("ONE","YELLOW"));
        roundtrack.get(1).add(new LightDie("TWO","YELLOW"));
        roundtrack.get(2).add(new LightDie("ONE","BLUE"));
        roundtrack.get(2).add(new LightDie("FIVE","RED"));
        roundtrack.get(3).add(new LightDie("SIX","YELLOW"));
        roundtrack.get(4).add(new LightDie("THREE","PURPLE"));
        roundtrack.get(5).add(new LightDie("ONE","RED"));
        roundtrack.get(6).add(new LightDie("FOUR","GREEN"));
        roundtrack.get(6).add(new LightDie("ONE","YELLOW"));

        tools.add(LightTool.toLightTool(new ToolCard(2)));
        tools.add(LightTool.toLightTool(new ToolCard(3)));
        tools.add(LightTool.toLightTool(new ToolCard(6)));
        tools.get(2).setUsed(true);

        cliview.updateTools(tools);

        cliview.updateRoundTrack(roundtrack);

        obj.add(LightCard.toLightCard(new PubObjectiveCard(2,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        obj.add(LightCard.toLightCard(new PubObjectiveCard(3,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        obj.add(LightCard.toLightCard(new PubObjectiveCard(4,MasterServer.XML_SOURCE+"PubObjectiveCard.xml")));
        LightPrivObj privobj =LightPrivObj.toLightPrivObj(new PrivObjectiveCard(2));

        cliview.updateObjectives(obj,privobj);

    }
    @Test
    void  testGetOne(){
        System.out.printf(cliel.getBigDie(ONE));
        System.out.println("");
        System.out.printf(cliel.getBigDie(TWO));
        System.out.println("");
        System.out.printf(cliel.getBigDie(THREE));
        System.out.println("");
        System.out.printf(cliel.getBigDie(FOUR));
        System.out.println("");
        System.out.printf(DieColor.BLUE.getUtf()+cliel.getBigDie(FIVE)+ DieColor.NONE.getUtf());
        System.out.println("");
        System.out.printf(cliel.getBigDie(SIX));
        System.out.println("");
        System.out.printf(cliel.getElem(FILLED));
        System.out.println("");
        System.out.printf(cliel.getElem(CLIFormats.EMPTY));

        System.out.println("");System.out.println("");System.out.println("");


        System.out.printf(cliview.printMainView(ClientFSMState.CHOOSE_PLACEMENT));




    }

}