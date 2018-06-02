package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.ModifyDie;
import it.polimi.ingsw.common.enums.DieQuantity;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.toolaction.ToolAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the Cards named "Tools" and their score calculating algorithms
 */
public class ToolCard extends Card{
    private boolean used;
    private ToolAction toolAction;
    public static final int NUM_TOOL_CARDS=12;
    Place from,to;
    DieQuantity quantity;
    ModifyDie modify;
    String when,ignored_constraint;


    /**
     * Constructs the card setting its id, name, description and use calculating algorithm
     * @param id the id of the card
     * @param xmlSrc the address to the xml file containing necessary information to initialize the cards
     */
    public ToolCard(int id, String xmlSrc){
        super();
        super.xmlReader(id,xmlSrc,"ToolCard");
        this.used=false;

        String className = "ToolAction" + id;
        String fullPathOfTheClass = "it.polimi.ingsw.server.model.toolaction." + className;
        Class cls = null;
        try {
            cls = Class.forName(fullPathOfTheClass);
            assert cls != null;
            this.toolAction = (ToolAction) cls.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected void toolReader(int id){
        File xmlFile= new File(MasterServer.XML_SOURCE+"ToolLogic.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            nodeList = doc.getElementsByTagName("ToolCard");

            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {
                Element eElement = (Element)nodeList.item(temp);
                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    this.from=Place.toPlace(eElement.getElementsByTagName("from").item(0).getTextContent());
                    this.to= Place.toPlace(eElement.getElementsByTagName("to").item(0).getTextContent());
                    this.quantity=DieQuantity.toDieQuantity(eElement.getElementsByTagName("quantity").item(0).getTextContent());
                    if(id==1 || id==5 || id==6 || id==7 || id==10 || id==11){
                        this.modify=ModifyDie.toModifyDie(eElement.getElementsByTagName("modify").item(0).getTextContent());
                    }
                    if(id==2 || id==3 || id==4 || id==9 || id==11){
                        ignored_constraint=eElement.getElementsByTagName("ignored-constraint").item(0).getTextContent();
                    }
                    if(id==7 || id==8 || id==12){
                        when=eElement.getElementsByTagName("when").item(0).getTextContent();
                    }


                }
            }
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * Checks whether the player can or can not use the tool card, based on the cost in favor tokens
     * @param player the player that wants to use the tool card
     * @return true iff the
     */
    public boolean canBeUsedBy(Player player){
        int cost;
        if (used) cost = 2;
        else cost = 1;
        return player.getFavorTokens() >= cost;
    }

    /**
     * This method calls the card-specific method for using the Tool
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    public boolean useTool(Player player) throws NegativeTokensException {
        if(!used){
            used =true;
            player.decreaseFavorTokens(1);
        }else{
            player.decreaseFavorTokens(2);
        }

        if (this.getId()==8){ player.setSkipsNextTurn(true); }
        return toolAction.useToolCard(player);
    }

    private boolean modifyDie(Die die){
        Die newDie;
        switch (modify){
            case FLIP:
                die.flipShade();
                return true;
            case REROLL:
                die.reroll();
                return true;
            default:
                return false;
        }
    }

    private boolean modifyDie(Die die1,Die die2){

        return false;
    }

    /**
     * This method provide the information about if the card has been yet used
     * @return true iff has been used yet
     */
    public boolean hasAlreadyUsed(){
        return this.used;
    }
}
