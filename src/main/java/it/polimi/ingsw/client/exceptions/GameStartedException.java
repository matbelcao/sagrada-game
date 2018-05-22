package it.polimi.ingsw.client.exceptions;

public class GameStartedException extends Exception{
    private int num_players;
    private int player_id;

    public GameStartedException(int num_players,int player_id){
        this.num_players=num_players;
        this.player_id=player_id;
    }

    public int getNumPlayers() {
        return num_players;
    }

    public int getPlayerId() {
        return player_id;
    }
}
