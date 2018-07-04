package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.serializables.IndexedCellContent;
import it.polimi.ingsw.server.SerializableServerUtil;
import it.polimi.ingsw.server.controller.MasterServer;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Cards named "Tools" and their calculating algorithms
 */
public class ToolCard extends Card {
    private static final String XML_DESCRIPTION = MasterServer.XML_SOURCE + "ToolCard.xml";
    private static final String XML_LOGIC = MasterServer.XML_SOURCE + "ToolLogic.xml";
    public static final int NUM_TOOL_CARDS=12;

    private boolean used;
    private Place from;
    private Place to;
    private List<Actions> actions;
    private IgnoredConstraint ignoredConstraint;
    private List<DieQuantity> quantity;
    private Turn turn;

    private DieColor constraint; //only for 12
    private int numDiePlaced;
    private int actionIndex;
    private SchemaCard schemaTemp;
    private List<Die> selectedDice;
    private List<Integer> oldIndexList;



    public ToolCard(int id) {// TODO: 25/06/2018 remove parser from constructor
        super();
        super.xmlReader(id, XML_DESCRIPTION, "ToolCard");
        this.used = false;
        actions = new ArrayList<>();
        quantity = new ArrayList<>();
        toolReader(super.getId());
    }

    protected void toolReader(int id) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile= classLoader.getResourceAsStream(XML_LOGIC);
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
                        actions.add(Actions.valueOf(text.toUpperCase().trim()));
                    }

                    if(to.equals(Place.SCHEMA)){
                        ignoredConstraint = IgnoredConstraint.toIgnoredConstraint(eElement.getElementsByTagName("ignored-constraint").item(0).getTextContent());
                    }else{
                        ignoredConstraint = IgnoredConstraint.NONE;
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
     * Checks whether the player can or can not use the tool card, based on the cost in favor tokens and other (turn,
     * dice already placed,...) constraints. It also clones the player's SchemaCard in a temporary to be used for
     * internal placements (subsequently it will replace the original if the execution of the card is executed completely
     * and correctly)
     * @param player the player who wants to use the toolcard
     * @param roundNumber the number of the match's round
     * @param turnFirstOrSecond if the turn is the first or the second in the round
     * @param numDiePlaced the number of dice already placed by the player in the turn
     * @param schema the player's SchemaCard
     * @return true if the ToolCard has been activated successfully
     */
    public boolean enableToolCard(Player player,int roundNumber,Turn turnFirstOrSecond, int numDiePlaced , SchemaCard schema) {
        try {
            if((actions.contains(Actions.SWAP) || actions.contains(Actions.SET_COLOR)) && roundNumber==0){return false;}
            if (!turn.equals(Turn.NONE) && !turn.equals(turnFirstOrSecond)) {return false;}
            if(turn.equals(Turn.SECOND_TURN) && numDiePlaced>=1){return false;}
            if(turn.equals(Turn.FIRST_TURN) && numDiePlaced!=1){return false;}
            if (checkPlacementConditions(numDiePlaced, schema)) {return false;}

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
            constraint=DieColor.NONE;
            actionIndex=0;
            schemaTemp=schema.cloneSchema();
            numDiePlaced=0;
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

    /**
     * Check the placement conditions according to whether the card performs a placement action  SchemaCard->SchemaCard
     * or DraftPool->SchemaCard
     * @param numDiePlaced the number of dice already placed by the player in the turn
     * @param schema the player's SchemaCard
     * @return true if the card can be activated
     */
    private boolean checkPlacementConditions(int numDiePlaced, SchemaCard schema) {
        if(isExternalPlacement() &&  numDiePlaced>=1 && !turn.equals(Turn.FIRST_TURN) && (to.equals(Place.SCHEMA) || to.equals(Place.DICEBAG))) {
            return true;
        }
        if(isInternalSchemaPlacement()){
            FullCellIterator diceIterator=(FullCellIterator)schema.iterator();
            if(diceIterator.numOfDice()<1 && quantity.contains(DieQuantity.ONE)){
                return true;
            }else {
                return diceIterator.numOfDice() < 2 && quantity.contains(DieQuantity.TWO) && !quantity.contains(DieQuantity.ONE);
            }
        }
        return false;
    }

    /**
     * Performs the increase / decrease action of the die's face
     * @param die the die the player wants to change
     * @return the indexed list of possible die-changing options
     */
    public List<IndexedCellContent> shadeIncreaseDecrease(Die die){
        Die tmpDie;
        List <Die> modifiedDie=new ArrayList<>();

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.increaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException ignored){ }

        try {
            tmpDie=new Die(die.getShade(),die.getColor());
            tmpDie.decreaseShade();
            modifiedDie.add(tmpDie);
        } catch (IllegalShadeException ignored) { }

        return toIndexedDieList(modifiedDie);
    }

    /**
     * Performs the swapping action between two dice, if the selected die is only one, it is only stored
     * @return true if the swapping action was successful
     */
    public boolean swapDie() {
        if(selectedDice.size()==1){return true;}// the swap will take effect on the next iteration
        else if(selectedDice.size()==2){
            selectedDice.get(0).swap(selectedDice.get(1));
            return true;
        }
        return false;
    }

    /**
     * Performs a random change of the selected die face (ONE to SIX)
     * @return the indexed list containing the modified die
     */
    public List<IndexedCellContent> rerollDie(){
        List<Die> dieList= new ArrayList<>();
        selectedDice.get(0).reroll();
        dieList.add(selectedDice.get(0));
        return toIndexedDieList(dieList);
    }

    /**
     * Performs a random change of the selected dice List faces (ONE to SIX)
     * @param rerollList the list of dice to reroll
     */
    public void rerollAll(List<Die> rerollList){
        for(Die d:rerollList){
            d.reroll();
        }
    }

    /**
     * Inverts the face of the previously selected die
     * @return the indexed list containing the modified die
     */
    public List<IndexedCellContent> flipDie(){
        List<Die> dieList= new ArrayList<>();
        selectedDice.get(0).flipShade();
        dieList.add(selectedDice.get(0));
        return toIndexedDieList(dieList);
    }

    /**
     * Returns the possible faces that the die (which the player wants to change) can take
     * @return the indexed list of possible face-changing options
     */
    public List<IndexedCellContent> chooseShade(){
        List <Die> modifiedDie=new ArrayList<>();
        for(int i=1;i<=6;i++){
            modifiedDie.add(new Die(i,selectedDice.get(0).getColor().toString()));
        }
        return toIndexedDieList(modifiedDie);
    }

    /**
     * Sets a color constraint for future ToolCard's placement actions
     */
    public void setColor(){
        constraint=selectedDice.get(0).getColor();
    }

    /**
     * Returns an indexed List of dice contained in the temporary schemacard
     * @return the indexed List of dice
     */
    public List<IndexedCellContent> internalIndexedSchemaDiceList(){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        FullCellIterator diceIterator=(FullCellIterator)schemaTemp.iterator();

        while(diceIterator.hasNext()) {
            die = diceIterator.next().getDie();
            if(!constraint.equals(DieColor.NONE)){
                if(die.getColor().equals(constraint)){
                    indexedCell = SerializableServerUtil.toIndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                    indexedList.add(indexedCell);
                }
            }else{
                indexedCell = SerializableServerUtil.toIndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                indexedList.add(indexedCell);
            }
        }
        return indexedList;
    }

    /**
     * Select a die that will be used later for the ToolCard-specific actions
     * @param die the die the player wants to manipulate
     */
    public void selectDie(Die die){
        selectedDice.add(die);
        return;
    }

    /**
     * Selects the die from the internal indexed dice List of the temporary SchemaCard (to be used only for SchemaCard->
     * SchemaCard placements)
     * @param listIndex the index of the die the user wants to select
     * @return the die selected by the player
     */
    public Die internalSelectDie(int listIndex){
        selectedDice.add(0,schemaTemp.getSchemaDiceList(constraint).get(listIndex));
        oldIndexList.add(0,schemaTemp.getDiePosition(selectedDice.get(0)));
        return selectedDice.get(0);
    }

    /**
     * Calculates and returns a list of integers that are the indexes (from 0 to 19) where the die could be placed. The
     * die is assumed that was previously selected (to be used only for SchemaCard->SchemaCard placements)
     * @return the list of possible placements for the selected die
     */
    public List<Integer> internalListPlacements() {
        schemaTemp.removeDie(oldIndexList.get(0));
        List<Integer> placements=schemaTemp.listPossiblePlacements(selectedDice.get(0),ignoredConstraint);

        try {
            schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
        } catch (IllegalDieException e) {
            System.out.println("Something went wrong....");
        }
        return placements;
    }

    /**
     * Places the die in the user-selected cell on the temporary SchemaCard (to be used only for SchemaCard->SchemaCard
     * placements)
     * @param index the index of the List of possible placements
     * @return true if the placement operation was successfully
     */
    public boolean internalDiePlacement(int index){
        List<Integer> placerments= internalListPlacements();
        try {
            //System.out.println("Internal_placement: "+selectedDice.get(0).toString()+" "+ignoredConstraint+" "+oldIndexList.get(0)); //TODO delete
            schemaTemp.removeDie(oldIndexList.get(0));
            schemaTemp.putDie(placerments.get(index),selectedDice.get(0),ignoredConstraint);
            numDiePlaced++;
        } catch (IllegalDieException e) {
            try {
                schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
            } catch (IllegalDieException e1) {
                System.out.println("Something went wrong....");
            }
        }
        return true;
    }

    /**
     * Constructs an indexed list of dice starting from a simple list of dice
     * @param dieList the originl list of dice
     * @return the new immutable indexed List of dice
     */
    private List<IndexedCellContent> toIndexedDieList(List<Die> dieList){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        for (int index=0;index<dieList.size();index++){
            die=dieList.get(index);
            indexedCell=SerializableServerUtil.toIndexedCellContent(index,from,die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }

    /**
     * Checks if the execution flow of the ToolCard is finished or must continue. If the execution flow is completed
     * correctly, it replaces the SchemaCard of the player with the temporary one ( only if internal placings have been
     * performed)
     * @param player the player is currently using the ToolCard
     * @return if the execution flow is not finished yet, false elsewhere
     */
    public boolean toolCanContinue(Player player){
        //System.out.println(selectedDice+"  "+oldIndexList); //TODO delete
        if(actions.get(actionIndex)!=Actions.SWAP && actions.get(actionIndex)!=Actions.INCREASE_DECREASE && !selectedDice.isEmpty()){//DA RIVEDERE, SI MANGIA I DADI
            selectedDice.remove(0);
        }
        if(isInternalSchemaPlacement() && !oldIndexList.isEmpty()){
            oldIndexList.remove(0);
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

    /**
     * Allows to cancel the selection of a die, and then removes the temporarily stored die
     */
    public void toolDiscard(){
        if(actions.get(actionIndex)!=Actions.SWAP && actions.get(actionIndex)!=Actions.INCREASE_DECREASE && !selectedDice.isEmpty()){//DA RIVEDERE, SI MANGIA I DADI
            selectedDice.remove(0);
        }
        if(isInternalSchemaPlacement() && !oldIndexList.isEmpty()){
            oldIndexList.remove(0);
        }
    }

    /**
     * Allows to exit the ToolCard execution procedure, interrupting the regular execution flow. If there are internal
     * placements completed correctly, it replaces the SchemaCard of the player with the temporary one.
     * @param player the player is currently using the ToolCard
     */
    public void toolExit(Player player){
        if(isInternalSchemaPlacement()) {
            if (quantity.contains(DieQuantity.TWO) && numDiePlaced== 2) {
                player.replaceSchema(schemaTemp);
                return;
            }
            if (quantity.contains(DieQuantity.ONE) && numDiePlaced== 1) {
                player.replaceSchema(schemaTemp);
            }
        }
    }

    /**
     * Returns the index of the old dices to delete from a certain position
     * @return a list of indexes
     */
    public List<Integer> getOldIndexes(){
        return oldIndexList;
    }

    /**
     * Returns true if the placements of the ToolCard are not internal (from!=to)
     * @return if the placements are not internal
     */
    public boolean isExternalPlacement(){
        return !from.equals(to);
    }

    /**
     * Returns true if the placements of the ToolCard are internal (SchemaCard->SchemaCard)
     * @return if the placements are internal
     */
    public boolean isInternalSchemaPlacement() {
        return from.equals(Place.SCHEMA) && to.equals(Place.SCHEMA);
    }

    /**
     * Returns true if the ToolCard expects that all dice will be rolled
     * @return if the ToolCard expects that all dice will be rolled
     */
    public boolean isRerollAllDiceCard(){
        return actions.get(actionIndex).equals(Actions.REROLL) && quantity.contains(DieQuantity.ALL);
    }

    /**
     * Returns true if the card expects a color constraint to be selected
     * @return if the card expects a color constraint to be selected
     */
    public boolean isSetColorFromRountrackCard(){
        return actions.get(actionIndex).equals(Actions.SET_COLOR);
    }

    /**
     * Returns the position from which the dice selected by the player come from, when the ToolCard is enabled
     * @return the from position
     */
    public Place getPlaceFrom(){
        return from;
    }

    /**
     * Returns the position from which the dice must be placed, when the ToolCard is enabled
     * @return the destination position
     */
    public Place getPlaceTo(){
        return to;
    }

    /**
     * Returns the color constraint selected by the player during the ToolCard execution, if expected
     * @return the DieColor constraint selected
     */
    public DieColor getColorConstraint(){
        return constraint;
    }

    /**
     * Returns the temporary SchemaCard used for performing the internal placements (SchemaCard->SchemaCard), when the
     * ToolCard is enabled
     * @return the internal temporary SchemaCard
     */
    public SchemaCard getNewSchema(){
        return schemaTemp;
    }

    /**
     * This method provide the information about if the card has already been used
     * @return true if the ToolCard has already been used
     */
    public boolean isAlreadyUsed(){
        return this.used;
    }

    /**
     * Returns the List containing the possible actions that the player needs to perform for the correct execution
     * flow of the ToolCard selected
     * @return the List containing the possible actions to perform
     */
    public List<Actions> getActions(){
        List<Actions> commands=new ArrayList<>();
        commands.add(actions.get(actionIndex));
        return commands;
    }

    /**
     * Returns the constraint to ignore (if there is) during the ToolCard usage
     * @return the constraint to ignore
     */
    public IgnoredConstraint getIgnoredConstraint(){
        return ignoredConstraint;
    }
}
