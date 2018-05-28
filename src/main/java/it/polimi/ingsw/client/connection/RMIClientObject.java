package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.ToolCard;

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
    public boolean pong() throws RemoteException {
        return true;
    }

    @Override
    public void quit() throws RemoteException {
        user.quit();
    }

    @Override
    public LightSchemaCard getSchema(int playerId) throws RemoteException {
        //return LightSchemaCard.toLightSchema(user.getGame().getUserSchemaCard(playerId,true));
        return null;
    }

    @Override
    public LightCard getPrivateObj() throws RemoteException {
        return LightCard.toLightCard(user.getGame().getPrivCard(user));
    }

    @Override
    public List<LightCard> getPublicObjs() throws RemoteException {
        List<LightCard> lightCards = new ArrayList<>();
        List<PubObjectiveCard> cards = user.getGame().getPubCards();
        for (PubObjectiveCard c : cards) {
            lightCards.add(LightCard.toLightCard(c));
        }
        return lightCards;
    }

    @Override
    public List<LightCard> getTools() throws RemoteException {
        List<ToolCard> toolCards = user.getGame().getToolCards();
        List<LightCard> lightCards = new ArrayList<>();
        for (ToolCard c : toolCards) {
            lightCards.add(LightCard.toLightCard(c));
        }
        return lightCards;
    }
}
