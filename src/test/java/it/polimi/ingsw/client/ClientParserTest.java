package it.polimi.ingsw.client;
import it.polimi.ingsw.client.connection.ClientParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

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

        assertTrue(ClientParser.parse("SEND schema D,9,5,RED,THREE", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("D,9,5,RED,THREE",parsedResult.get(2));

        assertTrue(ClientParser.parse("SEND schema E,2,3 D,4,5,RED,THREE C,2,1,GREEN", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("E,2,3",parsedResult.get(2));
        assertEquals("D,4,5,RED,THREE",parsedResult.get(3));
        assertEquals("C,2,1,GREEN",parsedResult.get(4));

        assertTrue(ClientParser.parse("SEND priv 1 4 schemaName description", parsedResult));
        assertEquals("SEND",parsedResult.get(0));
        assertEquals("priv",parsedResult.get(1));
        assertEquals("1",parsedResult.get(2));
        assertEquals("4",parsedResult.get(3));
        assertEquals("schemaName",parsedResult.get(4));
        assertEquals("description",parsedResult.get(5));

        assertTrue(ClientParser.parse("SEND priv 4 schemaName description", parsedResult));
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
        assertTrue(ClientParser.parse("SEND schema D,9,5,RED", parsedResult));
        assertFalse(ClientParser.parse("SEND schema", parsedResult));
        assertFalse(ClientParser.parse("SEND schema C,2,3 E,4,5,RED,THREE D,2,1,GREEN", parsedResult));
        assertFalse(ClientParser.parse("SEND priv 1 4 schemaName description xxxx", parsedResult));
        assertFalse(ClientParser.parse("SEND priv", parsedResult));
        assertFalse(ClientParser.parse("send priv 1 4 schemaName description", parsedResult));
        assertFalse(ClientParser.parse("SEND draftpool 3,RED,TWO 2,GREEN,SIX,ONE", parsedResult));
        assertFalse(ClientParser.parse("SEND draftpool", parsedResult));
        assertFalse(ClientParser.parse("SEND players 1,3,4 2,4 3,1", parsedResult));
        assertFalse(ClientParser.parse("SEND players", parsedResult));
    }

    @Test
    void testCheckList(){

        assertTrue(ClientParser.parse("LIST schema 2,3,3,RED,TWO", parsedResult));
        assertEquals("LIST",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("2,3,3,RED,TWO",parsedResult.get(2));

        assertTrue(ClientParser.parse("LIST schema 2,3,3,RED,TWO 4,5,2,GREEN,FIVE", parsedResult));
        assertEquals("LIST",parsedResult.get(0));
        assertEquals("schema",parsedResult.get(1));
        assertEquals("2,3,3,RED,TWO",parsedResult.get(2));
        assertEquals("4,5,2,GREEN,FIVE",parsedResult.get(3));

        assertTrue(ClientParser.parse("LIST placements 2,3", parsedResult));
        assertEquals("LIST",parsedResult.get(0));
        assertEquals("placements",parsedResult.get(1));
        assertEquals("2,3",parsedResult.get(2));

        assertTrue(ClientParser.parse("LIST placements 2,3 4,5 1,2", parsedResult));
        assertEquals("LIST",parsedResult.get(0));
        assertEquals("placements",parsedResult.get(1));
        assertEquals("2,3",parsedResult.get(2));
        assertEquals("4,5",parsedResult.get(3));
        assertEquals("1,2",parsedResult.get(4));

        assertTrue(ClientParser.parse("LIST tool_details 1 3 true ok", parsedResult));
        assertEquals("LIST",parsedResult.get(0));
        assertEquals("tool_details",parsedResult.get(1));
        assertEquals("1",parsedResult.get(2));
        assertEquals("3",parsedResult.get(3));
        assertEquals("true",parsedResult.get(4));
        assertEquals("ok",parsedResult.get(5));

        assertFalse(ClientParser.parse("LIST schema", parsedResult));
        assertFalse(ClientParser.parse("LIST", parsedResult));
        assertFalse(ClientParser.parse("LIST schema  2,3,3,RED,TWO 2,3,3,TWO", parsedResult));
        assertFalse(ClientParser.parse("LIST schema 2,3,3,RED,TWO 4,5,2,GREEN,FIVE,TWO", parsedResult));
        assertFalse(ClientParser.parse("LIST placements", parsedResult));
        assertFalse(ClientParser.parse("LIST placements 2,3,5", parsedResult));
        assertFalse(ClientParser.parse("LIST tool_details", parsedResult));
        assertFalse(ClientParser.parse("LIST tool_details 1 3 true ok ko", parsedResult));
    }

    @Test
    void testCheckDiscard(){

        assertTrue(ClientParser.parse("DISCARD ack", parsedResult));
        assertEquals("DISCARD",parsedResult.get(0));
        assertEquals("ack",parsedResult.get(1));

        assertFalse(ClientParser.parse("DISCARD", parsedResult));
        assertFalse(ClientParser.parse("DISCARD ack xxx", parsedResult));
    }

    @Test
    void testCheckChoice(){

        assertTrue(ClientParser.parse("CHOICE ok", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));

        assertTrue(ClientParser.parse("CHOICE ko", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ko",parsedResult.get(1));

        assertTrue(ClientParser.parse("CHOICE ok modified_die RED,THREE", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));
        assertEquals("modified_die",parsedResult.get(2));
        assertEquals("RED,THREE",parsedResult.get(3));

        //assertTrue(ClientParser.parse("CHOICE ok modified_die RED", parsedResult));

        assertTrue(ClientParser.parse("CHOICE ok rerolled_dice", parsedResult));
        assertEquals("CHOICE",parsedResult.get(0));
        assertEquals("ok",parsedResult.get(1));
        assertEquals("rerolled_dice",parsedResult.get(2));

        assertFalse(ClientParser.parse("CHOICE", parsedResult));
        assertFalse(ClientParser.parse("CHOICE ok ko", parsedResult));
        assertTrue(ClientParser.parse("CHOICE ok modified_die RED", parsedResult));
        assertFalse(ClientParser.parse("CHOICE ok modified_die RED,THREE,4", parsedResult));
        assertFalse(ClientParser.parse("CHOICE ok rerolled_dice 3 4", parsedResult));
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
