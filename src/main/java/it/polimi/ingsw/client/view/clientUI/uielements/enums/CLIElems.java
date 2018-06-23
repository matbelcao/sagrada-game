package it.polimi.ingsw.client.view.clientUI.uielements.enums;

public enum CLIElems {
    EMPTY("EMPTI"),
    FILLED("FILLED"),
    CLIENT_INFO("player-info"),
    USERNAME_ID("username-id"),
    TOKENS_INFO("tokens-info"),
    TOOL_INDEX("tool-index"),
    ROUND_TURN("round-turn"),
    ROW_COL("row-col"),
    INDEX("index"),
    LIST_ELEMENT("li"),
    POINT_LEFT("point-left"),
    PROMPT("prompt"),
    SCHEMA_COLUMNS("schema-cols"),
    SAGRADA_WALL("sagrada-wall"),
    LOGIN_LINE("login-line"),
    LOBBY("lobby"),
    DRAFTED_INFO("drafted-info"),
    POINTS("points");

    private String clielem;
    CLIElems(String clielem){
        this.clielem = clielem;

    }

    @Override
    public String toString() {
        return clielem;
    }

}
