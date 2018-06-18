package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.util.List;

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
    public void notifyGameEnd(List<LightPlayer> players){
        for(LightPlayer p:players){
            LightPlayer player=client.getBoard().getPlayerById(p.getPlayerId());
            player.setPoints(p.getPoints());
            player.setFinalPosition(p.getFinalPosition());
        }
    }

    @Override
    public void notifyRoundEvent(String event,int roundNumber){
        if(event.equals("round_start")){
            client.updateGameRoundStart(roundNumber);
        }else if (event.equals("round_end")){
            client.updateGameRoundEnd(roundNumber);
        }
    }

    @Override
    public void notifyTurnEvent(String event,int playerId,int turnNumber){
        if(event.equals("turn_start")){
            client.updateGameTurnStart(playerId,turnNumber==0); //todo change signature
        }else if (event.equals("turn_end")){
            client.updateGameTurnEnd(playerId,turnNumber);
        }
    }

    @Override
    public void notifyStatusUpdate (String event,int id){
        switch (event) {
            case "reconnect":
                break;
            case "disconnect":
                break;
            case "quit":
                break;
        }
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
