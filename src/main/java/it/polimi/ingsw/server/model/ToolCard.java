package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.toolaction.ToolAction;
import javafx.scene.control.IndexedCell;
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
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Cards named "Tools" and their score calculating algorithms
 */
public class ToolCard extends Card{
    private boolean used;
    private ToolAction toolAction;
    public static final int NUM_TOOL_CARDS=12;
    private Place from,to;
    private DieQuantity quantity;
    private ModifyDie modify;
    private IgnoredConstraint ignored_constraint;
    private String when;

    private boolean executedFrom,executedModify1,executeSelect,executedTo,executedModify2;


    private Die selectedDie;


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
        toolReader(super.getId());
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
                    from=Place.toPlace(eElement.getElementsByTagName("from").item(0).getTextContent());
                    to= Place.toPlace(eElement.getElementsByTagName("to").item(0).getTextContent());
                    quantity=DieQuantity.toDieQuantity(eElement.getElementsByTagName("quantity").item(0).getTextContent());
                    if(id==1 || id==5 || id==6 || id==7 || id==10 || id==11){
                        modify=ModifyDie.toModifyDie(eElement.getElementsByTagName("modify").item(0).getTextContent());
                    }else{
                        modify=ModifyDie.NONE;
                    }
                    if(id==2 || id==3 || id==4 || id==9 || id==11){
                        ignored_constraint=IgnoredConstraint.toIgnoredConstraint(eElement.getElementsByTagName("ignored-constraint").item(0).getTextContent());
                    }else{
                        ignored_constraint=IgnoredConstraint.NONE;
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
    public boolean useTool(Player player) {


        if (this.getId()==8){ player.setSkipsNextTurn(true); }
        return true;
    }

    public boolean enableToolCard(Player player,int turnFirstOrSecond){
        try {
            if (!used) {
                used = true;
                player.decreaseFavorTokens(1);
            } else {
                player.decreaseFavorTokens(2);
            }
            if (this.getId()==8){
                if(turnFirstOrSecond==1){return false;}
                player.setSkipsNextTurn(true);
            }
            executedFrom=false;
            executedTo=false;
            executedModify1=false;
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }


    /*public List<IndexedCellContent> execFrom(Board board,Player player) throws IllegalActionException {
        if(executedFrom){throw new IllegalActionException();}
        List<IndexedCellContent> indexedList=execFromOrTo(board,player, from);

        executedFrom=true;
        if(from==Place.SCHEMA){
            executedModify1=true;
            executedTo=true;
        }else{
            executeSelect=true;
        }
        return indexedList;
    }*/

    /*public List<IndexedCellContent> execTo(Board board,Player player, Place where) throws IllegalActionException {
        if(!(executeSelect && executedModify1)){throw new IllegalActionException();}
        List<IndexedCellContent> indexedList=execFromOrTo(board,player, where);

        executedTo=true;
        return indexedList;
    }*/



    public void execSelect(){
        executeSelect=true;
    }

    //CHOOSE die.....
    public boolean execModify1(Die die){
        if(executedModify1){return false;}
        selectedDie=die;
        try {
            switch(modify) {
            case DECREASE:
                selectedDie.decreaseShade();
                break;
            case INCREASE:
                selectedDie.increaseShade();
                break;
            case REROLL:
                selectedDie.reroll();
                break;
            case NONE:
                break;
            }
        }catch (IllegalShadeException e) {
            return false;
        }
        //to implement for tool no.11
        executedModify1=true;
        return true;
    }

    public boolean execModify2(Die die,int shade){
        if(executedModify2){return false;}
        switch(modify){
            case SETSHADE:
                die.setShade(shade);
                break;
            case SWAP:
                swapDie(selectedDie,die);
                break;
            default:
                return false;
        }
        return true;
    }


    //SWAP DIE
    public void swapDie(Die die1,Die die2) {
        Color tmpColor = die1.getColor();
        Face tmpFace = die1.getShade();
        die1.setShade(die2.getShade().toInt());
        die1.setColor(die2.getColor().toString());
        die2.setShade(tmpFace.toInt());
        die2.setColor(tmpColor.toString());
    }

    public void discard(){
        selectedDie=null;
        executedFrom=false;
        executedTo=false;
        executeSelect=false;
        executedModify1=false;
        executedModify2=false;
    }

    /**
     * This method provide the information about if the card has been yet used
     * @return true iff has been used yet
     */
    public boolean hasAlreadyUsed(){
        return this.used;
    }

    public Place getFrom(){
        return from;
    }

    public Place getTo(){
        return to;
    }

    public IgnoredConstraint getIgnoredConstraint(){
        return ignored_constraint;
    }

}
