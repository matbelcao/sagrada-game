package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.xml.sax.SAXException;

/**
 * This class represents a schema card of the game
 */
public class SchemaCard implements Iterable<Cell> {
    private String name;
    private int id;
    private int favorTokens;
    private final Cell [][] cell;
    private Boolean isFirstDie;
    public static final int NUM_COLS=5;
    public static final int NUM_ROWS=4;

    /**
     * Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id ToolCard id
     * @param xmlSrc xml file path
     */
    public SchemaCard(int id, String xmlSrc){
        super();

        int row;
        int column;
        cell = new Cell[NUM_ROWS][NUM_COLS];

        File xmlFile= new File(xmlSrc);
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
                    this.name=eElement.getElementsByTagName("name").item(0).getTextContent();
                    this.id=id;
                    this.favorTokens = Integer.parseInt(eElement.getElementsByTagName("token").item(0).getTextContent());
                    this.isFirstDie=true;

                    for (int temp2 = 0; temp2 < eElement.getElementsByTagName("data").getLength(); temp2++) {
                        row = Integer.parseInt(eElement.getElementsByTagName("row").item(temp2).getTextContent());
                        column = Integer.parseInt(eElement.getElementsByTagName("col").item(temp2).getTextContent());
                        cell[row][column]=new Cell(eElement.getElementsByTagName("data").item(temp2).getTextContent());
                    }
                }
            }

            for (int i=0; i<NUM_ROWS ; i++){
                for (int j=0; j<NUM_COLS ; j++){
                    if (cell[i][j] == null){
                        cell[i][j] = new Cell();
                    }
                }
            }

        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
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
    public List<Integer> listPossiblePlacements(Die die) throws IllegalDieException{
        ArrayList <Integer> list= new ArrayList<>();
        FullCellIterator diceIterator;
        Cell fullCell;
        diceIterator=new FullCellIterator(this.cell);

        if(isFirstDie){
            //first and last rows
            checkBorder(die, list);
        }else{
            while(diceIterator.hasNext()){
                fullCell=diceIterator.next();
                if(die.equals(fullCell.getDie())){
                    list.clear();
                    throw new IllegalDieException("! Trying to place the same die twice");
                }
                checkCloseCells(die, diceIterator, list);
            }
        }
        return list;
    }

    /**
     * Checks if cells close to the one given by the iterator(that contains a die) could accept the die
     * @param die the die to be possibly put in place
     * @param diceIterator iterates on full cells
     * @param list the list of possible positions for the die
     */
    private void checkCloseCells(Die die, FullCellIterator diceIterator, ArrayList<Integer> list) {
        int row;
        int column;
        Integer index;
        for(int i = -1; i<2; i++){
            row=diceIterator.getRow()+i;
            for(int j=-1; j<2; j++){
                column=diceIterator.getColumn()+j;
                if( (row>=0 && row<NUM_ROWS) && (column>=0 && column<NUM_COLS)){
                    index=(row * NUM_COLS + column);

                    if (!list.contains(index) && canBePlacedHere(row, column, die)) {
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
     */
    private void checkBorder(Die die, ArrayList<Integer> list) {
        int row;
        int column;
        //first and last rows
        for(row=0,column=0; column < NUM_COLS; column++){
            if(this.cell[row][column].canAcceptDie(die)){
                list.add(row * NUM_COLS + column);
            }
            if(this.cell[row+NUM_ROWS-1][column].canAcceptDie(die)){
                list.add((row+NUM_ROWS-1) * NUM_COLS + column);
            }
        }
        //first and last columns
        for(row=0,column=0; row<NUM_ROWS-1;row++){
            if(this.cell[row][column].canAcceptDie(die)){
                list.add(row * NUM_COLS );
            }
            if(this.cell[row][column+NUM_COLS-1].canAcceptDie(die)){
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
        if(!this.cell[row][column].canAcceptDie(die)){ return false; }

        if(this.cell[row][column].hasDie()){ return false; }

        if((row > 0)                    && this.cell[row - 1][column].hasDie() && this.cell[row - 1][column].checkNeighbor(die)) { return false; }

        if((row < (NUM_ROWS - 1))       && this.cell[row + 1][column].hasDie() && this.cell[row + 1][column].checkNeighbor(die)) { return false; }

        if((column > 0)                 && this.cell[row][column - 1].hasDie() && this.cell[row][column - 1].checkNeighbor(die)) { return false; }

        if((column < (NUM_COLS - 1))    && this.cell[row][column + 1].hasDie() && this.cell[row][column + 1].checkNeighbor(die)) { return false; }

        return true;
    }

    /**
     * Puts the die in place if possible, if not an exception is thrown
     * @param index index (0 to 19) where the die is going to be put
     * @param die die to be put
     */
    public void putDie (Integer index,Die die)throws IllegalDieException{

        assert this.listPossiblePlacements(die).contains(index);

        this.cell[index/NUM_COLS][index%NUM_COLS].setDie(die);
        this.isFirstDie=false;
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

}