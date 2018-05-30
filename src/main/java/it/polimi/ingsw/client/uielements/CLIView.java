package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.LightBoard;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.*;

public class CLIView {
    private static final int SCHEMA_WIDTH = 35;
    private static final int SCHEMA_HEIGHT = 18;
    private static final int CELL_HEIGHT = 4;
    private static final int CELL_WIDTH = 7;
    private static final int OBJ_LENGTH = 38;
    private static final int SCREEN_WIDTH = 160;


    private String bottomInfo ;
    private String turnRoundinfo;
    private final HashMap<Integer,List<String>> schemas= new HashMap<>();
    private List<String> objectives;
    private final List<String> tools= new ArrayList<>();

    private List<String> roundTrack= new ArrayList<>();
    private List<String> draftPool= new ArrayList<>();
    private final List<String> menuList =new ArrayList<>();
    private static CLIElems cliElems;
    private final UIMessages uiMsg;
    private int numPlayers;
    private int playerId;
    private int nowPlaying;

    public CLIView(UILanguage lang) throws InstantiationException {
        cliElems=new CLIElems();
        this.uiMsg=new UIMessages(lang);

    }

    public String printMainView(){
        StringBuilder builder=new StringBuilder();

        builder.append(printList(buildRoundTrack())).append(" |%n");


        builder.append(printList(buildTopSection())).append(" |%n");


        builder.append(printList(buildDraftPool())).append(" |%n");

        builder.append(printList(appendRows(schemas.get(playerId),menuList))).append("%n");

        builder.append(padUntil("",SCHEMA_WIDTH+6,' ')+String.format(cliElems.getElem("prompt"),uiMsg.getMessage("message-prompt")));

        return builder.toString();
    }



    private static String printList(List<String> toPrint){
        if(toPrint==null){throw new IllegalArgumentException();}
        StringBuilder builder=new StringBuilder();
        for(String line : toPrint) {
            builder.append(line).append("%n");
        }
        return builder.toString();
    }

    private List<String> buildTopSection(){
        List<String> result=new ArrayList<>();
        for(Map.Entry<Integer,List<String>> entry : schemas.entrySet() ){
            if(entry.getKey() != playerId){
                result= appendRows(result,entry.getValue());
            }
        }
        result= appendRows(result,buildWall(' ',result.size(), (LightBoard.MAX_PLAYERS-numPlayers)*(SCHEMA_WIDTH+6)));
        result= appendRows(result,buildSeparator(SCHEMA_HEIGHT+1));
        result= appendRows(result,objectives);
      return result;
    }


    /**
     * Creates a list of possible placements for a die
     * @param placements the list of placements
     * @param destination the place the possible placements refer to
     * @param die the die to be placed
     */
    public void updateMenuList(List<Integer> placements, Place destination, LightDie die){
        List<String> msg= new ArrayList<>();
        msg.addAll(buildWall(' ',CELL_HEIGHT-1,1));
        msg.add(bold(uiMsg.getMessage("can-be-placed")));

        menuList.addAll(appendRows(buildCell(die),msg));
        menuList.add(" ");

        if(destination.equals(Place.SCHEMA)){
            menuList.addAll(buildCoordinatesList(placements));
        }else{
            menuList.addAll(buildIndexList(placements));
        }

    }

    private List<String> buildIndexList(List<Integer> placements) {
        List<String> list= new ArrayList<>();
        for(int i=0; i<placements.size();i++){
            list.add(String.format(cliElems.getElem("li"), i, index(placements.get(i))));
        }
        return list;

    }

    private String index(Integer index) {
        return String.format(cliElems.getElem("index"),uiMsg.getMessage("pos"),index);

    }

    private List<String> buildCoordinatesList(List<Integer> placements) {
        List<String> list= new ArrayList<>();
        for(int i=0; i<placements.size();i++){
            list.add(String.format(cliElems.getElem("li"), i, rowColmumn(placements.get(i))));
        }
        return list;
    }


    /**
     * creates the representation of the player's schema and puts it into the map
     * @param player the player whose schema we want to create/update
     */
    public void updateSchema(LightPlayer player){
        this.schemas.put(player.getPlayerId(), buildSchema(player));
        if(player.getPlayerId()==playerId) {
            this.schemas.get(playerId).set(0, bold(schemas.get(playerId).get(0)));
        }
    }

    public void updateRoundTurn(int roundNumber, int turnNumber,int nowPlaying){
        this.nowPlaying=nowPlaying;
        turnRoundinfo= String.format(cliElems.getElem("round-turn"),
                uiMsg.getMessage("round"),
                roundNumber,
                uiMsg.getMessage("turn"),
                turnNumber);
        List<String> updateSchema= new ArrayList<>();

        updateSchema=schemas.get(nowPlaying);
        Random randomGen = new Random();
        updateSchema.set(0,addColorToLine(bold(updateSchema.get(0)),Color.values()[randomGen.nextInt(Color.values().length)]));
    }

