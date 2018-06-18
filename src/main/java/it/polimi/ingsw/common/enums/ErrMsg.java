package it.polimi.ingsw.common.enums;

public enum ErrMsg {
    IS_NULL("is null"),
    LONGER_THAN_EXPECTED ( "already longer than length:"),
    INVALID_NEGATIVE_PARAM ("parameter is invalidly negative"),
    ERR("ERR:"),
    CLS_COMMAND_ERR ("error while executing cls command"),
    INTERRUPTED_READY_WAIT("error while waiting for board elements"),
    ERROR_RETRIEVING_CONSOLE("error while retrieving the system console"),
    INTERRUPTED_LOGIN_PROCEDURE("error while logging in"),
    COULDNT_LOG_BACK_IN("error while trying to log back in for a new match");


    private String errMsg;
    ErrMsg(String errMsg){
        this.errMsg=errMsg;

    }

    @Override
    public String toString() {
        return errMsg;
    }
}
