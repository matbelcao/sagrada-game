package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a schema card of the game
 */
public class SchemaCard implements Iterable<Cell>  {
    private static final String EXTERNAL_ADDITIONAL_SCHEMAS_XML =
            (new File(MasterServer.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath()
                    +File.separator+"AdditionalSchemas.xml";
    private static final String SCHEMA_CARD = "SchemaCard";
    private static final String NAMEE = "name";
    private static final String TOKEN = "token";
    private static final String DATA = "data";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String ERR_COULDN_T_LOAD_SCHEMA_CARD = "ERR: couldn't load schema card";
    private static final String ERR_OPENING_ADD_SCHEMAS_FILE = "ERR: couldn't open the additional schemas file correctly: ";
    private static final String YOU_CAN_T_PLACE_THIS_DIE_HERE = "You can't place this die here!";
    private static final String THERE_S_NO_DIE_TO_REMOVE_IN_THIS_CELL_INDEX = "there's no die to remove in this cell(index:";
    private String name;
    private int id;
    private int favorTokens;
    private final Cell [][] cell;
    private Boolean isFirstDie;
    private Boolean additionalSchema;
    public static final int NUM_COLS=5;
    public static final int NUM_ROWS=4;
    static final int NUM_SCHEMA=24;
    private static final String XML_SCHEMAS =MasterServer.XML_SOURCE+"SchemaCard.xml";
    private static final String XML_ADDITIONAL_SCHEMAS =MasterServer.XML_SOURCE+"AdditionalSchemas.xml";

    /**
     * Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id ToolCard id
     */
    public SchemaCard(Cell [] [] cells,int id, String name,int favorTokens,boolean additionalSchema){
        cell = cells;
        this.id=id;
        this.name=name;
        this.favorTokens=favorTokens;
        this.additionalSchema = additionalSchema;
        isFirstDie=true;

    }

    /**
     * The SchemaCard's class constructor. Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param additionalSchema true to enable a personalized SchemaCard instantiation
     * @param id the ID of the schema card to instantiate
     */
    public static SchemaCard getNewSchema(int id,boolean additionalSchema){
        return parser(id,additionalSchema);

    }

    /**
     * Retrieve the SchemaCard's data from the xml file ant initilizes the related parameters
     * @param id the ScheCard's id
     * @param additionalSchema true to enable a personalized SchemaCard instantiation
     * @return the bulit SchemaCard
     */
    private static SchemaCard parser(int id, boolean additionalSchema){

        SchemaCard schema;
        if(additionalSchema){
            try (InputStream xmlFile= new FileInputStream(EXTERNAL_ADDITIONAL_SCHEMAS_XML)){
                schema=readSchema(id,xmlFile,additionalSchema);
                if(schema!=null){
                    return schema;
                }
            } catch (IOException ignored) {
                //Not necessary
            }

            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            InputStream xmlFile=classLoader.getResourceAsStream(XML_ADDITIONAL_SCHEMAS);

            return readSchema(id,xmlFile,additionalSchema);

        }else{
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            InputStream xmlFile=classLoader.getResourceAsStream(XML_SCHEMAS);
            return readSchema(id, xmlFile,additionalSchema);
        }

    }


    /**
     * Builds the schema card readed from the xml file
     * @param id the id of the built schema card
     * @param xmlFile the xml file path
     * @return the built schemas
     */
    @Nullable
    private static SchemaCard readSchema(int id, InputStream xmlFile,boolean additionalSchema) {
        String name;
        int favorTokens;
        Cell[][] cells= new Cell[NUM_ROWS][NUM_COLS];
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(SCHEMA_CARD);
            Element eElement = (Element)nodeList.item(id-1);

            name=eElement.getElementsByTagName(NAMEE).item(0).getTextContent();

            favorTokens = Integer.parseInt(eElement.getElementsByTagName(TOKEN).item(0).getTextContent());

            for (int temp2 = 0; temp2 < eElement.getElementsByTagName(DATA).getLength(); temp2++) {
                int row = Integer.parseInt(eElement.getElementsByTagName(ROW).item(temp2).getTextContent());
                int column = Integer.parseInt(eElement.getElementsByTagName(COL).item(temp2).getTextContent());
                cells[row][column]=new Cell(eElement.getElementsByTagName(DATA).item(temp2).getTextContent());
            }

            for (int i=0; i<NUM_ROWS ; i++){
                for (int j=0; j<NUM_COLS ; j++){
                    if (cells[i][j] == null){
                        cells[i][j] = new Cell();
                    }
                }
            }

            return new SchemaCard(cells,id,name,favorTokens,additionalSchema);
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            Logger.getGlobal().log(Level.INFO, ERR_COULDN_T_LOAD_SCHEMA_CARD);
        }
        return null;
    }

    /**
     * Returns the number of custom SchemasCards present in the XML file
     * @return the number of SchemaCards
     */
    static int getAdditionalSchemaSize(){

        try (InputStream xmlFile= new FileInputStream(EXTERNAL_ADDITIONAL_SCHEMAS_XML)){
            return countAdditionalSchemas(xmlFile);
        } catch (IOException ignored){
            //Not necessary
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile= classLoader.getResourceAsStream(XML_ADDITIONAL_SCHEMAS);

        return countAdditionalSchemas(xmlFile);
    }

    /**
     * Returns the numbero of additonal schemas on the xmlFile
     * @param xmlFile the xml file path
     * @return the numbero of schemas
     */
    private static int countAdditionalSchemas(InputStream xmlFile) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {

            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(SCHEMA_CARD);
            return nodeList.getLength();

        }catch (SAXException | ParserConfigurationException | IOException e1) {
            Logger.getGlobal().log(Level.INFO,e1.getMessage());
            Logger.getGlobal().log(Level.INFO, ERR_OPENING_ADD_SCHEMAS_FILE);
            return 0;
        }
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
        if(!ignoreConstraint.equals(IgnoredConstraint.FORCE) && !this.listPossiblePlacements(die, ignoreConstraint).contains(index)){
                throw new IllegalDieException(YOU_CAN_T_PLACE_THIS_DIE_HERE);
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
        throw new NoSuchElementException(THERE_S_NO_DIE_TO_REMOVE_IN_THIS_CELL_INDEX +index+")");
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
    SchemaCard cloneSchema(){
        SchemaCard temp= getNewSchema(this.id,additionalSchema);
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
    public List<Die> getSchemaDiceList(DieColor constraint){
        List<Die> dieList=new ArrayList<>();
        Die die;
        FullCellIterator diceIterator=(FullCellIterator)this.iterator();
        while(diceIterator.hasNext()) {
            die=diceIterator.next().getDie();
            if(!constraint.equals(DieColor.NONE)){
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
        assert getSchemaDiceList(DieColor.NONE).contains(die);
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