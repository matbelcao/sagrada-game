package it.polimi.ingsw;

/**
 * This abstract class is useful to the subclasses [ToolCard , ObjectiveCard and SchemaCard] to initialize common parameters
 */
public abstract class Card {
    private String name;
    private String imgSrc;
    private int id;

    /**
     * Initialize the common parameters
     * @param name name of the card
     * @param imgSrc directory path of the image
     * @param id unique id of the card
     */
    public Card(String name, String imgSrc, int id){
        this.name = new String(name);
        this.imgSrc = new String(imgSrc);
        this.id = id;
    }

    /**
     * Returns the name of the card
     * @return name
     */
    public String getName(){
        return new String (name);
    }

    /**
     * Returns the directory path of the image
     * @return imgSrc
     */
    public String getImgSrc(){
        return new String (imgSrc);
    }

    /**
     * Returns the card id
     * @return id
     */
    public int getId(){
        return id;
    }
}
