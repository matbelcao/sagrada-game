package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.serializables.Event;
import it.polimi.ingsw.common.serializables.LightPlayer;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.util.List;

import static it.polimi.ingsw.common.serializables.Event.*;

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
    public void notifyRoundEvent(Event event, int roundNumber){
        if(event.equals(ROUND_START)){
            client.updateGameRoundStart(roundNumber);
        }else if (event.equals(ROUND_END)){
            client.updateGameRoundEnd(roundNumber);
        }
    }

    @Override
    public void notifyTurnEvent(Event event,int playerId,int turnNumber){
        if(event.equals(TURN_START)){
            client.updateGameTurnStart(playerId,turnNumber==0); //todo change signature
        }else if (event.equals(TURN_END)){
            client.updateGameTurnEnd(playerId,turnNumber);
        }
    }

    @Override
    public void notifyStatusUpdate (Event event,int id){

        client.updatePlayerStatus(id,event);
    }

    @Override
    public void notifyBoardChanged(){
        client.getUpdates();
    }

    @Override
    public void close(){

    }

    @Override
    public boolean ping() {
        return true;
    }
}
