package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.server.connection.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
    public LightCard getPublicObj() throws RemoteException {
        return null; //TODO implement
    }




    /*@Override
    public ArrayList<LightCard> getPublicObj() throws RemoteException {
        ArrayList<LightCard> lightCards = new ArrayList<>();
        ArrayList<PubObjectiveCard> cards = (ArrayList)user.getGame().getPubCards();
        for (PubObjectiveCard c : cards) {
            lightCards.add(LightCard.toLightCard(c));
        }
        return lightCards;
    }*/
}
