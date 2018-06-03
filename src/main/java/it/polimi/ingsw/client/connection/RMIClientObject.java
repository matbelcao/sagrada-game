package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.connection.User;
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

    @Override
    public boolean pong() {
        return true;
    }

    @Override
    public void quit() { user.quit(); }

    @Override
    public LightSchemaCard getSchema(int playerId) throws IllegalActionException {
        return LightSchemaCard.toLightSchema(user.getGame().getUserSchemaCard(playerId,false)); //TODO check if the params have changed
    }

    @Override
    public LightCard getPrivateObject() {
        return LightCard.toLightCard(user.getGame().getPrivCard(user));
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
    public List<LightCard> getTools() {
        List<ToolCard> toolCards = user.getGame().getToolCards();
        List<LightCard> lightCards = new ArrayList<>();
        for (ToolCard c : toolCards) {
            lightCards.add(LightCard.toLightCard(c));
        }
        return lightCards;
    }

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
    public List<LightPlayer> getPlayers() {
        List<Player> players = user.getGame().getPlayers();
        List<LightPlayer> lightPlayers = new ArrayList<>();
        for (Player p : players) {
            lightPlayers.add(LightPlayer.toLightPlayer(p));
        }
        return lightPlayers;
    }

    @Override
    public int getFavorTokens(int playerId) {
        return user.getGame().getFavorTokens(user);
    }

    @Override
    public List<LightDie> getDraftPool() throws IllegalActionException {
         List<Die> draftPool = user.getGame().getDraftedDice(false);
         List<LightDie> lightDraftPool = new ArrayList<>();
         for(Die d : draftPool){
             lightDraftPool.add(new LightDie(d.getShade(),d.getColor()));
         }
         return lightDraftPool;
    }
}
