package it.polimi.ingsw;

public class Player {
    private String username;
    private PrivObjectiveCard privObjective;
    private SchemaCard schema;
    private Integer favorTokens;
    private Integer finalPosition;
    //to be continued

    public void chooseSchemaCard(Integer id){
        schema = new SchemaCard(id,GameController.xmlSource+"SchemaCard.xml");
    }

    public void chooseSchemaCard(Integer id,String schemaCardFilename){ //for extra schemacards
        schema = new SchemaCard(id,GameController.xmlSource+schemaCardFilename);
    }


}
