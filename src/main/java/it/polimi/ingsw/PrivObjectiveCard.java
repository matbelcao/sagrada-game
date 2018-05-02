package it.polimi.ingsw;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.IOException;
import org.xml.sax.SAXException;

public class PrivObjectiveCard extends Card{
    Color color;

    public PrivObjectiveCard(int id, String xmlSrc){
        super();

        String imgSrc;
        File xmlFile= new File(xmlSrc);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        NodeList nodeList;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            nodeList = doc.getElementsByTagName("PrivObjectiveCard");

            for (int temp = 0; temp < nodeList.getLength() && (temp-1)!=id; temp++) {
                Element eElement = (Element)nodeList.item(temp);
                if(Integer.parseInt(eElement.getAttribute("id"))==id){
                    imgSrc= eElement.getElementsByTagName("imgSrc").item(0).getTextContent();
                    imgSrc=imgSrc.replaceAll("[\\ ]", File.separator);
                    super.setParam(eElement.getElementsByTagName("name").item(0).getTextContent(),imgSrc,eElement.getElementsByTagName("description").item(0).getTextContent(),id);
                    color=Color.valueOf(eElement.getElementsByTagName("color").item(0).getTextContent());
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

    public String getColor(){
        return color.toString();
    }
}