    /**
     * Updates the tools following a change in the used state of them
     * @param tools the list of the match tools
     */
    public void updateTools(List<LightTool> tools){
        for(int i=0;i < Board.NUM_TOOLS;i++){
            this.tools.add(String.format(cliElems.getElem("tool-index"),uiMsg.getMessage("tool-number"),i));
            this.tools.addAll(buildTool(tools.get(i)));
        }
    }


    private  String rowColmumn(int index){
        int row= index/SchemaCard.NUM_COLS;
        int column= index%SchemaCard.NUM_COLS;
        return String.format(cliElems.getElem("row-col"),uiMsg.getMessage("row"),row,uiMsg.getMessage("col"),column);
    }

    /**
     * This method creates the representation of the objectives
     * @param pubObj the list of public objectives
     * @param privObj the private objective
     */
    public void updateObjectives(List<LightCard> pubObj, LightPrivObj privObj){
        this.objectives= buildObjectives(pubObj,privObj);
    }


    /**
     * updates the draftpool representation
     * @param draftPool the new draftpool
     */
    public void updateDraftPool(List<LightDie> draftPool){

        this.draftPool= buildDiceRow( listToMap(draftPool),0,numPlayers*2+1);

        this.draftPool.add(padUntil("",SCREEN_WIDTH,'–'));
    }

    private List<String > buildRoundTrack(){
        List<String> result=new ArrayList<>();
        result.addAll(this.roundTrack);

        result.add(padUntil("", SCREEN_WIDTH,'–'));
        result= appendRows(buildSeparator(result.size()),result);

        return result;
    }

    /**
     * creates a rectangle filled with the filler of the  selected sizes
     * @param filler the character used as a filler
     * @param height the height of the rectangle
     * @param width the width of the rectangle
     * @return the wall
     */
    private List<String> buildWall(char filler,int height, int width){
        if(height<0||width<0){ throw new IllegalArgumentException();}

        List<String> result=new ArrayList<>();
        for(int row=0;row<height;row++){
            result.add(padUntil("",width,filler));
        }
        return result;
    }

    public void updateRoundTrack(List<List<CellContent>> roundTrack){
        List<String> result=new ArrayList<>();
        int maxLength=0;

        //calculating max number of leftover dice in a single round
        for(List<CellContent> round:roundTrack){
            maxLength= (maxLength<round.size()?round.size():maxLength);
        }
        //building from the top row
        for(int row=maxLength-1; row>0; row-- ){
            List<String> builtRow=new ArrayList<>();
            for(int round=0; round<10;round++){
                try{
                    builtRow= appendRows(builtRow,buildCell(roundTrack.get(round).get(row)));
                }catch (IndexOutOfBoundsException e){
                    builtRow= appendRows(builtRow,buildWall(' ',CELL_HEIGHT,CELL_WIDTH));
                }
            }
            result.addAll(builtRow);
        }

        Map<Integer,CellContent> baseRow= new HashMap<>();

        for(int round=0; round<roundTrack.size(); round++){
            baseRow.put(round, roundTrack.get(round).get(0));
        }

        result.addAll(buildCellRow(baseRow,0,10));
        List<String> padding=buildWall(' ',result.size()-1,1);
        padding.add(bold(uiMsg.getMessage("roundtrack")));
        result= appendRows(result,padding);
        this.roundTrack=result;
    }

    private List<String> buildDraftPool(){
        List<String> result=new ArrayList<>();

        result.addAll(draftPool);
        result.set(CELL_HEIGHT-1,
                result.get(CELL_HEIGHT-1)+
                        bold(uiMsg.getMessage("draftpool")+
                                alignRight(bottomInfo,
                                        SCREEN_WIDTH  - (2*numPlayers+1)*CELL_WIDTH - uiMsg.getMessage("draftpool").length())));
        result= appendRows(buildSeparator(draftPool.size()),result);
        return result;
    }

    /**
     * converts a list of light dice in a map of light dice preserving the order
     * @param list the list to convert
     * @return the converted list as a map
     */
    private static Map<Integer,LightDie> listToMap(List<LightDie> list) {
        if(list==null){
            throw new IllegalArgumentException();
        }
        HashMap<Integer, LightDie> map = new HashMap<>();
        for (int i = 0; i<list.size();i++){
            map.put(i,list.get(i));
        }
        return map;
    }

