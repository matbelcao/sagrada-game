package it.polimi.ingsw.client.view.clientui.uielements;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.view.clientui.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientui.uielements.enums.UIMsg;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIMessages {
    private Element msgFile;
    private static final String MSG="messages";
    private final UILanguage lang;
    private static final Integer FIRST=0;
    private static final String UIMESSAGES_FILE_NAME= "UIMessages.xml";

    public UIMessages(UILanguage lang) {
        this.lang = lang;
        createXmlParser();
   }

    /**
     * this sets the xml reader ready to gather the needed messages
     */
    private void createXmlParser() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile= classLoader.getResourceAsStream(Client.XML_SOURCE+UIMESSAGES_FILE_NAME);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
           dBuilder = dbFactory.newDocumentBuilder();
           Document doc = dBuilder.parse(xmlFile);
           doc.getDocumentElement().normalize();
           msgFile = (Element)doc.getElementsByTagName(MSG).item(FIRST);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());

        }
    }

    /**
     * @param msgName the desired message
     * @return the string containing the messages
     */
    public  String getMessage(UIMsg msgName){
        Element msg= (Element) msgFile.getElementsByTagName(msgName.toString().toLowerCase()).item(FIRST);
        return msg.getElementsByTagName(lang.toString().toLowerCase()).item(FIRST).getTextContent();
   }

}
