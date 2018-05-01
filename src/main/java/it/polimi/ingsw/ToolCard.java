package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import org.xml.sax.SAXException;

public class ToolCard extends Card{
    private boolean used;
    //private ToolAction tool;

    /**
     * Retrieve from the xml file the ToolCard(id) data and instantiate it
     * @param id ToolCard id
     * @param xmlSrc xml path
     */
    public ToolCard(int id, String xmlSrc){
        super();

        File xmlFile= new File(xmlSrc);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("ToolCard");
            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {
                Element eElement = (Element)nodeList.item(temp);
                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    super.setParam(eElement.getElementsByTagName("name").item(0).getTextContent(),eElement.getElementsByTagName("imgSrc").item(0).getTextContent(),eElement.getElementsByTagName("description").item(0).getTextContent(),id);
                    this.used=false;
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
     * Return if the toolcard has been used during the game
     * @return true if it has been used, false if not
     */
    public boolean hasBeenUsed(){
        return this.used;
    }



}
