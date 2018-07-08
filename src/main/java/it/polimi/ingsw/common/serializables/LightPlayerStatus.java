package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.UserStatus;

/**
 * Enum of the status that a player can assume during a match
 */
public enum LightPlayerStatus {
    PLAYING,
    DISCONNECTED,
    QUITTED;

    public static LightPlayerStatus toLightPlayerStatus(UserStatus status){
        switch (status){
            case DISCONNECTED:
                return LightPlayerStatus.DISCONNECTED;
            case PLAYING:
                return LightPlayerStatus.PLAYING;
            default:
                return LightPlayerStatus.QUITTED;
        }
    }

}
