package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.UserStatus;

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
