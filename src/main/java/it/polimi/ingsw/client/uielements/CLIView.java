package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.immutables.CellContent;
import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CLIView {
    private static final int SCHEMA_WIDTH = 38 ;
    private static final int SCHEMA_HEIGHT = 16;
    private final ArrayList<String> topInfo= new ArrayList<>();
    private String topSectionDetails="";
    private ArrayList<String> topSection= new ArrayList<>();
    private String topSectionSeparators;
    private String tracksDetails;
    private final ArrayList<String> tracks= new ArrayList<>();
    private String bottomDetails;
    private final ArrayList<String> bottom=new ArrayList<>();
    private static final CLIElems cliElems= new CLIElems();
    private final UIMessages uiMsg;
    public CLIView(UILanguage lang){
        this.uiMsg=new UIMessages(lang);
    }



    /**
     * creates a list of  four strings  that represent a row of cells containing or not dice or constraints
     * @param elems a map containing the elements to be represented
     * @param from the index of the first element to be put inside the result (if an element is not in the Map an empty cell is added)
     * @param to the index of the last one
     * @return the said representation
     */
    public static List<String> buildBigRow(Map<Integer,CellContent> elems, int from, int to){
        assert(from<=to && from>=0);
        ArrayList<String> result=new ArrayList<>();
        result.add("");
        result.add("");
        result.add("");
        result.add("");
        String [] rows;
        rows = new String[4];
        for(int i=from;i<to;i++) {

            //empty cell
            if (!elems.containsKey(i)) {
                rows = splitElem(cliElems.getBigDie("EMPTI"));
            }

            //not empty
            //die
            if (elems.containsKey(i) && elems.get(i).isDie()) {
                rows = splitElem(cliElems.getBigDie(elems.get(i).getShade().toString()));
            }

            //constraint
            if (elems.containsKey(i) && !elems.get(i).isDie()) {
                if (elems.get(i).hasColor()) {

                    //color constraint
                    rows = splitElem(cliElems.getBigDie("FILLED"));
                } else {

                    //shade constraint
                    rows = splitElem(cliElems.getBigDie(elems.get(i).getShade().toString()));
                }
            }

            //add color to colored things
            if (elems.get(i).hasColor()) {
                rows=addColor(rows,elems.get(i).getColor());
            }

            assert (result.size() == 4);
            //append new cell/die/constraint
            result = (ArrayList<String>) appendBigRows(result, Arrays.asList(rows));
        }
        return result;
    }

    /**
     * builds a list containing strings that represent the schema of a player
     * @param schema
     * @return
     */
    public static List<String> buildSchema(LightSchemaCard schema){
        ArrayList<String> schem= new ArrayList<>();
        for(int row=0; row< SchemaCard.NUM_ROWS;row++){
            schem.addAll(buildBigRow(schema.getCellsMap(),row*SchemaCard.NUM_COLS,(row+1)*SchemaCard.NUM_COLS ));
        }
        return schem;
    }
    /**
     * This method sets the line that will be at the top of the interface and will contain generic info about the user
     * @param mode the type of connection the user is using
     * @param username the username
     * @param playerId the player id of the user
     */
    public void setClientInfo(ConnectionMode mode, String username, int playerId){
        if(mode==null||username==null||(playerId<0||playerId>3)){ throw new IllegalArgumentException();}

        String info = String.format(cliElems.getElem("player-info"),
                username,
                uiMsg.getMessage("connected-via"),
                mode.toString(),
                uiMsg.getMessage("player-number"),
                playerId);

        topInfo.set(0,info);
    }



    /**
     * this puts the schemas at the beginning of the top portions ( it will also clear what was previously in all topSection related strings ).
     * Must be called before all others topSection-related builders
     * @param players the map containing the participants
     * @param playerId the player id whose view we are building
     * @param numPlayers the number of participants
     */
    private void buildTopSectionSchemas(Map<Integer,LightPlayer> players, int playerId,int numPlayers){
        //create separator of the appropriate size
        topSectionSeparators=new String(new char[numPlayers-1]).replace("\0", cliElems.getElem("schema-border"));

        if(players.isEmpty() || numPlayers<2||numPlayers>4 || playerId>numPlayers-1||playerId<0){
            throw new IllegalArgumentException();
        }
        topSection.clear();
        topSection=(ArrayList<String>) appendBigRows(topSection,buildSchemaSeparator());

        StringBuilder builder= new StringBuilder();

        for(int i=0; i < numPlayers; i++){
            if(i!=playerId){
                //append others schemas to the top section
                topSection=(ArrayList<String>) appendBigRows(topSection,buildSchema(players.get(i).getSchema()));
                //append chars to separate schemas each one from another
                topSection=(ArrayList<String>) appendBigRows(topSection,buildSchemaSeparator());

                String playerDetails=String.format(cliElems.getElem("username-id"),
                        players.get(i).getUsername(),
                        uiMsg.getMessage("player-number"),
                        players.get(i).getPlayerId());

                builder.append(padUntil(playerDetails,SCHEMA_WIDTH));
            }
        }
        topSectionDetails=builder.toString();
    }

    private void buildTopSectionAppendObjectives(ArrayList<LightCard> pubObj,LightCard privObj){
        topSection=(ArrayList<String>) appendBigRows(topSection,buildObjectives(pubObj,privObj));
        // TODO: 27/05/2018
    }

    private List<String> buildObjectives(ArrayList<LightCard> pubObj,LightCard privObj){
        ArrayList<String> result=new ArrayList<>();
        // TODO: 27/05/2018
        return result;
    }
    /**
     * creates a list containing characters needed to visually separate schemas on the screen
     * @return said list
     */
    private List<String> buildSchemaSeparator(){
        ArrayList<String> separator= new ArrayList<>();
        for(int i=0;i< SCHEMA_HEIGHT;i++){
            separator.add(" | ");
        }
        return separator;
    }

    private String padUntil(String toPad, int finalLenght){
        if(finalLenght<0|| toPad==null||toPad.length()>finalLenght){throw new IllegalArgumentException();}

        return toPad+ new String(new char[finalLenght - toPad.length()]).replace("\0", " ");

    }

    /**
     * Adds the color to the cell being added to the "bigRow"
     * @param rows the rows to be added to the "bigRow"
     * @param color the color to be added
     * @return the new array of string modified as said
     */
    public static String [] addColor(String[] rows, Color color) {
        String [] result= new String[rows.length];
        if(rows.length==0){throw new IllegalArgumentException();}
        for(int i=0;i<rows.length;i++){
            result[i] = addColorToLine(rows[i],color);
        }
        return result;
    }

    /**
     * returns a copy of the string with added colors
     * @param line the string to apply color to
     * @param color the color to apply
     * @return the colored string
     */
    public static String addColorToLine(String line, Color color){
        return color.getUtf()+line+Color.RESET;
    }

    /**
     * appends two lists of strings one  by one one to the other to create a new list (number of strings)
     * @param a the list of strings that will come first in the resulting list
     * @param b the list of strings being appended to a
     * @return the result of appending, string by string, a to b
     */
    public static List<String> appendBigRows(List<String> a,List<String> b){
        if(a==null || b==null || (!a.isEmpty() && a.size()<b.size())){
            throw new IllegalArgumentException();
        }
        ArrayList<String> result= new ArrayList<>();
        for(int row=0; row<a.size();row++){
            result.add(row,a.get(row)+b.get(row));
        }
        return result;
    }

    /**
     * this method splits the element read from an xml file by on "::"
     * @param elem the string to be split
     * @return the array of strings containing the parts of the elem divided by "::"
     */
    private static String[] splitElem(String elem){
        return elem.split("::");
    }

}


