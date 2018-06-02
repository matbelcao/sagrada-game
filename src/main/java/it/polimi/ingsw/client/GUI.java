package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.immutables.*;

import java.util.List;
import java.util.Map;

public class GUI implements ClientUI {
    private Client client;
    private UIMessages uimsg;

    public GUI(Client client, UILanguage lang){
        this.uimsg=new UIMessages(lang);
        this.client = client;
        System.out.println("Created gui!!");
    }

    @Override
    public void showLoginScreen() {

    }


    @Override
    public void updateLogin(boolean logged) {

    }


    @Override
    public void updateConnectionOk() {

    }

    @Override
    public void updateLobby(int numUsers) {

    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {

    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {

    }

    @Override
    public void updateBoard(LightBoard board) {

    }

    @Override
    public void updateDraftPool(Map<Integer, LightDie> draftpool) {

    }

    @Override
    public void updateSchema(LightSchemaCard schema, int playerId) {

    }

    @Override
    public void updateRoundTrack(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void showRoundTrackWithCoordinates(List<List<LightDie>> roundtrack) {

    }


    @Override
    public void updateRoundStart(int numRound, List<List<LightDie>> roundtrack) {

    }

    @Override
    public void updateTurnStart(int playerId, boolean isFirstTurn, Map<Integer,LightDie> draftpool) {

    }

    @Override
    public void updateToolUsage(List<LightTool> tools) {

    }


    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

    }


    @Override
    public void updateConnectionClosed() {

    }

    @Override
    public void updateConnectionBroken() {

    }

    @Override
    public void printmsg(String msg) {

    }

    @Override
    public String getCommand() {
        return null;
    }
}
