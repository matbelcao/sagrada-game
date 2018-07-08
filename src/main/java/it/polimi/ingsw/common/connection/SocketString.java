package it.polimi.ingsw.common.connection;

/**
 * Utility class containing the strings used during the Socket session
 */
public class SocketString {
    public static final String LOGIN="LOGIN";
    public static final String LOBBY="LOBBY";
    public static final String INVALID="INVALID";
    public static final String INVALID_MESSAGE="INVALID message";
    public static final String ILLEGAL="ILLEGAL";
    public static final String ILLEGAL_ACTION="ILLEGAL ACTION!";
    public static final String STATUS="STATUS";

    public static final String GAME="GAME";
    public static final String START="start";
    public static final String END="end";
    public static final String ROUND_START="round_start";
    public static final String ROUND_END="round_end";
    public static final String TURN_START="turn_start";
    public static final String TURN_END="turn_end";
    public static final String END_TURN="end_turn";
    public static final String BOARD_CHANGED="board_changed";
    public static final String GAME_START=GAME+" "+START+" ";
    public static final String GAME_END=GAME+" "+END;
    public static final String GAME_END_TURN=GAME+" "+END_TURN;

    public static final String GET="GET";
    public static final String SEND="SEND";
    public static final String SCHEMA="schema";
    public static final String DRAFTED="draft";
    public static final String TOKENS="favor_tokens";
    public static final String PRIVATE="priv";
    public static final String PUBLIC="pub";
    public static final String TOOLCARD="tool";
    public static final String DRAFTPOOL="draftpool";
    public static final String ROUNDTRACK="roundtrack";
    public static final String PLAYERS="players";
    public static final String GAME_STATUS="game_status";
    public static final String GET_SCHEMA=GET+" "+SCHEMA+" ";
    public static final String GET_TOKENS=GET+" "+TOKENS+" ";
    public static final String GET_PRIVATE=GET+" "+PRIVATE;
    public static final String GET_PUBLIC=GET+" "+PUBLIC;
    public static final String GET_TOOLCARD=GET+" "+TOOLCARD;
    public static final String GET_DRAFTPOOL=GET+" "+DRAFTPOOL;
    public static final String GET_ROUNDTRACK=GET+" "+ROUNDTRACK;
    public static final String GET_PLAYERS=GET+" "+PLAYERS;
    public static final String GET_GAME_STATUS=GET+" "+GAME_STATUS;
    public static final String SEND_SCHEMA=SEND+" "+SCHEMA+" ";
    public static final String SEND_TOKENS=SEND+" "+TOKENS+" ";
    public static final String SEND_PRIVATE=SEND+" "+PRIVATE+" ";
    public static final String SEND_PUBLIC=SEND+" "+PUBLIC+" ";
    public static final String SEND_TOOLCARD=SEND+" "+TOOLCARD+" ";
    public static final String SEND_DRAFTPOOL=SEND+" "+DRAFTPOOL;
    public static final String SEND_ROUNDTRACK=SEND+" "+ROUNDTRACK;
    public static final String SEND_PLAYERS=SEND+" "+PLAYERS;
    public static final String SEND_GAME_STATUS=SEND+" "+GAME_STATUS+" ";

    public static final String GET_DICE_LIST="GET_DICE_LIST";
    public static final String LIST_DICE="LIST_DICE";
    public static final String SELECT="SELECT";
    public static final String LIST_OPTIONS="LIST_OPTIONS";
    public static final String GET_PLACEMENTS_LIST="GET_PLACEMENTS_LIST";
    public static final String LIST_PLACEMENTS="LIST_PLACEMENTS";
    public static final String DIE="D";
    public static final String CONSTRAINT="C";

    public static final String CHOOSE="CHOOSE";
    public static final String CHOICE="CHOICE";
    public static final String BACK="BACK";
    public static final String DISCARD="DISCARD";
    public static final String NEW_MATCH="new_match";
    public static final String GAME_NEW_MATCH=GAME+" "+NEW_MATCH;
    public static final String QUIT="QUIT";
    public static final String OK="ok";
    public static final String KO="ko";

    public static final String TOOL="TOOL";
    public static final String CONTINUE="can_continue";
    public static final String ENABLE="enable";
    public static final String TOOL_ENABLE=TOOL+" "+ENABLE+" ";
    public static final String TOOL_CONTINUE=TOOL+" "+CONTINUE;

    public static final String PING="PING";
    public static final String PONG="PONG";
    public static final String CONNECTION_ESTABLISHED = "Connection established!";
    public static final String LOGIN_OK = LOGIN+" "+OK;
    public static final String LOGIN_KO = LOGIN + " " + KO;

    private SocketString(){
        //Not implemented
    }
}
