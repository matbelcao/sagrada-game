package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.ClientUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class UIMessages {
    private Element msgFile;
    private static final String MSG="messages";
    private final UILanguage lang;

    public UIMessages(UILanguage lang) {
        this.lang = lang;
        File xmlFile = new File(ClientUI.MESSAGES_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
           dBuilder = dbFactory.newDocumentBuilder();
           Document doc = dBuilder.parse(xmlFile);
           doc.getDocumentElement().normalize();
           msgFile = (Element)doc.getElementsByTagName(MSG).item(0);

        } catch (ParserConfigurationException | SAXException | IOException e) {
           e.printStackTrace();

        }
   }
   public  String getMessage(String msgName){
        Element msg= (Element) msgFile.getElementsByTagName(msgName).item(0);
        return msg.getElementsByTagName(lang.toString()).item(0).getTextContent();
   }

}