    /**
     * builds a row of LightDie
     * @param elems the map containing the dice
     * @param from the index where to start from
     * @param to the index where to end(this will not be in the result)
     * @return the representation of the row
     */
    private static List<String> buildDiceRow(Map<Integer,LightDie> elems, int from, int to){
        assert(from<=to && from>=0);
        List<String> result=new ArrayList<>();

        for(int i=from;i<to;i++) {
            //empty cell
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert (result.size() == 4);
        //append new cell/die/constraint

        return result;
    }


    /**
     * creates a list of  four strings  that represent a row of cells containing or not dice or constraints
     * @param elems a map containing the elements to be represented
     * @param from the index of the first element to be put inside the result (if an element is not in the Map an empty cell is added)
     * @param to the index of the last one
     * @return the said representation
     */
    private static List<String> buildCellRow(Map<Integer,CellContent> elems, int from, int to){
        assert(from<=to && from>=0);
        List<String> result=new ArrayList<>();
        for(int i=from;i<to;i++) {
            //append new cell/die/constraint
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert(result.size()==4);
        return result;
    }

    /**
     * creates a string that is the original string aligned to the right in a line of a certain size
     * @param line the line to align
     * @param size the size of the space
     * @return the aligned string
     */
    private static String alignRight(String line, int size){
        if(line==null||line.length()>size){ throw new IllegalArgumentException();}
        return padUntil("",size-line.length(),' ')+line;
    }



    /**
     * builds a list containing strings that represent the schema of a player
     * @return the representation of the schema
     */
    private List<String>  buildSchema(LightPlayer player){
        int width=SCHEMA_WIDTH + (playerId==player.getPlayerId()?2:0);

        //add top info
        List<String> schem = new ArrayList<>();


        //add top info
        schem.addAll(0,fitInLength(buildSchemaInfo(player), width));
        //add top border
        schem.add(fitInLength(buildSchemaInfo(player), width).size(),padUntil("",width,'–'));

        if(player.getPlayerId()==playerId){
                schem.add(cliElems.getElem("schema-cols"));

        }

        //build schema
        for(int row=0; row< SchemaCard.NUM_ROWS;row++){

            schem.addAll(appendRows(
                    player.getPlayerId()==playerId?
                            buildSchemaRowsIndex(row):
                            new ArrayList<String>(CELL_HEIGHT)
                    ,buildCellRow(player.getSchema().getCellsMap(),row*SchemaCard.NUM_COLS,(row+1)*SchemaCard.NUM_COLS )));
        }


        int height= SCHEMA_HEIGHT+ (playerId==player.getPlayerId()?1:0);
        //add bottom border
        schem.add(height,padUntil("",width,'–'));
        //add left/right borders
        schem= appendRows(
                buildSeparator(height+1)
                ,schem);
        schem= appendRows(schem,buildSeparator(height+1));

        return schem;
    }
    /**
     * This method sets the line that will be at the top of the interface and will contain generic info about the user
     * @param mode the type of connection the user is using
     * @param username the username
     */
    public void setClientInfo(ConnectionMode mode, String username){
        if(mode==null||username==null){ throw new IllegalArgumentException();}

        String info = String.format(cliElems.getElem("player-info"),
                username,
                uiMsg.getMessage("connected-via"),
                mode.toString(),
                uiMsg.getMessage("player-number"),
                playerId);

        bottomInfo=info;
    }


    private List<String> buildSchemaRowsIndex(int row){
        List<String> index= new ArrayList<>();

        for(int i=0; i<CELL_HEIGHT;i++){
            index.add("  ");
        }
        index.set(CELL_HEIGHT/2,row+" ");
        return index;
    }

    /**
     * this creates the info section of a schema card
     * @param player one of the participants
     */
    private String buildSchemaInfo(LightPlayer player) {
        if(player==null){throw new IllegalArgumentException();}

        String info=String.format(cliElems.getElem("username-id"),
                player.getUsername(),alignRight(uiMsg.getMessage("player-number") +
                        player.getPlayerId(),SCHEMA_WIDTH-player.getUsername().length()));

        return info;

    }

    /**
     * creates a list containing the details about the objectives fitting them in a defined length
     * @param pubObj the public objectives
     * @param privObj the private objective
     * @return a list of strings with a max length defined by OBJ_LENGTH
     */
    private List<String> buildObjectives(List<LightCard> pubObj, LightPrivObj privObj){
        List<String> result=new ArrayList<>();
        result.add(" ");
        result.add(bold(uiMsg.getMessage("pub-obj")));
        result.add(" ");
        for(LightCard card : pubObj){
            result.addAll(buildCard(card));
            result.add("     ");
        }
        result.add(bold(uiMsg.getMessage("priv-obj")));
        result.addAll(
                appendRows(
                        buildCell(new LightConstraint(privObj.getColor())),
                        buildPrivObj(privObj,OBJ_LENGTH-CELL_WIDTH)));


        return result;
    }

    private static String bold(String line){
        return "\u001B[1m"+line+Color.RESET;

    }
    private List<String> buildPrivObj(LightPrivObj privObj, int length) {
        List<String> result=new ArrayList<>();
        result.add(" ");
        result.add(bold(privObj.getName()+":"));
        result.addAll(fitInLength(privObj.getDescription(), length));
        return result;
    }

    private static List<String> colorWall(List<String> wall){
        Random randomGen = new Random();
        for(int row=0 ; row <wall.size();row++){
            wall.set(row,addColorToLine(wall.get(row),Color.values()[randomGen.nextInt(Color.values().length )]));
        }
        return wall;
    }
    /**
     * this builds a list of strings that represents the cell content
     * @param cellContent the
     * @return
     */
    private static List<String> buildCell(CellContent cellContent) {
        String[] rows=new String [4];

        if(cellContent==null){
            //empty
            rows = splitElem(cliElems.getBigDie("EMPTI"));
        }else {
            //not empty

            //die
            if (cellContent.isDie()) {
                rows = splitElem(cliElems.getBigDie(cellContent.getShade().toString()));
            }

            //constraint
            if (!cellContent.isDie()) {
                if (cellContent.hasColor()) {

                    //color constraint
                    rows = splitElem(cliElems.getBigDie("FILLED"));
                } else {

                    //shade constraint
                    rows = splitElem(cliElems.getBigDie(cellContent.getShade().toString()));
                }
            }

            //add color to colored things
            if (cellContent.hasColor()) {
                rows = addColor(rows, cellContent.getColor());
            }
        }
        return new ArrayList<>(Arrays.asList(rows));
    }

    /**
     * builds a single generic card(name and description)
     * @param card the card to be represented
     * @return a list that is the card's representation
     */
    private List<String> buildCard( LightCard card) {
        List<String> result=new ArrayList<>();

        result.add(bold(card.getName()+":"));
        result.addAll(fitInLength(card.getDescription(), OBJ_LENGTH));
        return result;
    }

    private List<String> buildTool(LightTool tool){
        List<String> result;
        result= buildCard(tool);
        result.add(String.format(cliElems.getElem("tokens-info"),
                uiMsg.getMessage("tokens"),
                tool.isUsed()?"2":"1"));
        return result;
    }

    /**
     * This method creates a list of string with a fixed maximum length that put together represent the original string
     * @param line the string to be split
     * @param length the desired maximum length
     * @return a list of strings that are parts of the original line
     */
    private static List<String> fitInLength(String line, int length ){
        if(line == null || length <= 0){throw new IllegalArgumentException();}
        List<String> result=new ArrayList<>();

        String lineToFit= line;
        while(lineToFit.length()>length){
            int i = length - 1;
            while(lineToFit.charAt(i) != ' '){
                i--;
            }
            result.add(lineToFit.substring(0,i));
            lineToFit=lineToFit.substring(i).trim();
        }
        if(lineToFit.trim().length()>=0){
            result.add(padUntil(lineToFit,length,' '));
        }
        return result;
    }

    /**
     * Creates a list containing characters needed to visually separate schemas on the screen
     * @return said list
     */
    private List<String> buildSeparator(int height){
        List<String> separator= new ArrayList<>();
        for(int i=0;i< height;i++){
            separator.add(" | ");
        }

        return separator;
    }

    /**
     * appends spaces to a string until a defined length is reached
     * @param toPad the string to be padded
     * @param finalLenght the total final length of the padded string
     * @return the padded string
     */
    private static String padUntil(String toPad, int finalLenght, char filler){
        if(finalLenght<0|| toPad==null||toPad.length()>finalLenght){throw new IllegalArgumentException();}

        return toPad+ new String(new char[finalLenght - toPad.length()]).replace("\0", filler+"");

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
    public static List<String> appendRows(List<String> a, List<String> b){
        if(a==null || b==null || (!a.isEmpty() && a.size()<b.size())){
            throw new IllegalArgumentException();
        }

        List<String> result= new ArrayList<>();
        if(a.isEmpty()){
            result.addAll(b);
        }else{
            assert(a.size()>=b.size());
            for(int row=0; row<a.size();row++){
                if(row<b.size()) {
                    result.add(row,a.get(row)+b.get(row));
                }else{
                    result.add(row,a.get(row));
                }
            }
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

    public void setMatchInfo(int playerId, int numPlayers) {
        this.playerId=playerId;
        this.numPlayers=numPlayers;
    }

}