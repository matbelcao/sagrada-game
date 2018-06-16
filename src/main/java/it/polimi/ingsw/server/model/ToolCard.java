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
    private List<Commands> actions;
    private IgnoredConstraint ignoredConstraint;
    private List<DieQuantity> quantity;
    private Turn turn;

    private Color constraint; //only for 12
    private int numDiePlaced;
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
            if((actions.contains(Commands.SWAP) || actions.contains(Commands.SET_COLOR)) && roundNumber==0){return false;}
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
            constraint=Color.NONE;
            actionIndex=0;
            schemaTemp=schema.cloneSchema();
            numDiePlaced=0;
        } catch (NegativeTokensException e) {
            return false;
        }
        return true;
    }

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

    public boolean swapDie() {
        if(selectedDice.size()==1){return true;}// the swap will take effect on the next iteration
        else if(selectedDice.size()==2){
            selectedDice.get(0).swap(selectedDice.get(1));
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

    public void selectDie(Die die){
        selectedDice.add(die);
        return;
    }

    public Die internalSelectDie(int listIndex){
        selectedDice.add(0,schemaTemp.getSchemaDiceList(constraint).get(listIndex));
        oldIndexList.add(0,schemaTemp.getDiePosition(selectedDice.get(0)));
        return selectedDice.get(0);
    }


    public List<Integer> internalListPlacements() {
        schemaTemp.removeDie(oldIndexList.get(0));
        List<Integer> placements=schemaTemp.listPossiblePlacements(selectedDice.get(0),ignoredConstraint);

        try {
            schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
        } catch (IllegalDieException e) {
            System.out.println("Something gone wrong....");
        }
        return placements;
    }

    public boolean internalDiePlacement(int index){
        List<Integer> placerments= internalListPlacements();
        try {
            //System.out.println("Internal_placement: "+selectedDice.get(0).toString()+" "+ignoredConstraint+" "+oldIndexList.get(0));
            schemaTemp.removeDie(oldIndexList.get(0));
            schemaTemp.putDie(placerments.get(index),selectedDice.get(0),ignoredConstraint);
            numDiePlaced++;
        } catch (IllegalDieException e) {
            try {
                schemaTemp.putDie(oldIndexList.get(0),selectedDice.get(0),IgnoredConstraint.FORCE);
            } catch (IllegalDieException e1) {
                System.out.println("Something gone wrong....");
            }
        }
        return true;
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
        //System.out.println(selectedDice+"  "+oldIndexList);
        if(actions.get(actionIndex)!=Commands.SWAP && actions.get(actionIndex)!=Commands.INCREASE_DECREASE && !selectedDice.isEmpty()){//DA RIVEDERE, SI MANGIA I DADI
            selectedDice.remove(0);
        }
        if(isInternalSchemaPlacement() && !selectedDice.isEmpty()){
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

    public void toolDiscard(){
        if(actions.get(actionIndex)!=Commands.SWAP && actions.get(actionIndex)!=Commands.INCREASE_DECREASE && !selectedDice.isEmpty()){//DA RIVEDERE, SI MANGIA I DADI
            selectedDice.remove(0);
        }
        if(isInternalSchemaPlacement() && !selectedDice.isEmpty()){
            oldIndexList.remove(0);
        }
    }

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

    public List<Integer> getOldIndexes(){
        return oldIndexList;
    }


    public boolean isExternalPlacement(){
        return !from.equals(to);
    }

    public boolean isInternalSchemaPlacement() {
        return from.equals(Place.SCHEMA) && to.equals(Place.SCHEMA);
    }

    public boolean isRerollAllDiceCard(){
        return actions.get(actionIndex).equals(Commands.REROLL) && quantity.contains(DieQuantity.ALL);
    }

    public boolean isSetColorFromRountrackCard(){
        return actions.get(actionIndex).equals(Commands.SET_COLOR);
    }

    public Place getPlaceFrom(){
        return from;
    }

    public Place getPlaceTo(){
        return to;
    }

    public Color getColorConstraint(){
        return constraint;
    }

    public SchemaCard getNewSchema(){
        return schemaTemp;
    }

    /**
     * This method provide the information about if the card has been yet used
     * @return true iff has been used yet
     */
    public boolean isAlreadyUsed(){
        return this.used;
    }

    public List<Commands> getActions(){
        List<Commands> commands=new ArrayList<>();
        commands.add(actions.get(actionIndex));
        return commands;
    }

    public IgnoredConstraint getIgnoredConstraint(){
        return ignoredConstraint;
    }


}
