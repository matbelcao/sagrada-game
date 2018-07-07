package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.rmi_interfaces.RMIServerInt;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.common.exceptions.IllegalActionException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIServerObject extends UnicastRemoteObject implements RMIServerInt {
    private User user;

    public RMIServerObject(User user) throws RemoteException {
        this.user = user;
    }

    @Override
    public List<LightSchemaCard> getSchemaDraft() throws IllegalActionException {
        return user.getGame().getDraftedSchemaCards(user);
    }

    @Override
    public LightSchemaCard getSchema(int playerId) throws IllegalActionException {
        return user.getGame().getUserSchemaCard(playerId);
    }

    @Override
    public LightPrivObj getPrivateObject() {
        return user.getGame().getPrivCard(user);
    }

    @Override
    public List<LightCard> getPublicObjectives() {
        return user.getGame().getPubCards();
    }

    @Override
    public List<LightTool> getTools() {
        return user.getGame().getToolCards();
    }

    @Override
    public List<LightDie> getDraftPool() throws IllegalActionException {
        return user.getGame().getDraftedDice();
    }

    @Override
    public List<List<LightDie>> getRoundTrack() throws IllegalActionException {
        return user.getGame().getRoundTrackDice();
    }

    @Override
    public List<LightPlayer> getPlayers() {
        return user.getGame().getPlayers();
    }

    @Override
    public LightGameStatus getGameStatus() {
        return user.getGame().getGameStatus();
    }

    @Override
    public int getFavorTokens(int playerId) {
        return user.getGame().getFavorTokens(playerId);
    }

    @Override
    public List<IndexedCellContent> getDiceList() throws IllegalActionException {
        return user.getGame().getDiceList();
    }

    @Override
    public List<Actions> select(int dieIndex) throws IllegalActionException {
        return user.getGame().selectDie(dieIndex);
    }

    @Override
    public List<Integer> getPlacementsList() throws IllegalActionException {
        return user.getGame().getPlacements();
    }

    @Override
    public boolean choose(int optionIndex) throws IllegalActionException {
        return user.getGame().choose(user,optionIndex);
    }

    @Override
    public boolean enableTool(int toolIndex) throws IllegalActionException {
        return user.getGame().activeTool(toolIndex);
    }

    @Override
    public boolean toolCanContinue() throws IllegalActionException {
        return user.getGame().toolStatus();
    }

    @Override
    public void endTurn() throws IllegalActionException {
        if(!user.isMyTurn()){ throw new IllegalActionException();}
        user.getGame().startFlow();
    }

    @Override
    public void discard() throws IllegalActionException {
        if (!user.isMyTurn()) { throw new IllegalActionException(); }
        user.getGame().discard();
    }

    @Override
    public void back() throws IllegalActionException {
        if (!user.isMyTurn()) {throw new IllegalActionException();}
        user.getGame().back(true);
    }

    @Override
    public void quit() { user.quit(); }

    @Override
    public void newMatch() { user.newMatch(); }

    @Override
    public boolean pong() {
        user.getClass();
        return true;
    }
}
