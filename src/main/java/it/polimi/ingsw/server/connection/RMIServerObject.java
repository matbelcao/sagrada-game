package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.util.List;

import static it.polimi.ingsw.common.serializables.GameEvent.*;

public class RMIServerObject  implements RMIServerInt {
    Client client;
    public RMIServerObject(Client client) {
        this.client = client;
    }

    @Override
    public void notifyLobbyUpdate(int n) {
        client.getClientUI().updateLobby(n);
    }

    @Override
    public void notifyGameStart(int n, int id) {
        client.updateGameStart(n,id);
    }

    @Override
    public void notifyGameEnd(List<RankingEntry> ranking){
        client.updateGameEnd(ranking);
    }

    @Override
    public void notifyRoundEvent(GameEvent gameEvent, int roundNumber){
        if(gameEvent.equals(ROUND_START)){
            client.updateGameRoundStart(roundNumber);
        }else if (gameEvent.equals(ROUND_END)){
            client.updateGameRoundEnd(roundNumber);
        }
    }

    @Override
    public void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber){
        if(gameEvent.equals(TURN_START)){
            client.updateGameTurnStart(playerId,turnNumber==0); //todo change signature
        }else if (gameEvent.equals(TURN_END)){
            client.updateGameTurnEnd(playerId);
        }
    }

    @Override
    public void notifyStatusUpdate (GameEvent gameEvent, int id, String userName){

        client.updatePlayerStatus(id, gameEvent,userName);
    }

    @Override
    public void notifyBoardChanged(){
        client.getUpdates();
    }

    @Override
    public boolean ping() {
        client.getClass();
        return true;
    }
}
