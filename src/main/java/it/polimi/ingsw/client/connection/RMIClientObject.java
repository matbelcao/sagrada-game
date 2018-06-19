package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.User;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIClientObject extends UnicastRemoteObject implements RMIClientInt {
    private User user;

    public RMIClientObject(User user) throws RemoteException {
        this.user = user;
    }

    //OK
    @Override
    public List<LightSchemaCard> getSchemaDraft() throws IllegalActionException {
        List<SchemaCard>  schemas = user.getGame().getDraftedSchemaCards(user);
        List<LightSchemaCard> lightSchema = new ArrayList<>();
        for (SchemaCard s : schemas) {
            lightSchema.add(LightSchemaCard.toLightSchema(s));
        }
        return lightSchema;
    }

    @Override
    public LightSchemaCard getSchema(int playerId) throws IllegalActionException {
        return LightSchemaCard.toLightSchema(user.getGame().getUserSchemaCard(playerId));
    }

    @Override
    public LightPrivObj getPrivateObject() {
        return LightPrivObj.toLightPrivObj(user.getGame().getPrivCard(user));
    }

    @Override
    public List<LightCard> getPublicObjects() {
        List<LightCard> lightCards = new ArrayList<>();
        List<PubObjectiveCard> cards = user.getGame().getPubCards();
        for (PubObjectiveCard c : cards) {
            lightCards.add(LightCard.toLightCard(c));
        }
        return lightCards;
    }

    @Override
    public List<LightTool> getTools() {
        List<ToolCard> toolCards = user.getGame().getToolCards();
        List<LightTool> lightCards = new ArrayList<>();
        for (ToolCard c : toolCards) {
            lightCards.add(LightTool.toLightTool(c));
        }
        return lightCards;
    }

    @Override
    public List<LightDie> getDraftPool() throws IllegalActionException {
        List<Die> draftPool = user.getGame().getDraftedDice();
        List<LightDie> lightDraftPool=new ArrayList<>();
        LightDie die;
        for(Die d:draftPool) {
            die = new LightDie(d.getShade(), d.getColor());
            lightDraftPool.add(die);
        }
        return lightDraftPool;
    }

    @Override
    public List<List<LightDie>> getRoundTrack() throws IllegalActionException {
        List<List<Die>> trackList = user.getGame().getRoundTrackDice();
        List<Die> dieList;

        List<List<LightDie>> roundTrack=new ArrayList<>();
        List<LightDie> container;
        LightDie die;

        for(int i=0;i<trackList.size();i++){
            dieList=trackList.get(i);
            container = new ArrayList<>();
            for(Die d:dieList){
                die=new LightDie(d.getShade(),d.getColor());
                container.add(die);
            }
            roundTrack.add(i, container);
        }
        return roundTrack;
    }

    @Override
    public List<LightPlayer> getPlayers() {
        List<Player> players = user.getGame().getPlayers();
        List<LightPlayer> lightPlayers = new ArrayList<>();
        for (Player p : players) {
            lightPlayers.add(new LightPlayer(p.getUsername(),p.getGameId()));
        }
        return lightPlayers;
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
    public List<Commands> select(int dieIndex) throws IllegalActionException {
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
    public void exit() throws IllegalActionException {
        if (!user.isMyTurn()) {throw new IllegalActionException();}
        user.getGame().exit(true);
    }

    @Override
    public void quit() { user.quit(); }

    @Override
    public boolean pong() {
        return true;
    }
}
