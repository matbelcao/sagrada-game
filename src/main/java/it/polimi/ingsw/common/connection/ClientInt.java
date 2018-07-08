package it.polimi.ingsw.common.connection;

import it.polimi.ingsw.client.view.clientui.ClientUI;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.io.Serializable;
import java.util.List;

public interface ClientInt extends Serializable {
    ClientUI getClientUI();
    void addUpdateTask(Thread newUpdate);
    void updateGameStart(int numPlayers, int playerId);
    void updateGameEnd(List<RankingEntry> ranking);
    void updateGameRoundStart(int numRound);
    void updateGameRoundEnd(int numRound);
    void updateGameTurnStart(int playerId, boolean isFirstTurn);
    void updateGameTurnEnd(int playerId);
    void getBoardUpdates();
    void updatePlayerStatus(int playerId, GameEvent gameEvent, String username);
}
