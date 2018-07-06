package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This class checks the correct retrieval of the messages for the UI
 */
class UIMessagesTest {
    private static UIMessages uimsg;
    @BeforeAll
    static void setUp(){
        uimsg=new UIMessages(UILanguage.ENG);
    }

    /**
     * This class checks the correct retrieval of the messages from the xml file
     */
    @Test
    void testGetMessage() {

        System.out.printf(uimsg.getMessage(UIMsg.GAME_START),4,2);

        UIMessages uita = new UIMessages(UILanguage.ITA);
        System.out.printf(uita.getMessage(UIMsg.GAME_START),4,2);
    }

}