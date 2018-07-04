package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.connection.ClientInt;
import it.polimi.ingsw.common.connection.rmi_interfaces.RMIClientInt;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static it.polimi.ingsw.common.serializables.GameEvent.*;

public class RMIClientObject extends UnicastRemoteObject implements RMIClientInt {
    ClientInt client;
    public RMIClientObject(ClientInt client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void notifyLobbyUpdate(int n) {
        client.getClientUI().updateLobby(n);
    }

    @Override
    public void notifyGameStart(int n, int id) {
        client.addUpdateTask(new Thread(()->
            client.updateGameStart(n,id)
        ));
    }

    @Override
    public void notifyGameEnd(List<RankingEntry> ranking){
        client.addUpdateTask(new Thread(()->
            client.updateGameEnd(ranking)
        ));
    }

    @Override
    public void notifyRoundEvent(GameEvent gameEvent, int roundNumber){
        if(gameEvent.equals(ROUND_START)){
            client.addUpdateTask(new Thread(()->
                client.updateGameRoundStart(roundNumber)
            ));
        }else if (gameEvent.equals(ROUND_END)){
            client.addUpdateTask(new Thread(()->
                client.updateGameRoundEnd(roundNumber)
            ));
        }
    }

    @Override
    public void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber){
        if(gameEvent.equals(TURN_START)){
            client.addUpdateTask(new Thread(()->
                client.updateGameTurnStart(playerId,turnNumber==0)
            ));
        }else if (gameEvent.equals(TURN_END)){
            client.addUpdateTask(new Thread(()->
                client.updateGameTurnEnd(playerId)
            ));
        }
    }

    @Override
    public void notifyStatusUpdate (GameEvent gameEvent, int id, String userName){
        client.addUpdateTask(new Thread(()->
            client.updatePlayerStatus(id, gameEvent,userName)
        ));
    }

    @Override
    public void notifyBoardChanged(){
        client.addUpdateTask(new Thread(()->
            client.getBoardUpdates()
        ));
    }

    @Override
    public boolean ping() {
        client.getClass();
        return true;
    }
}
