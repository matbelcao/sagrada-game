package it.polimi.ingsw.client.view.clientui.uielements.enums;

public enum CLIFormats {
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
    POINTS("points"), ROUND_INFO("round-info");

    private String clielem;
    CLIFormats(String clielem){
        this.clielem = clielem;

    }

    @Override
    public String toString() {
        return clielem;
    }

}
