package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    public static final int NUM_TOOL_CARDS = 12;
    private Place from;
    private Place to;
    private List<DieQuantity> quantity;
    private List<ModifyDie> modify;
    private IgnoredConstraint ignored_constraint;
    private Turn turn;
    //states
    private boolean executedFrom;
    private boolean executedModify1;
    private boolean executedSelect1;
    private boolean executedTo;
    private boolean executedModify2;
    private boolean executedSelect2;


    private int numSelectedDice;
    private SchemaCard schemaTemp;
    private List<Die> selectedDice;
    private List<Integer> selectedIndex;


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

    public boolean enableToolCard(Player player, int turnFirstOrSecond, SchemaCard schema) {
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
            selectedDice=new ArrayList<>();
            selectedIndex=new ArrayList<>();
            schemaTemp=schema.cloneSchema();
            numSelectedDice=0;
            discard();
            initStage();
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

    /**
     * Sets to true the stages to be skipped
     */
    public void initStage() {

        // set to true if they can't be executed
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

    public boolean stageFrom(Place gamePlace) {
        if (!executedFrom && gamePlace.equals(from)) {
            executedFrom = true;
            return true;
        }
        return false;
    }

    public boolean canSelect1() {
        if (executedFrom && executedModify1 && !executedSelect1) {
            return true;
        }
        return false;
    }

    public boolean canModify1() { //Die die,ModifyDie action
        if (executedFrom && executedSelect1 && !executedModify1) {
            return true;
        } else {
            return false;
        }

    }

    public boolean stageTo(Place gamePlace) {
        if (!executedTo && executedModify1 && executedSelect1 && executedFrom && gamePlace.equals(to)) {
            executedTo = true;
            return true;
        }
        return false;
    }

    public boolean canModify2() { //Die die,ModifyDie action, int shade
        if (executedTo && !executedModify2 && executedSelect2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canSelect2() {
        if (executedTo && executedModify2 && !executedSelect2) {
            return true;
        }
        return false;
    }

    public boolean canSelectDie() {
        if(canSelect1()){
            return true;
        }
        if(canSelect2()){
            return true;
        }
        return false;
    }

    //only for tool that place two die in the same turn
    public boolean isExternalSchemaPlacement(){
        if(!from.equals(Place.SCHEMA) && to.equals(Place.SCHEMA)){
            return true;
        }
        return false;
    }

    public boolean isInternalSchemaPlacement() {
        if(from.equals(Place.SCHEMA) && to.equals(Place.SCHEMA)){
            return true;
        }
        return false;
    }

    public boolean placementsCompleted(){
        if(quantity.equals(DieQuantity.ONE) && executedSelect1){
            return true;
        }
        if(quantity.equals(DieQuantity.TWO) && executedSelect1 && executedSelect2){
            return true;
        }
        return false;
    }

    public SchemaCard getNewSchema(){
        return schemaTemp;
    }

    public List<Die> getToolDice(){
        return selectedDice;
    }

    public List<Integer> getDiceIndexes(){
        return selectedIndex;
    }

    public void discard() {
        executedFrom = false;
        executedSelect1 = false;
        executedModify1 = false;
        executedTo = false;
        executedModify2 = false;
        executedSelect2 = false;
    }

    public boolean modifyDie1(Die die, ModifyDie action){
    if(!modify.contains(action) || !canModify1()){return false;}
    selectedDice.add(die);
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


    public boolean selectDie(int index){
        if(canSelect1()) {
            selectedDice.add(schemaTemp.getCell(index).getDie());
            schemaTemp.removeDie(index);
            selectedIndex.add(index);
            return true;
        }else if(canSelect2() && !selectedDice.get(0).equals(schemaTemp.getCell(index).getDie()) && selectedIndex.get(0)!=index){
            selectedDice.add(schemaTemp.getCell(index).getDie());
            schemaTemp.removeDie(index);
            selectedIndex.add(index);
            return true;
        }
        return false;
    }

    //For schema to schema
    public boolean virtualPlacement(int index) {
        if(!canSelectDie()){return false;}
        if(selectedDice.size()>0){
            try {
                schemaTemp.putDie(index,selectedDice.get(0),ignored_constraint);
                selectedDice.remove(0);
                selectedIndex.remove(0);
            } catch (IllegalDieException e) {
                return false;
            }
            if(canSelect1()) {
                executedSelect1=true;
            }else{
                executedSelect2=true;
            }
            return true;
        }
        return false;
    }

    //for other to schema
    public boolean externalVirtualPlacement(int index){

        return false;
    }



    //setshade
    public boolean setShade(int shade) {
        if(!modify.contains(ModifyDie.SETSHADE) || !canModify2()){return false;}

        selectedDice.get(0).setShade(shade);
        executedModify2=true;
        return true;
    }

    public boolean swapDie(Die die) {
        if(!modify.contains(ModifyDie.SWAP) || !canModify2()){return false;}
        Color tmpColor = selectedDice.get(0).getColor();
        Face tmpFace = selectedDice.get(0).getShade();
        selectedDice.get(0).setShade(die.getShade().toInt());
        selectedDice.get(0).setColor(die.getColor().toString());
        die.setShade(tmpFace.toInt());
        die.setColor(tmpColor.toString());
        executedModify2=true;
        return true;
    }



    /**
     * This method provide the information about if the card has been yet used
     * @return true iff has been used yet
     */
    public boolean isAlreadyUsed(){
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
