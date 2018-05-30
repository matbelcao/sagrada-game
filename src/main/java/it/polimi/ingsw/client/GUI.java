package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

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
    public void showLobby() {

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
    public void showDraftedSchemas(LightBoard board) {

    }

    @Override
    public void updateChosenSchemas(Map<Integer, LightSchemaCard> schemas) {

    }

    @Override
    public void updateRoundStart(int numRound) {

    }

    @Override
    public void updateTurnStart(int playerId, boolean isFirstTurn) {

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
