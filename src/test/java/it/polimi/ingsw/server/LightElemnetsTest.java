package it.polimi.ingsw.server;

import it.polimi.ingsw.common.serializables.LightSchemaCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LightElemnetsTest {
    @Test
    public void testToLightSchemaText(){
        LightSchemaCard schema= LightSchemaCard.toLightSchema("SEND schema Aurora_Sagradis 4 C,0,RED C,2,BLUE C,4,YELLOW C,5,FOUR C,6,PURPLE C,7,THREE C,8,GREEN C,9,TWO C,11,ONE C,13,FIVE C,17,SIX");
        assertEquals("Aurora Sagradis",schema.getName() );
    }
}
