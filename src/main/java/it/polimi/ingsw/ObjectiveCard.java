package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * This abstract class is useful to the subclasses [PrivObjectiveCard and PubObjectiveCard ] to initialize common parameters
 */
public abstract class ObjectiveCard extends Card{
    private String description;

    /**
     * Retrieve from the xml file the ObjectiveCard(id) data and instantiate it
     * @param id ToolCard id
     * @param xmlSrc xml path
     * @param type PrivObjectiveCard or PubObjectiveCard
     */
    public ObjectiveCard(int id, String xmlSrc, String type){
        super();

        File xmlFile= new File(xmlSrc);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            if(type.equals("PrivObjectiveCard")){
                nodeList = doc.getElementsByTagName("PrivObjectiveCard");
            }else{
                nodeList = doc.getElementsByTagName("PubObjectiveCard");
            }
            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {
                Element eElement = (Element)nodeList.item(temp);
                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    super.setParam(eElement.getElementsByTagName("name").item(0).getTextContent(),eElement.getElementsByTagName("imgSrc").item(0).getTextContent(),id);
                    description = new String(eElement.getElementsByTagName("description").item(0).getTextContent());
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
     * Changes the ObjectiveCard properties
     * @param description card description
     */
    protected void setDescription(String description){
        this.description=new String(description);
    }

    /**
     * Returns the ObjectiveCard description
     * @return card description
     */
    public String getDescription(){
        return new String(description);
    }
}
