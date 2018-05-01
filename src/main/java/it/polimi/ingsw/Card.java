package it.polimi.ingsw;

/**
 * This abstract class is useful to the subclasses [ToolCard , ObjectiveCard and SchemaCard] to initialize common parameters
 */
public abstract class Card {
    private String name;
    private String imgSrc;
    private String description;
    private int id;


    /**
     * Sets the card properties
     * @param name card name
     * @param imgSrc image path
     * @param description description of the card
     * @param id card id
     */
    protected void setParam(String name, String imgSrc,String description, int id){
        this.name = name;
        this.imgSrc = imgSrc;
        this.description= description;
        this.id=id;
    }

    /**
     * Returns the name of the card
     * @return name
     */
    public String getName(){ return this.name; }

    /**
     * Returns the directory path of the relative image
     * @return imgSrc
     */
    public String getImgSrc(){
        return this.imgSrc;
    }

    /**
     * Returns the description of the card
     * @return a string containing the description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Returns the card id
     * @return id
     */
    public int getId(){
        return this.id;
    }
}
