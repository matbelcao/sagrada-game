package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.xml.sax.SAXException;

/**
 * This class represents a schema card of the game
 */
public class SchemaCard extends Card implements Iterable{
    private String name;
    private int id;
    private int favorTokens;
    private Cell cell [][];
    private Boolean isFirstDie;
    static final int NUM_COLS=5,NUM_ROWS=4;

    /**
     * Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id ToolCard id
     * @param xmlSrc xml file path
     */
    public SchemaCard(int id, String xmlSrc){
        super();

        int row,column;
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
                        System.out.println(row+"  "+column+"  "+eElement.getElementsByTagName("data").item(temp2).getTextContent());
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

        }catch (SAXException e1) {
            e1.printStackTrace();
        }catch (ParserConfigurationException e2){
            e2.printStackTrace();
        }catch (IOException e3){
            e3.printStackTrace();
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
     * Calculates and returns a list of integers that are the indexes (from 0 to 19) where the die could be placed
     * @param die the die we want to place
     * @return the list of valid positions for the die
     */
    public ArrayList<Integer> listPossiblePlacements(Die die){
        int row;
        int column;
        DieIterator dice;
        Integer index;
        ArrayList <Integer> list= new ArrayList();

        if(isFirstDie){
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
        }else{
            dice=new DieIterator(cell);
            while(dice.hasNext()){
                dice.next();
                for(int i=-1; i<2; i++){
                    for(int j=-1; j<2; j++){
                        if((i!=0 && j!=0) && (dice.getRow()+i>=0 && dice.getRow()+i<NUM_ROWS && dice.getColumn()+j>=0 && dice.getColumn()+j<NUM_COLS)){
                            index=(dice.getRow()+i)*NUM_COLS + (dice.getColumn()+j);
                            if(!list.contains(index)) {
                                if(canBePlacedHere(dice.getRow()+i,dice.getColumn()+j,die)){
                                    list.add(index);
                                }
                            }
                        }
                    }
                }

            }
        }
        return list;
    }

    /**
     * Checks whether or not a die can be placed in a cell that is known to be adiacent to a die that is already placed
     * @param row  the row of the cell to be checked
     * @param column the column of the cell to be checked
     * @param die the die we want to place
     * @return true iff the die can be placed there
     */
    private Boolean canBePlacedHere(int row,int column, Die die){
        if(!this.cell[row][column].canAcceptDie(die)){
            return false;
        }
        if(row > 0){
            if(this.cell[row - 1][column].getDie()!=null) {
                if (this.cell[row - 1][column].getDie().getColor().equals(die.getColor()) || this.cell[row - 1][column].getDie().getShade().equals(die.getShade())) {
                    return false;
                }
            }
        }
        if(row < 3){
            if(this.cell[row + 1][column].getDie()!=null) {
                if (this.cell[row + 1][column].getDie().getColor().equals(die.getColor()) || this.cell[row + 1][column].getDie().getShade().equals(die.getShade())) {
                    return false;
                }
            }
        }
        if(column > 0){
            if(this.cell[row][column - 1].getDie()!=null) {
                if (this.cell[row][column - 1].getDie().getColor().equals(die.getColor()) || this.cell[row][column - 1].getDie().getShade().equals(die.getShade())) {
                    return false;
                }
            }
        }
        if(column < 4){
            if(this.cell[row][column + 1].getDie()!=null) {
                if (this.cell[row][column + 1].getDie().getColor().equals(die.getColor()) || this.cell[row][column + 1].getDie().getShade().equals(die.getShade())) {
                    return false;
                }
            }
        }
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
        }





    /**
     * Instantiates an iterator for cells containing a die
     * @return iterator on cells that contain a die
     */
    @NotNull
    @Override
    public Iterator iterator() {
        return new DieIterator(this.cell);
    }

    /**
     * This method has not been implemented
     * @param action
     */
    @Override
    public void forEach(Consumer action) {

    }

    /**
     * This method hasn't been implemented
     * @return spliterator
     */
    @Override
    public Spliterator spliterator() {
        return null;
    }
}