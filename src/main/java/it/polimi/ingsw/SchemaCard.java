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

    /**
     * Retrieves the SchemaCard(id) data from the xml file and instantiates it
     * @param id ToolCard id
     * @param xmlSrc xml file path
     */
    public SchemaCard(int id, String xmlSrc){
        super();

        cell = new Cell[4][5];

        File xmlFile= new File(xmlSrc);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("SchemaCard");
            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {

                Element eElement = (Element)nodeList.item(temp);

                //va aggiunto caricamento constraints

                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    this.name=eElement.getElementsByTagName("name").item(0).getTextContent();
                    this.id=id;
                    this.favorTokens = Integer.parseInt(eElement.getElementsByTagName("favor").item(0).getTextContent());
                    this.isFirstDie=true;
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

    public ArrayList<Cell> canBePlacedHere(Die die){
        int row,column;
        ArrayList <Cell> list= new ArrayList();
        if(isFirstDie){
            //first and last rows
            for(row=0,column=0; column < 5;column++){
                if(this.cell[row][column].canAcceptDie(die)){
                    list.add(this.cell[row][column]);
                }
                if(this.cell[row+3][column].canAcceptDie(die)){
                    list.add(this.cell[row][column]);
                }
            }
            //first and last columns
            for(row=0,column=0; row<3;row++){
                if(this.cell[row][column].canAcceptDie(die)){
                    list.add(this.cell[row][column]);
                }
                if(this.cell[row][column+4].canAcceptDie(die)){
                    list.add(this.cell[row][column]);
                }
            }
        }
        return list;
    }


    /**
     * Puts the die in place if possible, if not an exception is thrown
     * @param die die to be put
     * @param row row of the schema card
     * @param column column of the schema card
     * @throws IllegalDieException
     */
    public void putDie (Die die, int row, int column) throws IllegalDieException{

        }

    /**
     * Returns the die in the cell(x,y) or NULL if it's empty
     * @param row x coordinate
     * @param column y coordinate
     * @return die pornter
     */
    public Die getCellConstraint(int row, int column){
        return cell[row][column].getDie();
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return new DieIterator(this.cell);
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}