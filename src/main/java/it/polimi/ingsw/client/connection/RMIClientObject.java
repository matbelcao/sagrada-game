package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.User;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIClientObject extends UnicastRemoteObject implements RMIClientInt {
    private User user;

    public RMIClientObject(User user) throws RemoteException {
        this.user = user;
    }

    //OK
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
    public List<LightCard> getPublicObjects() {
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
    public LightGameStatus getGameStatus() throws RemoteException {
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
    public void newMatch() throws RemoteException { user.newMatch(); }

    @Override
    public boolean pong() {
        user.getClass();
        return true;
    }
}
