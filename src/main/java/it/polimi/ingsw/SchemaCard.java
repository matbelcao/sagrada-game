package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import org.xml.sax.SAXException;

public class SchemaCard extends Card {
    private int favorTokens;
    private Cell cell [];

    /**
     * Retrieve from the xml file the SchemaCard(id) data and instantiate it
     * @param id ToolCard id
     * @param xmlSrc xml path
     */
    public SchemaCard(int id, String xmlSrc){
        super();
        cell = new Cell[20];

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
     * Check if the cell(x,y) can be put the die and do it if it's possible
     * @param d die tu put
     * @param x x coordinate
     * @param y y coordinate
     * @return true if do it
     */
    public boolean putDie (Die d, int x, int y){
            if ( x>=1 && y>=1 && cell[(x-1)*5+(y-1)].canAcceptDie(d)){
                try{
                    cell[x*5+y-1].setDie(d);
                }catch (IllegalDieException ex){
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }

    /**
     * Returns the die in the cell(x,y) or NULL if it's empry
     * @param x x coordinate
     * @param y y coordinate
     * @return die pornter
     */
    public Die getCellContent(int x, int y){
        return cell[x*5+y-1].getDie();
    }
}