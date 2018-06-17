package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class represents a schema card of the game
 */
public class SchemaCard implements Iterable<Cell>  {
    private String name;
    private int id;
    private int favorTokens;
    private final Cell [][] cell;
    private Boolean isFirstDie;
    public static final int NUM_COLS=5;
    public static final int NUM_ROWS=4;
    public static final int NUM_SCHEMA=24;
    private static String xmlSource=MasterServer.XML_SOURCE+"SchemaCard.xml";

    /**
     * Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id ToolCard id
     */
    public SchemaCard(Cell [] [] cells,int id, String name,int favorTokens){
        cell = cells;
        this.id=id;
        this.name=name;
        this.favorTokens=favorTokens;
        isFirstDie=true;

    }

    /**
     * The SchemaCard's class constructor. Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id the ID of the schema card to instantiate
     */
    public SchemaCard(int id){
        SchemaCard temp= parser(id);
        assert temp != null;
        cell = temp.cell;
        this.id=id;
        this.name=temp.name;
        this.favorTokens=temp.favorTokens;
        isFirstDie=true;
    }

    /**
     * Retrieve the SchemaCard's data from the xml file ant initilizes the related parameters
     * @param id the ScheCard's id
     * @return
     */
    public static SchemaCard parser(int id){
        File xmlFile= new File(xmlSource);
        String name="";
        int favorTokens=0;
        Cell[][] cells= new Cell[NUM_ROWS][NUM_COLS];

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("SchemaCard");
            for (int temp1 = 0; temp1 < nodeList.getLength() && (temp1-1)!=id; temp1++) {

                Element eElement = (Element)nodeList.item(temp1);

                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    name=eElement.getElementsByTagName("name").item(0).getTextContent();

                    favorTokens = Integer.parseInt(eElement.getElementsByTagName("token").item(0).getTextContent());


                    for (int temp2 = 0; temp2 < eElement.getElementsByTagName("data").getLength(); temp2++) {
                        int row = Integer.parseInt(eElement.getElementsByTagName("row").item(temp2).getTextContent());
                        int column = Integer.parseInt(eElement.getElementsByTagName("col").item(temp2).getTextContent());
                        cells[row][column]=new Cell(eElement.getElementsByTagName("data").item(temp2).getTextContent());
                    }
                }
            }

            for (int i=0; i<NUM_ROWS ; i++){
                for (int j=0; j<NUM_COLS ; j++){
                    if (cells[i][j] == null){
                        cells[i][j] = new Cell();
                    }
                }
            }

