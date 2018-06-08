package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClientParserTest {
    private static ArrayList<String> parsedResult = new ArrayList<>();
    private static String command;

    @Test
    void testCheckLogin(){

        assertTrue(ClientParser.parse("LOGIN ok", parsedResult));
        assertEquals("LOGIN",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));

        assertTrue(ClientParser.parse("LOGIN ko", parsedResult));
        assertEquals("LOGIN",parsedResult.get(0));
        assertEquals("ko",parsedResult.get(1));

        assertFalse(ClientParser.parse("LOGIN OK ok", parsedResult));
        assertFalse(ClientParser.parse("LOGIN", parsedResult));
    }

    @Test
    void testCheckLobby(){

        assertTrue(ClientParser.parse("LOBBY 1", parsedResult));
        assertEquals("LOBBY",parsedResult.get(0));
        assertEquals("1",parsedResult.get(1));

        assertFalse(ClientParser.parse("LOBBY", parsedResult));
        assertFalse(ClientParser.parse("LOBBY 1 1", parsedResult));
    }

    @Test
    void testCheckGame(){

        assertTrue(ClientParser.parse("GAME start 2 3", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("start",parsedResult.get(1));
        assertEquals("2",parsedResult.get(2));
        assertEquals("3",parsedResult.get(3));
        assertTrue(ClientParser.isGame("GAME start 2 3"));

        assertTrue(ClientParser.parse("GAME end 1,45,1", parsedResult));
        assertTrue(ClientParser.parse("GAME end 1,45,1 2,28,2", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("end",parsedResult.get(1));
        assertEquals("1,45,1",parsedResult.get(2));
        assertEquals("2,28,2",parsedResult.get(3));

        assertTrue(ClientParser.parse("GAME round_start 2", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("round_start",parsedResult.get(1));
        assertEquals("2",parsedResult.get(2));

        assertTrue(ClientParser.parse("GAME round_end 2", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("round_end",parsedResult.get(1));
        assertEquals("2",parsedResult.get(2));

        assertTrue(ClientParser.parse("GAME turn_start 2 3", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("turn_start",parsedResult.get(1));
        assertEquals("2",parsedResult.get(2));
        assertEquals("3",parsedResult.get(3));

        assertTrue(ClientParser.parse("GAME turn_end 3 2", parsedResult));
        assertEquals("GAME",parsedResult.get(0));
        assertEquals("turn_end",parsedResult.get(1));
        assertEquals("3",parsedResult.get(2));
        assertEquals("2",parsedResult.get(3));

        assertFalse(ClientParser.parse("GAME", parsedResult));
        assertFalse(ClientParser.parse("GAME start", parsedResult));
        assertFalse(ClientParser.parse("GAME start 3 3 3", parsedResult));
        assertFalse(ClientParser.parse("game start 2 3", parsedResult));
        assertFalse(ClientParser.parse("game START 2 3", parsedResult));
        assertFalse(ClientParser.parse("GAME end 1,45,1 2,2", parsedResult));
        assertFalse(ClientParser.parse("GAME round_start", parsedResult));
        assertFalse(ClientParser.parse("GAME round_end 2 2", parsedResult));
        assertFalse(ClientParser.parse("GAME round 2 2", parsedResult));
        assertFalse(ClientParser.parse("GAME turn_end 2 2 2", parsedResult));
        assertFalse(ClientParser.parse("GAME turn_start 2", parsedResult));
    }

    @Test
    void testCheckSend(){

        assertTrue(ClientParser.parse("SEND schema name 1 D,2,RED,THREE", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("name",parsedResult.get(2));
        assertEquals("1",parsedResult.get(3));
        assertEquals("D,2,RED,THREE",parsedResult.get(4));

        assertTrue(ClientParser.parse("SEND schema name 5 D,2,RED,THREE C,2,GREEN", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("name",parsedResult.get(2));
        assertEquals("D,2,RED,THREE",parsedResult.get(4));
        assertEquals("C,2,GREEN",parsedResult.get(5));

        assertTrue(ClientParser.parse("SEND priv 1 4 schemaName description", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("priv",parsedResult.get(1));
        assertEquals("1",parsedResult.get(2));
        assertEquals("4",parsedResult.get(3));
        assertEquals("schemaName",parsedResult.get(4));
        assertEquals("description",parsedResult.get(5));

        assertTrue(ClientParser.parse("SEND priv 4 schemaName description RED", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("priv",parsedResult.get(1));
        assertEquals("4",parsedResult.get(2));
        assertEquals("schemaName",parsedResult.get(3));
        assertEquals("description",parsedResult.get(4));

        assertTrue(ClientParser.parse("SEND draftpool 3,RED,TWO", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("draftpool",parsedResult.get(1));
        assertEquals("3,RED,TWO",parsedResult.get(2));

        assertTrue(ClientParser.parse("SEND draftpool 3,RED,TWO 2,GREEN,SIX", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("draftpool",parsedResult.get(1));
        assertEquals("3,RED,TWO",parsedResult.get(2));
        assertEquals("2,GREEN,SIX",parsedResult.get(3));

        assertTrue(ClientParser.parse("SEND tool 1 name desc true", parsedResult));

        assertTrue(ClientParser.parse("SEND favor_tokens 2", parsedResult));

        assertTrue(ClientParser.parse("SEND players 1,3", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("players",parsedResult.get(1));
        assertEquals("1,3",parsedResult.get(2));

        assertTrue(ClientParser.parse("SEND players 1,3 2,4 3,1", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("players",parsedResult.get(1));
        assertEquals("1,3",parsedResult.get(2));
        assertEquals("2,4",parsedResult.get(3));
        assertEquals("3,1",parsedResult.get(4));

        assertFalse(ClientParser.parse("SEND", parsedResult));
        assertFalse(ClientParser.parse("SEND schema name D,1,RED", parsedResult));
        assertFalse(ClientParser.parse("SEND schema name", parsedResult));
        assertFalse(ClientParser.parse("SEND schema C,2,THREE,FOUR D,2,GREEN", parsedResult));
        assertFalse(ClientParser.parse("SEND priv 1 4 schemaName description xxxx", parsedResult));
        assertFalse(ClientParser.parse("SEND priv", parsedResult));
        assertFalse(ClientParser.parse("send priv 1 4 schemaName description", parsedResult));
        assertFalse(ClientParser.parse("SEND draftpool 3,RED,TWO 2,GREEN,SIX,ONE", parsedResult));
        assertTrue(ClientParser.parse("SEND draftpool", parsedResult));
        assertFalse(ClientParser.parse("SEND players 1,3,4 2,4 3,1", parsedResult));
        assertTrue(ClientParser.parse("SEND players", parsedResult));
    }

    @Test
    void testCheckList(){
        assertTrue(ClientParser.parse("LIST_DICE 2,TWO,RED", parsedResult));
        assertTrue(ClientParser.parse("LIST_DICE 2,TWO,RED 5,THREE,GREEN", parsedResult));
        assertEquals("LIST_DICE",parsedResult.get(0));
        assertEquals("2,TWO,RED",parsedResult.get(1));
        assertEquals("5,THREE,GREEN",parsedResult.get(2));

        assertTrue(ClientParser.parse("LIST_OPTIONS PUT_DIE", parsedResult));
        assertTrue(ClientParser.parse("LIST_OPTIONS PUT_DIE NULL", parsedResult));
        assertEquals("LIST_OPTIONS",parsedResult.get(0));
        assertEquals("PUT_DIE",parsedResult.get(1));
        assertEquals("NULL",parsedResult.get(2));

        assertTrue(ClientParser.parse("CHOOSE_PLACEMENT 5", parsedResult));
        assertTrue(ClientParser.parse("CHOOSE_PLACEMENT 1 5 15", parsedResult));

        assertTrue(ClientParser.parse("LIST_DICE", parsedResult));
        assertFalse(ClientParser.parse("LIST_OPTIONS", parsedResult));
        assertTrue(ClientParser.parse("CHOOSE_PLACEMENT", parsedResult));
        assertFalse(ClientParser.parse("LIST 2,RED", parsedResult));
        assertFalse(ClientParser.parse("LIST_DICE 1,2", parsedResult));
        assertFalse(ClientParser.parse("LIST_DICE 1,2,greeN,TWO,SIX", parsedResult));
    }

    @Test
    void testCheckChoice(){

        assertTrue(ClientParser.parse("CHOICE ok", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));

        assertTrue(ClientParser.parse("CHOICE ko", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ko",parsedResult.get(1));

        assertFalse(ClientParser.parse("CHOICE", parsedResult));
        assertFalse(ClientParser.parse("CHOICE OK", parsedResult));
        assertFalse(ClientParser.parse("CHOICE ok ko", parsedResult));
    }

    @Test
    void testCheckTool(){

        assertTrue(ClientParser.isTool("TOOL ok"));
        assertTrue(ClientParser.parse("TOOL ok", parsedResult));
        assertEquals("TOOL",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));

        assertTrue(ClientParser.parse("TOOL ko", parsedResult));
        assertEquals("TOOL",parsedResult.get(0));
        assertEquals("ko",parsedResult.get(1));

        assertFalse(ClientParser.parse("TOOL", parsedResult));
        assertFalse(ClientParser.parse("TOOL OK", parsedResult));
        assertFalse(ClientParser.parse("TOOL ok ko", parsedResult));
    }

    @Test
    void testCheckPinf(){
        assertTrue(ClientParser.isPing("PING"));
        assertTrue(ClientParser.parse("PING", parsedResult));
        assertEquals("PING",parsedResult.get(0));

        assertFalse(ClientParser.parse("PING PING", parsedResult));
    }

    @Test
    void testCheckStatus(){

        assertTrue(ClientParser.parse("STATUS check", parsedResult));
        assertEquals("STATUS",parsedResult.get(0));
        assertEquals("check",parsedResult.get(1));

        assertTrue(ClientParser.parse("STATUS quit 1", parsedResult));
        assertEquals("STATUS",parsedResult.get(0));
        assertEquals("quit",parsedResult.get(1));
        assertEquals("1",parsedResult.get(2));

        assertFalse(ClientParser.parse("STATUS", parsedResult));
        assertFalse(ClientParser.parse("STATUS quit 1 3", parsedResult));
    }
}
