package it.polimi.ingsw.common.enums;

public enum ErrMsg {
    IS_NULL("is null"),
    LONGER_THAN_EXPECTED ( "already longer than length:"),
    INVALID_NEGATIVE_PARAM ("parameter is invalidly negative"),
    ERR("ERR:"),
    CLS_COMMAND_ERR ("error while executing cls command"),
    INTERRUPTED_READY_WAIT("error while waiting for board elements"),
    INTERRUPTED_LOGIN_PROCEDURE("error while logging in");


    private String errMsg;
    ErrMsg(String errMsg){
        this.errMsg=errMsg;

    }

    @Override
    public String toString() {
        return errMsg;
    }
}
