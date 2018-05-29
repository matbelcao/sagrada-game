package it.polimi.ingsw.client.uielements;

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

    public CLIElems() throws InstantiationException {
        this.elemFile=parser();
        if(this.elemFile==null){
            throw new InstantiationException();
        }
    }

    private Element parser(){
        File xmlFile = new File(ELEM_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return (Element)doc.getElementsByTagName(CLI_COMP).item(0);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getBigDie(String face){
        if(face.matches(BIG_EL)) {
            return getElem(face);
        }
        throw new IllegalArgumentException();
    }

    public String getElem(String elem){
        try {
            return elemFile.getElementsByTagName(elem).item(0).getTextContent();
        }catch(NullPointerException e){
            throw new IllegalArgumentException();
        }
    }

}
