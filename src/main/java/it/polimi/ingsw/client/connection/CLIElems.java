package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class CLIElems {
    private Element elemFile;
    private static final String ELEM_FILE= Client.XML_SOURCE+"CLI.xml";
    private static final String CLI_COMP= "cli-components";
    private static final String BIG_EL="[A-Z]+";

    public CLIElems() {

        File xmlFile = new File(ELEM_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            elemFile = (Element)doc.getElementsByTagName(CLI_COMP).item(0);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();

        }
    }

    public String getBigDie(String face){
        if(face.matches(BIG_EL)) {
            return elemFile.getElementsByTagName(face).item(0).getTextContent();
        }
        throw new IllegalArgumentException();
    }
}