            return new SchemaCard(cells,id,name,favorTokens);
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            System.err.println("ERR: couldn't load schema card");
        }
        return null;
    }

    /**
     * Returns the SchemaCard id
     * @return id
     */
    public int getId(){
        return this.id;
    }

    /**
     * Returns the name of the SchemaCard
     * @return name
     */
    public String getName(){ return this.name; }


    /**
     * Returns the SchemaCard favorTokens
     * @return number of favorTokens
     */
    public int getFavorTokens(){
        return this.favorTokens;
    }

    /**
     * Returns the cell(row,column)
     * @param row the cell's row
     * @param column the cell's column
     * @return the cell's reference
     */
    public Cell getCell(int row, int column){
        return cell[row][column];
    }

    /**
     * Returns the schema's cell at a given index
     * @param index the cell's index (0 to 19)
     * @return the cell's reference
     */
    public Cell getCell(int index){
        return cell[index/SchemaCard.NUM_COLS][index%SchemaCard.NUM_COLS];
    }

    /**
     * Calculates and returns a list of integers that are the indexes (from 0 to 19) where the die could be placed
     * @param die the die we want to place
     * @return the list of valid positions for the die
     */
    public List<Integer> listPossiblePlacements(Die die) {
        return listPossiblePlacements(die,IgnoredConstraint.NONE);
    }

    /**
     * Calculates and returns a list of integers that are the indexes (from 0 to 19) where the die could be placed
     * @param die the die we want to place
     * @param ignoreConstraint the constraint to be ignored
     * @return the list of valid positions for the die
     */
    public List<Integer> listPossiblePlacements(Die die, IgnoredConstraint ignoreConstraint) {
        List <Integer> list= new ArrayList<>();
        FullCellIterator diceIterator;
        diceIterator= (FullCellIterator) this.iterator();
        if(ignoreConstraint.equals(IgnoredConstraint.ADJACENCY)){
            for(int i=0; i < SchemaCard.NUM_COLS * SchemaCard.NUM_ROWS; i++ ){
                if(canBePlacedHere(i/SchemaCard.NUM_COLS, i%SchemaCard.NUM_COLS , die)){
                    list.add(i);
                }
            }
        }else{
            if(isFirstDie){
                //first and last rows
                checkBorder(die, list,ignoreConstraint);
            }else{
                while(diceIterator.hasNext()){
                    diceIterator.next();

                    checkCloseCells(die, diceIterator, list, ignoreConstraint);
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    public List<Integer> listPossiblePlacementsSwap(Die die, Color fixedColor){
        if(!fixedColor.equals(Color.NONE) && !die.getColor().equals(fixedColor)){
            throw new IllegalArgumentException();
        }
        List <Integer> list= new ArrayList<>();
        FullCellIterator diceIterator;
        diceIterator= (FullCellIterator) this.iterator();
        while(diceIterator.hasNext()){
            diceIterator.next();
            checkCloseCells(die, diceIterator, list, IgnoredConstraint.NONE);
        }

        diceIterator= (FullCellIterator)this.iterator();

        while(diceIterator.hasNext()){
            Cell celll=diceIterator.next();

            if(celll.getDie().getColor().equals(fixedColor) || fixedColor.equals(Color.NONE)){
                SchemaCard tempschema=cloneSchema();
                tempschema.removeDie(diceIterator.getIndex());

                if(tempschema.listPossiblePlacements(die).contains(diceIterator.getIndex())) {

                    try {
                        tempschema.putDie(diceIterator.getIndex(), die);
                    } catch (IllegalDieException e) {

                    }

                    if (!tempschema.listPossiblePlacements(celll.getDie()).isEmpty()) {
                        list.add(diceIterator.getIndex());
                    }
                }

                checkCloseCells(die,diceIterator,list,IgnoredConstraint.NONE);

            }
        }

        Collections.sort(list);
        return list;
    }

    /**
     * Checks if cells close to the one given by the iterator(that contains a die) could accept the die
     * @param die the die to be possibly put in place
     * @param diceIterator iterates on full cells
     * @param list the list of possible positions for the die
     * @param ignoreConstraint string that tells which constraint(s) can be ignored
     */
    private void checkCloseCells(Die die, FullCellIterator diceIterator, List<Integer> list, IgnoredConstraint ignoreConstraint) {
        int row;
        int column;
        Integer index;
        for(int i = -1; i<2; i++){
            row=diceIterator.getRow()+i;
            for(int j=-1; j<2; j++){
                column=diceIterator.getColumn()+j;
                if( (row>=0 && row<NUM_ROWS) && (column>=0 && column<NUM_COLS)){
                    index=(row * NUM_COLS + column);

                    if (!list.contains(index) && canBePlacedHere(row, column, die,ignoreConstraint)) {
                        list.add(index);
                    }

                }
            }
        }
    }



    /**
     * Calculates a list of indexes where the die can be put, if it's the first being placed (only on the border of the schema card)
     * @param die die to be put in place
     * @param list list of possible placements
     * @param ignoreConstraint string that tells which constraint(s) can be ignored
     */
    private void checkBorder(Die die, List<Integer> list,IgnoredConstraint ignoreConstraint) {
        int row;
        int column;
        //first and last rows
        for(row=0,column=0; column < NUM_COLS; column++){
            if(this.cell[row][column].canAcceptDie(die,ignoreConstraint)){
                list.add(row * NUM_COLS + column);
            }
            if(this.cell[row+NUM_ROWS-1][column].canAcceptDie(die,ignoreConstraint)){
                list.add((row+NUM_ROWS-1) * NUM_COLS + column);
            }
        }

        //first and last columns
        for(row=1,column=0; row<NUM_ROWS-1;row++){
            if(this.cell[row][column].canAcceptDie(die,ignoreConstraint)){
                list.add(row * NUM_COLS );
            }
            if(this.cell[row][column+NUM_COLS-1].canAcceptDie(die,ignoreConstraint)){
                list.add(row * NUM_COLS + NUM_COLS-1);
            }
        }
    }
    /**
     * Checks whether or not a die can be placed in a cell that is known to be adiacent to a die that is already placed
     * @param row  the row of the cell to be checked
     * @param column the column of the cell to be checked
     * @param die the die we want to place
     * @return true iff the die can be placed there
     */
    @NotNull
    private Boolean canBePlacedHere (int row, int column, Die die){
        return canBePlacedHere(row,column,die,IgnoredConstraint.NONE);
    }

    /**
     * Checks whether or not a die can be placed in a cell that is known to be adiacent to a die that is already placed
     * @param row  the row of the cell to be checked
     * @param column the column of the cell to be checked
     * @param die the die we want to place
     * @param ignoreConstraint string that tells which constraint(s) can be ignored
     * @return true iff the die can be placed there
     */
    @NotNull
    private Boolean canBePlacedHere (int row, int column, Die die,IgnoredConstraint ignoreConstraint){
        if(this.cell[row][column].hasDie()){ return false; }

        if(!this.cell[row][column].canAcceptDie(die,ignoreConstraint)){ return false; }

        if((row > 0)                    && this.cell[row - 1][column].hasDie() && this.cell[row - 1][column].checkNeighbor(die)) { return false; }

        if((row < (NUM_ROWS - 1))       && this.cell[row + 1][column].hasDie() && this.cell[row + 1][column].checkNeighbor(die)) { return false; }

        if((column > 0)                 && this.cell[row][column - 1].hasDie() && this.cell[row][column - 1].checkNeighbor(die)) { return false; }

        return !((column < (NUM_COLS - 1)) && this.cell[row][column + 1].hasDie() && this.cell[row][column + 1].checkNeighbor(die));
    }

    /**
     * Puts the die in place if possible, if not an exception is thrown
     * @param index index (0 to 19) where the die is going to be put
     * @param die die to be put
     * @throws IllegalDieException iff the die can't be placed there
     */
    public void putDie (int index,Die die) throws IllegalDieException{
        putDie(index,die,IgnoredConstraint.NONE);
    }

    /**
     * Puts the die in place if possible, if not an exception is thrown
     * @param index index (0 to 19) where the die is going to be put
     * @param die die to be put
     * @param ignoreConstraint string that tells which constraint(s) can be ignored
     * @throws IllegalDieException iff the die can't be placed there
     */
    public void putDie (int index,Die die, IgnoredConstraint ignoreConstraint) throws IllegalDieException {
        if(!ignoreConstraint.equals(IgnoredConstraint.FORCE)) {

            if (!this.listPossiblePlacements(die, ignoreConstraint).contains(index))
                throw new IllegalDieException("You can't place this die here!");
        }
        this.cell[index/NUM_COLS][index%NUM_COLS].setDie(die,ignoreConstraint);
        this.isFirstDie=false;
    }

    /**
     * Removes the die in the cell in the position indicated by index and returns it to the caller
     * @param index the index of the cell containing the die to be removed
     */
    public void removeDie(int index) {
        assert index>=0 && index<NUM_ROWS*NUM_COLS;
        if (this.getCell(index).hasDie()) {
            this.getCell(index).removeDie();
            if(((FullCellIterator)this.iterator()).numOfDice()==0){
                isFirstDie=true;
            }
            return;
        }
        throw new NoSuchElementException("there's no die to remove in this cell(index:"+index+")");
    }

    /**
     * Instantiates an iterator for cells containing a die
     * @return iterator on cells that contain a die
     */
    @NotNull
    @Override
    public Iterator<Cell> iterator() {
        return new FullCellIterator(this.cell);
    }

    /**
     * This method has not been implemented
     * @param action the action to be applied to each element
     */
    @Override
    public void forEach(Consumer<?super Cell> action){
        throw new UnsupportedOperationException();
    }

    /**
     * Cretes a copy of the SchemaCard instantiated
     * @return the copy reference
     */
    public SchemaCard cloneSchema(){
        SchemaCard temp= new SchemaCard(this.id);
        FullCellIterator iter= (FullCellIterator) iterator();
        Cell tempCell;
        while(iter.hasNext()){
            tempCell=iter.next();
            try {
                temp.putDie(iter.getIndex(),new Die(tempCell.getDie().getShade(),tempCell.getDie().getColor()),IgnoredConstraint.FORCE);
            } catch (IllegalDieException e) {
                return null;
            }

        }
        return temp;
    }

    /**
     * Returns a list of the dice placed in the schema card.
     * @param constraint the color restriction
     * @return a list of die
     */
    public List<Die> getSchemaDiceList(Color constraint){
        List<Die> dieList=new ArrayList<>();
        Die die;
        FullCellIterator diceIterator=(FullCellIterator)this.iterator();
        while(diceIterator.hasNext()) {
            die=diceIterator.next().getDie();
            if(!constraint.equals(Color.NONE)){
                if(die.getColor().equals(constraint)){
                    dieList.add(die);
                }
            }else{
                dieList.add(die);
            }

        }
        return dieList;
    }

    public int getDiePosition(Die die){
        assert getSchemaDiceList(Color.NONE).contains(die);
        FullCellIterator diceIterator=(FullCellIterator)this.iterator();
        while(diceIterator.hasNext()) {
            Die dieTemp=diceIterator.next().getDie();
            if(die.equals(dieTemp)){
                    return diceIterator.getIndex();

            }
        }
        return -1;
    }

}