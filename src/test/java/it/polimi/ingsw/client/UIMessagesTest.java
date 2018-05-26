package it.polimi.ingsw.client;

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

        System.out.printf(uimsg.getMessage("game-start"),4,2);

        UIMessages uita = new UIMessages(UILanguage.ita);
        System.out.printf(uita.getMessage("game-start"),4,2);
    }

}