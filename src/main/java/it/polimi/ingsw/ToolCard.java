package it.polimi.ingsw;

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
        super.xmlReader(id,xmlSrc,"ToolCard");
        this.used=false;
    }

    /**
     * Return if the toolcard has been used during the game
     * @return true if it has been used, false if not
     */
    public boolean hasBeenUsed(){
        return this.used;
    }

}
