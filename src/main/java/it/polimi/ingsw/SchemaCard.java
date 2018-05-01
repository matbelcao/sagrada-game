package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import org.xml.sax.SAXException;

public class SchemaCard extends Card {
    private int favorTokens;
    private Cell cell [][];

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
                    super.setParam(eElement.getElementsByTagName("name").item(0).getTextContent(),eElement.getElementsByTagName("imgSrc").item(0).getTextContent(),id);
                    favorTokens = Integer.parseInt(eElement.getElementsByTagName("favor").item(0).getTextContent());
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
     * Puts the die in place if possible, if not an exception is thrown
     * @param die die to be put
     * @param row row of the schema card
     * @param column column of the schema card
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
        return cell[].getDie();
    }
}