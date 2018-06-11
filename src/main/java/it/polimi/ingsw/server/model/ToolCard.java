package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
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
import java.util.Random;

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
    private List<Commands> actions;
    private IgnoredConstraint ignored_constraint;
    private List<DieQuantity> quantity;
    private Turn turn;
    //states


    Color constraint; //only for 12
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
        actions = new ArrayList<>();
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

                    for (int temp2 = 0; temp2 < eElement.getElementsByTagName("command").getLength(); temp2++) {
                        text = eElement.getElementsByTagName("command").item(temp2).getTextContent().toUpperCase();
                        actions.add(Commands.valueOf(text.toUpperCase().trim()));
                    }

                    if(to.equals(Place.SCHEMA)){
                        ignored_constraint = IgnoredConstraint.toIgnoredConstraint(eElement.getElementsByTagName("ignored-constraint").item(0).getTextContent());
                    }else{
                        ignored_constraint = IgnoredConstraint.NONE;
                    }

                    for (int temp2 = 0; temp2 < eElement.getElementsByTagName("quantity").getLength(); temp2++) {
                        text = eElement.getElementsByTagName("quantity").item(temp2).getTextContent();
                        quantity.add(DieQuantity.valueOf(text.toUpperCase().trim()));
                    }

                    try {
                        turn = Turn.toTurn(eElement.getElementsByTagName("when").item(0).getTextContent());
                    } catch (NullPointerException e) {
                        turn = Turn.NONE;
                    }
                }
            }
        }catch (SAXException | ParserConfigurationException | IOException e1) {
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
            constraint=Color.NONE;
            numSelectedDice=0;
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

    public Place getPlaceFrom(){
        return from;
    }

    public Place getPlaceTo(){
        return to;
    }

    public Commands getAction(){return actions.get(0);}



    public List<IndexedCellContent> shadeIncreaseDecrease(Die die){
        Die tmpDie;
        List <Die> modifiedDie=new ArrayList<>();

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.increaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException e) {
            e.printStackTrace();
        }

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.decreaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException e) {
            e.printStackTrace();
        }

        return toIndexedDieList(modifiedDie);
    }

    public boolean swapDie() {
        //if(!actions.contains(Commands.SWAP) || !canModify2()){return false;}
        if(selectedDice.size()==1){return true;}// the swap will take effect on the next iteration
        else if(selectedDice.size()==2){
            Color tmpColor = selectedDice.get(0).getColor();
            Face tmpFace = selectedDice.get(0).getShade();
            selectedDice.get(0).setShade(selectedDice.get(1).getShade().toInt());
            selectedDice.get(0).setColor(selectedDice.get(1).toString());
            selectedDice.get(1).setShade(tmpFace.toInt());
            selectedDice.get(1).setColor(tmpColor.toString());
            return true;
        }
        return false;
    }

    public List<IndexedCellContent> rerollDie(){
        List<Die> dieList= new ArrayList<>();
        Random randomGen = new Random();
        int face=randomGen.nextInt(7);
        selectedDice.get(0).setShade(face);
        dieList.add(selectedDice.get(0));
        return toIndexedDieList(dieList);
    }

    public void rerollAll(List<Die> rerollList){
        for(Die d:rerollList){
            d.reroll();
        }
    }

    public List<IndexedCellContent> flipDie(){
        List<Die> dieList= new ArrayList<>();
        selectedDice.get(0).flipShade();
        dieList.add(selectedDice.get(0));
        return toIndexedDieList(dieList);
    }

    public List<IndexedCellContent> chooseShade(){
        List <Die> modifiedDie=new ArrayList<>();
        for(int i=1;i<=6;i++){
            modifiedDie.add(new Die(i,selectedDice.get(0).getColor().toString()));
        }
        return toIndexedDieList(modifiedDie);
    }

    public void setColor(){
        constraint=selectedDice.get(0).getColor();
    }

    public Color getColorConstraint(){
        return constraint;
    }

    public boolean placeDie(int index){
        List<Integer> placerments= schemaTemp.listPossiblePlacements(selectedDice.get(0),ignored_constraint);

        try {
            schemaTemp.putDie(placerments.get(index),selectedDice.get(0),ignored_constraint);
        } catch (IllegalDieException e) {
            return false;
        }
        return true;
    }

    public void selectDie(Die die){
        selectedDice.add(die);
        return;
    }

    private List<IndexedCellContent> toIndexedDieList(List<Die> dieList){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        for (int index=0;index<dieList.size();index++){
            die=dieList.get(index);
            indexedCell=new IndexedCellContent(index,from,die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }

    public boolean toolCanContinue(Player player){
        if(actions.get(0)!=Commands.SWAP){
            selectedDice.remove(0);
        }
        actions.remove(0);
        if(actions.isEmpty()){
            player.replaceSchema(schemaTemp);
            return false;
        }
        return true;
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


    public SchemaCard getNewSchema(){
        return schemaTemp;
    }

    public List<Die> getToolDice(){
        return selectedDice;
    }

    public List<Integer> getDiceIndexes(){
        return selectedIndex;
    }


    //for other to schema
    public boolean externalVirtualPlacement(int index){

        return false;
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

    public List<Commands> getActions(){
        return actions;
    }

    public IgnoredConstraint getIgnoredConstraint(){
        return ignored_constraint;
    }

    public Turn getTurn(){
        return turn;
    }

}
