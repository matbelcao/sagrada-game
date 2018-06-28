package it.polimi.ingsw.client.view.clientUI.uielements.enums;

public enum UIMsg {
    CONNECTION_OK("connection-ok"),
    LOGIN_USERNAME("login-username"),
    LOGIN_PASSWORD("login-password"),
    LOGIN_OK("login-ok"),
    LOGIN_KO("login-ko"),
    LOBBY_UPDATE("lobby-update"),
    GAME_START("game-start"),
    WAIT_FOR_GAME_START("wait-for-game-start"),
    CONNECTED_VIA("connected-via"),
    PLAYER_NUMBER("player-number"),
    PUBLIC_OBJ("pub-obj"),
    PRIVATE_OBJ("priv-obj"),
    TOOLS("tools"),
    REMAINING_TOKENS("remaining-tokens"),
    TOOL_NUMBER("tool-number"),
    ROUND("round"),
    ROUND_NUMBER("round-number"),
    TURN_OF("turn-of"),
    YOUR_TURN("your-turn"),
    DRAFTPOOL("draftpool"),
    ROUNDTRACK("roundtrack"),
    SCHEMA("schema"),
    CAN_BE_PLACED("can-be-placed"),
    ROW("row"),
    COL("col"),
    POS("pos"),
    PROMPT_MESSAGE("prompt-message"),
    QUIT_OPTION("quit-option"),
    DISCARD_OPTION("discard-option"),
    END_TURN_OPTION("endturn-option"),
    BACK_OPTION("back-option"),
    NEW_GAME_OPTION("newgame-option"),
    CHOOSE_FROM_DICE_LIST("choose-from-dice-list"),
    PLACE_DIE("place-die"),
    USE_TOOL("use-tool"),
    CHOOSE_SCHEMA("choose-schema"),
    CHOOSE_SCHEMA_2("choose-schema-2"),
    CHOOSE_TOOL("choose-tool"),
    MAIN_CHOICE("main-choice"),
    NOT_MY_TURN("not-my-turn"),
    FIRST_TURN("first-turn"),
    SECOND_TURN("second-turn"),
    BROKEN_CONNECTION("broken-connection"),
    CLOSED_CONNECTION("closed-connection"),
    QUITTED("quitted"),
    DISCONNECTED("disconnected"), GAME_END("game-end"),
    SCORE("score"),
    PLAYER_SCORE("player-score");




    private String uiMsg;
    UIMsg(String uiMsg){
        this.uiMsg=uiMsg;

    }

    @Override
    public String toString() {
        return uiMsg;
    }


}
