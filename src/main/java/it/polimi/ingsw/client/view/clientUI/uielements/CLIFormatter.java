package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.CLIFormats;
import it.polimi.ingsw.common.enums.Shade;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CLIFormatter {
    private Element elemFile;
    private static final String FORMATS_FILE_NAME="CLIFormats.xml";
    private static final String CLI_COMP= "cli-formats";


    public CLIFormatter() throws InstantiationException {
        this.elemFile=parser();
        if(this.elemFile==null){
            throw new InstantiationException();
        }
    }

    private static Element parser(){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile= classLoader.getResourceAsStream(Client.XML_SOURCE+FORMATS_FILE_NAME);
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

    /**
     * this method returns an uncolored big die that has the face corresponding to the param
     * @param face the wanted face of the die
     * @return the string containing a raw representation of the face
     */
    public String getBigDie(Shade face){

        return getString(face.toString());
    }

    /**
     * this method returns an element that is not the face of a die
     * @param elem the requested element
     * @return the string containing the element's representation
     */
    public String getElem(CLIFormats elem){
        return getString(elem.toString());
    }

    /**
     * this is the method that actually gets the requested element from the xml file
     * @param elem the wanted element
     * @return the string related to the element
     */
    private String getString(String elem) {
        try {
            return elemFile.getElementsByTagName(elem).item(0).getTextContent();
        }catch(NullPointerException e){
            throw new IllegalArgumentException();
        }
    }

}
