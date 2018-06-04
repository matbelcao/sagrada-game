package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.toolaction.ToolAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Cards named "Tools" and their score calculating algorithms
 */
public class ToolCard extends Card {
    private static final String xmlTool = MasterServer.XML_SOURCE + "ToolCard.xml";
    private static final String xmlLogic = MasterServer.XML_SOURCE + "ToolLogic.xml";

    private boolean used;
    private ToolAction toolAction;
    public static final int NUM_TOOL_CARDS = 12;
    private Place from, to;
    private List<DieQuantity> quantity;
    private List<ModifyDie> modify;
    private IgnoredConstraint ignored_constraint;
    private Turn turn;

    private boolean executedFrom, executedModify1, executedSelect1, executedTo, executedModify2, executedSelect2;


    private Die selectedDie;


    /**
     * Constructs the card setting its id, name, description and use calculating algorithm
     *
     * @param id the id of the card
     */
    public ToolCard(int id) {
        super();
        super.xmlReader(id, xmlTool, "ToolCard");
        this.used = false;
        modify = new ArrayList<>();
        quantity = new ArrayList<>();

        String className = "ToolAction" + id;
        Class cls = null;
        toolReader(super.getId());
    }

    protected void toolReader(int id) {
        File xmlFile = new File(xmlLogic);
        String text;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            nodeList = doc.getElementsByTagName("ToolCard");

            for (int temp = 0; temp < nodeList.getLength() && (temp - 1) != id; temp++) {
                Element eElement = (Element) nodeList.item(temp);
                if (Integer.parseInt(eElement.getAttribute("id")) == id) {
                    from = Place.toPlace(eElement.getElementsByTagName("from").item(0).getTextContent());
                    to = Place.toPlace(eElement.getElementsByTagName("to").item(0).getTextContent());

                    for (int temp2 = 0; temp2 < eElement.getElementsByTagName("quantity").getLength(); temp2++) {
                        text = eElement.getElementsByTagName("quantity").item(temp2).getTextContent();
                        quantity.add(DieQuantity.toDieQuantity(text));
                    }

                    int temp2;
                    for (temp2 = 0; temp2 < eElement.getElementsByTagName("modify").getLength(); temp2++) {
                        text = eElement.getElementsByTagName("modify").item(temp2).getTextContent();
                        modify.add(ModifyDie.toModifyDie(text));
                    }
                    if (temp2 == 0) {
                        modify.add(ModifyDie.NONE);
                    }

                    try {
                        ignored_constraint = IgnoredConstraint.toIgnoredConstraint(eElement.getElementsByTagName("ignored-constraint").item(0).getTextContent());
                    } catch (NullPointerException e) {
                        ignored_constraint = IgnoredConstraint.NONE;
                    }

                    try {
                        turn = Turn.toTurn(eElement.getElementsByTagName("when").item(0).getTextContent());
                    } catch (NullPointerException e) {
                        turn = Turn.NONE;
                    }
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * Checks whether the player can or can not use the tool card, based on the cost in favor tokens
     *
     * @param player the player that wants to use the tool card
     * @return true iff the
     */
    public boolean canBeUsedBy(Player player) {
        int cost;
        if (used) cost = 2;
        else cost = 1;
        return player.getFavorTokens() >= cost;
    }

    public boolean enableToolCard(Player player, int turnFirstOrSecond) {
        try {
            if (!used) {
                used = true;
                player.decreaseFavorTokens(1);
            } else {
                player.decreaseFavorTokens(2);
            }
            if (this.getId() == 8) {
                if (turnFirstOrSecond == 1) {
                    return false;
                }
                player.setSkipsNextTurn(true);
            }
            discard();
            initStage();
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

    /**
     * Sets to true the stages to skip
     */
    public void initStage() {
        if (from.equals(to) && !from.equals(Place.SCHEMA)) {
            if (quantity.contains(DieQuantity.ALL)) {
                executedModify1 = true;
            }
            executedSelect1 = true;
            executedTo = true;
            executedModify2 = true;
            executedSelect2 = true;
            return;
        }
        if (from.equals(to) && from.equals(Place.SCHEMA)) {
            if (!quantity.contains(DieQuantity.TWO)) {
                executedSelect2 = true;
                executedTo = true;
            }
            executedModify1 = true;
            executedModify2 = true;
            return;
        }
        if (!from.equals(to) && to.equals(Place.SCHEMA)) {
            executedModify1 = true;
            executedTo = true;
            executedModify2 = true;
            executedSelect2 = true;
            return;
        }
        if (!from.equals(to) && !to.equals(Place.SCHEMA)) {
            executedSelect1 = true;
            executedSelect2 = true;
            return;
        }
    }

    public boolean stageFrom() {
        if (!executedFrom) {
            executedFrom = true;
            return true;
        }
        return false;
    }

    public boolean stageSelect1() {
        if (executedFrom && executedModify1 && !executedSelect1) {
            executedSelect1 = true;
            return true;
        }
        return false;
    }

    public boolean stageModify1() { //Die die,ModifyDie action
        if (executedFrom && executedSelect1 && !executedModify1) {
            executedModify1 = true;
        } else {
            return false;
        }
        return true;
    }

    public boolean stageTo() {
        if (!executedTo && executedModify1 && executedSelect1 && executedFrom) {
            executedTo = true;
            return true;
        }
        return false;
    }

    public boolean stageModify2() { //Die die,ModifyDie action, int shade
        if (executedTo && !executedModify2 && executedSelect2) {
            executedModify2 = true;
        } else {
            return false;
        }
        /*switch(action){
            case SETSHADE:
                die.setShade(shade);
                break;
            case SWAP:
                swapDie(selectedDie,die);
                break;
            default:
                break;
        }*/
        return true;
    }

    public boolean stageSelect2() {
        if (executedTo && executedModify2 && !executedSelect2) {
            executedSelect2 = true;
            return true;
        }
        return false;
    }

    public void discard() {
        selectedDie = null;
        executedFrom = false;
        executedSelect1 = false;
        executedModify1 = false;
        executedTo = false;
        executedModify2 = false;
        executedSelect2 = false;
    }

    public boolean modifyDie1(Die die, ModifyDie action){
    if(!modify.contains(action) || executedModify1){return false;}
    selectedDie=die;
    try {
        switch(action) {
            case DECREASE:
                die.decreaseShade();
                break;
            case INCREASE:
                die.increaseShade();
                break;
            case REROLL:
                die.reroll();
                break;
            default:
                return false;
        }
    }catch (IllegalShadeException e) {
        return false;
    }
    //to implement for tool no.11
    executedModify1=true;
    return true;
    }

    public boolean selectDie1(Die die){
        if(!stageModify1()){return false;}
        selectedDie=die;
        return true;
    }



    //setshade
    public boolean setShade(int shade) {
        if(!modify.contains(ModifyDie.SETSHADE) || !stageModify2()){return false;}

        selectedDie.setShade(shade);
        return true;
    }

    public boolean swapDie(Die die) {
        if(!modify.contains(ModifyDie.SWAP) || !stageModify2()){return false;}
        Color tmpColor = selectedDie.getColor();
        Face tmpFace = selectedDie.getShade();
        selectedDie.setShade(die.getShade().toInt());
        selectedDie.setColor(die.getColor().toString());
        die.setShade(tmpFace.toInt());
        die.setColor(tmpColor.toString());
        return true;
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

    public List<DieQuantity> getQuantity(){
        return quantity;
    }

    public List<ModifyDie> getModify(){
        return modify;
    }

    public IgnoredConstraint getIgnoredConstraint(){
        return ignored_constraint;
    }

    public Turn getTurn(){
        return turn;
    }

}
