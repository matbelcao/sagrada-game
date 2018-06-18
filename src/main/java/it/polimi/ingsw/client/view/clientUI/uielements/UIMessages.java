package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.view.clientUI.ClientUI;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
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
    private static final Integer FIRST=0;

    public UIMessages(UILanguage lang) {
        this.lang = lang;
        File xmlFile = new File(ClientUI.MESSAGES_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
           dBuilder = dbFactory.newDocumentBuilder();
           Document doc = dBuilder.parse(xmlFile);
           doc.getDocumentElement().normalize();
           msgFile = (Element)doc.getElementsByTagName(MSG).item(FIRST);

        } catch (ParserConfigurationException | SAXException | IOException e) {
           e.printStackTrace();

        }
   }
   public  String getMessage(UIMsg msgName){
        Element msg= (Element) msgFile.getElementsByTagName(msgName.toString()).item(FIRST);
        return msg.getElementsByTagName(lang.toString()).item(FIRST).getTextContent();
   }

}
