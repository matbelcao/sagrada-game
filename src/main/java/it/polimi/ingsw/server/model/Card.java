package it.polimi.ingsw.server.model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This abstract class is useful to the subclasses [ToolCard , ObjectiveCard and SchemaCard] to initialize common parameters
 */
public abstract class Card {
    private static final String IDD = "id";
    private static final String NAMEE = "name";
    private static final String DESCRIPTIONN = "description";
    private static final String PRIV_OBJECTIVE_CARD = "PrivObjectiveCard";
    private static final String DIE_COLOR = "dieColor";
    private String name;

    private String description;
    private int id;

    /**
     * Read the xml file and initialize the card's common parameter
     * @param id card's id
     * @param xmlSrc xml file path
     * @param type card type
     * @return  color string if type is PrivObjectiveCard, else null
     */
    String xmlReader(int id, String xmlSrc, String type){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile= classLoader.getResourceAsStream(xmlSrc);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            nodeList = doc.getElementsByTagName(type);

            Element eElement = (Element)nodeList.item(id-1);
            if(Integer.parseInt(eElement.getAttribute(IDD))==id){
                this.id=id;
                this.name=eElement.getElementsByTagName(NAMEE).item(0).getTextContent();

                this.description=eElement.getElementsByTagName(DESCRIPTIONN).item(0).getTextContent();

                return type.equals(PRIV_OBJECTIVE_CARD)? eElement.getElementsByTagName(DIE_COLOR).item(0).getTextContent() : null;
            }

        }catch (SAXException | ParserConfigurationException | IOException e1) {
            Logger.getGlobal().log(Level.INFO,e1.getMessage());
        }
        return null;
    }

    /**
     * Returns the name of the card
     * @return namecd
     */
    public String getName(){ return this.name; }


    /**
     * Returns the description of the card
     * @return a string containing the description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Returns the card id
     * @return id
     */
    public int getId(){
        return this.id;
    }
}
