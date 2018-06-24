package it.polimi.ingsw.server.model;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import java.util.Objects;

import org.xml.sax.SAXException;

/**
 * This abstract class is useful to the subclasses [ToolCard , ObjectiveCard and SchemaCard] to initialize common parameters
 */
public abstract class Card {
    private String name;
    private String imgSrc;
    private String description;
    private int id;

    /**
     * Read the xml file and initialize the card's common parameter
     * @param id card's id
     * @param xmlSrc xml file path
     * @param type card type
     * @return  color string if type is PrivObjectiveCard, else null
     */
    protected String xmlReader(int id, String xmlSrc, String type){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File xmlFile= new File(Objects.requireNonNull(classLoader.getResource(xmlSrc)).getFile());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            nodeList = doc.getElementsByTagName(type);

            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {
                Element eElement = (Element)nodeList.item(temp);
                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    this.id=id;
                    this.name=eElement.getElementsByTagName("name").item(0).getTextContent();
                    this.imgSrc= eElement.getElementsByTagName("imgSrc").item(0).getTextContent().replace("::",File.separator);
                    this.description=eElement.getElementsByTagName("description").item(0).getTextContent();

                    return type.equals("PrivObjectiveCard")? eElement.getElementsByTagName("color").item(0).getTextContent() : null;
                }
            }
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the name of the card
     * @return namecd
     */
    public String getName(){ return this.name; }

    /**
     * Returns the directory path of the relative image
     * @return imgSrc
     */
    public String getImgSrc(){
        return this.imgSrc;
    }

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
