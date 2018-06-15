package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
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
 * This class implements the Cards named "Tools" and their calculating algorithms
 */
public class ToolCard extends Card {
    private static final String XML_DESCRIPTION = MasterServer.XML_SOURCE + "ToolCard.xml";
    private static final String XML_LOGIC = MasterServer.XML_SOURCE + "ToolLogic.xml";

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
    private int actionIndex;
    private SchemaCard schemaTemp;
    private List<Die> selectedDice;
    private List<Integer> oldIndexList;


    public ToolCard(int id) {
        super();
        super.xmlReader(id, XML_DESCRIPTION, "ToolCard");
        this.used = false;
        actions = new ArrayList<>();
        quantity = new ArrayList<>();
        toolReader(super.getId());
    }

    protected void toolReader(int id) {
        File xmlFile = new File(XML_LOGIC);
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

    public boolean enableToolCard(Player player,int roundNumber,Turn turnFirstOrSecond, int numDiePlaced , SchemaCard schema) {
        try {
            if(actions.contains(Commands.SWAP) || actions.contains(Commands.SET_COLOR) && roundNumber==0){return false;}
            if (!turn.equals(Turn.NONE) && !turn.equals(turnFirstOrSecond)) {return false;}
            if(turn.equals(Turn.SECOND_TURN) && numDiePlaced>=1){return false;}
            if(turn.equals(Turn.FIRST_TURN) && numDiePlaced!=1){return false;}

            if(isExternalPlacement() &&  numDiePlaced>=1 && !turn.equals(Turn.FIRST_TURN)) {
                if (to.equals(Place.SCHEMA) || to.equals(Place.DICEBAG)) {
                    return false;
                }
            }
            if(isInternalSchemaPlacement()){
                FullCellIterator diceIterator=(FullCellIterator)schema.iterator();
                if(diceIterator.numOfDice()<1 && quantity.contains(DieQuantity.ONE)){
                    return false;
                }else if(diceIterator.numOfDice()<2 && quantity.contains(DieQuantity.TWO)) {
                    return false;
                }
            }

            if (!used) {
                player.decreaseFavorTokens(1);
                used = true;
            } else {
                player.decreaseFavorTokens(2);
            }

            if(turn.equals(Turn.FIRST_TURN)){
                player.setSkipsNextTurn(true);
            }

            selectedDice=new ArrayList<>();
            oldIndexList=new ArrayList<>();
            constraint=Color.NONE;
            actionIndex=0;
            schemaTemp=schema.cloneSchema();
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

    public List<IndexedCellContent> shadeIncreaseDecrease(Die die){
        Die tmpDie;
        List <Die> modifiedDie=new ArrayList<>();

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.increaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException ignored) { }

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.decreaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException ignored) { }

        return toIndexedDieList(modifiedDie);
    }

    public boolean swapDie() {
        System.out.println("swap: "+selectedDice.size());
        if(selectedDice.size()==1){return true;}// the swap will take effect on the next iteration
        else if(selectedDice.size()==2){
            System.out.println(selectedDice.get(0).toString()+" "+selectedDice.get(1).toString());
            selectedDice.get(0).swap(selectedDice.get(1));
            System.out.println(selectedDice.get(0).toString()+" "+selectedDice.get(1).toString());
            return true;
        }
        return false;
    }

    public List<IndexedCellContent> rerollDie(){
        List<Die> dieList= new ArrayList<>();
        selectedDice.get(0).reroll();
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

    public List<IndexedCellContent> chooseShade(){// TODO: 13/06/2018 modify method 
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

    public List<IndexedCellContent> internalIndexedSchemaDiceList(){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        FullCellIterator diceIterator=(FullCellIterator)schemaTemp.iterator();

        while(diceIterator.hasNext()) {
            die = diceIterator.next().getDie();
            if(!constraint.equals(Color.NONE)){
                if(die.getColor().equals(constraint)){
                    indexedCell = new IndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                    indexedList.add(indexedCell);
                }
            }else{
                indexedCell = new IndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                indexedList.add(indexedCell);
            }
        }
        return indexedList;
    }

    public Die internalSelectDie(int list_index){
        selectedDice.add(0,schemaTemp.getSchemaDiceList(constraint).get(list_index));
        oldIndexList.add(0,schemaTemp.getDiePosition(selectedDice.get(0)));
        return selectedDice.get(0);
    }


    public List<Integer> internalListPlacements() {
        System.out.println("Internal_placement_list: "+selectedDice.get(0).toString()+" "+ignored_constraint+" "+oldIndexList.get(0));
        System.out.println("Before: "+ schemaTemp.getSchemaDiceList(Color.NONE));

        schemaTemp.removeDie(oldIndexList.get(0));
        System.out.println("After: "+ schemaTemp.getSchemaDiceList(Color.NONE));

        List<Integer> placements=schemaTemp.listPossiblePlacements(selectedDice.get(0),ignored_constraint);
        System.out.println(schemaTemp.listPossiblePlacements(selectedDice.get(0),ignored_constraint));
        try {
            schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
        } catch (IllegalDieException e) {
            e.printStackTrace();
            System.out.println("Something gone wrong....");
        }
        return placements;
    }

    public boolean internalDiePlacement(int index){
        List<Integer> placerments= internalListPlacements();
        try {
            System.out.println("Internal_placement: "+selectedDice.get(0).toString()+" "+ignored_constraint+" "+oldIndexList.get(0));
            schemaTemp.removeDie(oldIndexList.get(0));
            schemaTemp.putDie(placerments.get(index),selectedDice.get(0),ignored_constraint);
        } catch (IllegalDieException e) {
            e.printStackTrace();
            try {
                schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
            } catch (IllegalDieException e1) {
                System.out.println("Something gone wrong....");
            }
        }
        return true;
    }

    public void selectDie(Die die,int oldIndex){// TODO: 15/06/2018
        selectedDice.add(die);
        /*if(isExternalPlacement()){
            oldIndexList.add(oldIndex);
        }*/
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

    public boolean isRerollAllDiceCard(){
        return actions.get(actionIndex).equals(Commands.REROLL) && quantity.contains(DieQuantity.ALL);
    }

    public boolean isSetColorFromRountrackCard(){
        return actions.get(actionIndex).equals(Commands.SET_COLOR);
    }

    public boolean toolCanContinue(Player player){
        System.out.println(selectedDice+"  "+oldIndexList);
        if(actions.get(actionIndex)!=Commands.SWAP && actions.get(actionIndex)!=Commands.INCREASE_DECREASE){//DA RIVEDERE, SI MANGIA I DADI
            if(selectedDice.size()>0){selectedDice.remove(0);}
        }
        if(isInternalSchemaPlacement()){// || actions.get(actionIndex).equals(Commands.SWAP)){
            if(oldIndexList.size()>0){oldIndexList.remove(0);}
        }

        actionIndex++;
        if(actions.size()==actionIndex){
            if(isInternalSchemaPlacement()){
                player.replaceSchema(schemaTemp);
            }
            return false;
        }
        return true;
    }

    public List<Integer> getOldIndexes(){
        return oldIndexList;
    }


    public boolean isExternalPlacement(){
        if(!from.equals(to)){
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

    public Place getPlaceFrom(){
        return from;
    }

    public Place getPlaceTo(){
        return to;
    }

    /**
     * This method provide the information about if the card has been yet used
     * @return true iff has been used yet
     */
    public boolean isAlreadyUsed(){
        return this.used;
    }

    public List<DieQuantity> getQuantity(){
        return quantity;
    }

    public List<Commands> getActions(){
        List<Commands> commands=new ArrayList<>();
        if(quantity.contains(DieQuantity.ONE) && quantity.contains(DieQuantity.TWO)){
            if(commands.get(actionIndex).equals(Commands.PLACE_DIE) && commands.get(actionIndex-1).equals(Commands.PLACE_DIE)){
                commands.add(Commands.NONE);
            }
        }
        commands.add(actions.get(actionIndex));
        return commands;
    }

    public IgnoredConstraint getIgnoredConstraint(){
        return ignored_constraint;
    }

    public Turn getTurn(){
        return turn;
    }

}
