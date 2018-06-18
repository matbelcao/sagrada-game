package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UIMessagesTest {
    private static UIMessages uimsg;
    @BeforeAll
    static void setUp(){
        uimsg=new UIMessages(UILanguage.eng);
    }
    @Test
    void testGetMessage() {

        System.out.printf(uimsg.getMessage(UIMsg.GAME_START),4,2);

        UIMessages uita = new UIMessages(UILanguage.ita);
        System.out.printf(uita.getMessage(UIMsg.GAME_START),4,2);
    }

}