package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.LightBoard;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.SchemaCard;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.uielements.CLIViewUtils.*;

public class CLIView {

    private String bottomInfo;
    private String options;
    private String turnRoundinfo;
    private final HashMap<Integer,List<String>> schemas= new HashMap<>();
    private List<String> objectives;
    private final List<String> tools= new ArrayList<>();
    private List<String> privObj;
    private List<String> roundTrack= new ArrayList<>();
    private List<String> draftPool= new ArrayList<>();
    private List<String> menuList =new ArrayList<>();

    private UIMessages uiMsg;
    private int numPlayers;
    private int playerId;
    private int turnNumber;
    private int nowPlaying;

    public CLIView(UILanguage lang) {
        this.uiMsg=new UIMessages(lang);
    }



    public String showLoginUsername() {
        StringBuilder result= new StringBuilder();
        result.append(resetScreenPosition());
        try {
            result.append(cliElems.getWall());
        } catch (IOException e) {

        }
        result.append(uiMsg.getMessage("connection-ok")).append("%n");

        result.append(String.format(cliElems.getElem("login-line"),uiMsg.getMessage("login-username")));
        return result.toString();
    }

    public String showLoginPassword() {
        return String.format(cliElems.getElem("login-line"),uiMsg.getMessage("login-password"));
    }


    public void updateDraftedSchemas(List<LightSchemaCard> schemaCards){
        for(int i=0; i<schemaCards.size();i++){
            List<String> schema=new ArrayList<>();

            schema.add(buildDraftedSchemaInfo(schemaCards.get(i)));
            schema.addAll(buildSchema(schemaCards.get(i),false));
            schemas.put(i,schema);
        }
    }

    private String buildDraftedSchemaInfo(LightSchemaCard schemaCard) {
        return schemaCard.getName()+alignRight(printFavorTokens(schemaCard.getFavorTokens()),SCHEMA_WIDTH);
    }



    public String printMainView(){
        StringBuilder builder=new StringBuilder();
        builder.append(resetScreenPosition());
        builder.append(printList(buildRoundTrack())).append(" |%n");


        builder.append(printList(buildTopSection())).append(" |%n");


        builder.append(printList(buildDraftPool())).append(" |%n");

        builder.append(printList(buildBottomSection())).append("%n");

        builder.append(String.format(cliElems.getElem("prompt"), buildOptions()));

        return builder.toString();
    }

    public String printSchemaChoiceView(){
        StringBuilder builder=new StringBuilder();
        builder.append(resetScreenPosition());
        builder.append(printList(appendRows(buildDraftedSchemas(),privObj))).append("%n%n%n");
        return builder.toString();
    }

    public void updatePrivObj(LightPrivObj priv){
        this.privObj=buildPrivObj(priv,OBJ_LENGTH);
    }

    private List<String> buildDraftedSchemas() {
        List<String> result= new ArrayList<>(SCHEMA_HEIGHT+2);
        for(Map.Entry<Integer,List<String>> entry: schemas.entrySet()){
            result=appendRows(result,entry.getValue());
        }
        return result;
    }



    /**
     * this method builds the bottom section of the main view by arranging side by side the schema of the user with the list of possible moves he can do
     * and the three tools of the game
     * @return a List of strings that represent that view
     */
    private List<String> buildBottomSection() {

        if(menuList.size()>schemas.get(playerId).size() ) {
            schemas.get(playerId).addAll(buildWall(' ', menuList.size() - schemas.get(playerId).size(), SCHEMA_WIDTH + 8));
        }

        List<String> result=appendRows(schemas.get(playerId),menuList);

        result= appendRows(result,buildSeparator(result.size()));
        result= appendRows(result,tools);

        return result;
    }



    /**
     * this builds the top section by arranging the rivals' schema in a row, and appending pieces of information about the private and public objectives
     * @return a list of strings that represent that
     */
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

    public void updateMenuListDefault() {
        menuList.clear();
    }

    private String buildOptions() {
        StringBuilder defaultMenu= new StringBuilder();

            defaultMenu.append(
                            (nowPlaying==playerId?
                                    (uiMsg.getMessage("discard-option")+"|"):
                                    "") // TODO: 03/06/2018 add pass turn option
                                    + uiMsg.getMessage("quit-option"));


        return defaultMenu.toString();
    }

    /**
     * Creates a list of possible placements for a die
     * @param placements the list of placements
     * @param destination the place the possible placements refer to
     * @param die the die to be placed
     */
    public void updateMenuList(List<Integer> placements, Place destination, LightDie die){
        List<String> msg = new ArrayList<>(buildWall(' ', CELL_HEIGHT - 1, MENU_WIDTH - CELL_WIDTH));
        msg.add(boldify(padUntil(uiMsg.getMessage("can-be-placed"),MENU_WIDTH-CELL_WIDTH,' ')));

        menuList.addAll(appendRows(buildCell(die),msg));
        menuList.add(padUntil("",MENU_WIDTH,' '));

        if(destination.equals(Place.SCHEMA)){
            menuList.addAll(padUntil(buildCoordinatesList(placements),MENU_WIDTH,' '));
        }else{
            menuList.addAll(buildIndexList(placements));
        }
        menuList.addAll(buildWall(' ',MENU_HEIGHT-menuList.size(),MENU_WIDTH));

    }



    /**
     * this method builds a list of indexes for the menu, those indexes represents possible placements or dice from which to choose
     * @param indexes the list of possible choices
     * @return the list of string containing the possible entries
     */
    private List<String> buildIndexList(List<Integer> indexes) {
        List<String> list= new ArrayList<>();
        for(int i=0; i<indexes.size();i++){
            list.add(String.format(cliElems.getElem("li"), i, index(indexes.get(i))));
        }
        return list;

    }

    /**
     * this method returns a string that is a formatted version of the index passed to it
     * @param index the index to be represented
     * @return the string containing the index
     */
    private String index(Integer index) {
        return String.format(cliElems.getElem("index"),uiMsg.getMessage("pos"),index);

    }

    /**
     * this method builds a list of the coordinates (row, column) in a schema in which we can place a some selected die
     * @param indexes the indexes (0-19) that point to a cell in the schema
     * @return a list of the coordinates
     */
    private List<String> buildCoordinatesList(List<Integer> indexes) {
        List<String> list= new ArrayList<>();
        for(int i=0; i<indexes.size();i++){
            list.add(String.format(cliElems.getElem("li"), i, rowColmumn(indexes.get(i))));
        }
        return list;
    }


    /**
     * creates the representation of the player's schema and puts it into the map
     * @param player the player whose schema we want to create/update
     */
    public void updateSchema(LightPlayer player){
        this.schemas.put(player.getPlayerId(), buildPlayerSchema(player));
        if(player.getPlayerId()==playerId) {
            this.schemas.get(playerId).set(0, boldify(schemas.get(playerId).get(0)));
        }
    }

    public void updateNewRound(int numRound) {
        menuList.clear();

    }

    /**
     * updates the information abuot the round, turn and who's playing this turn
     * @param roundNumber the number of the round
     * @param nowPlaying the user playing the turn
     */
    public void updateRoundTurn(int roundNumber,int nowPlaying){
        this.nowPlaying=nowPlaying;
        turnRoundinfo= String.format(cliElems.getElem("round-turn"),
                uiMsg.getMessage("round"),
                roundNumber,
                uiMsg.getMessage("turn"),
                nowPlaying);
        List<String> updateSchema;


        updateSchema=schemas.get(nowPlaying);
        Random randomGen = new Random();
        updateSchema.set(0,addColorToLine(boldify(updateSchema.get(0)),Color.values()[randomGen.nextInt(Color.values().length)]));
    }

    /**
     * Updates the tools following a change in the used state of them
     * @param tools the list of the match tools
     */
    public void updateTools(List<LightTool> tools){
        for(int i=0;i < Board.NUM_TOOLS;i++){
            this.tools.add(padUntil("",OBJ_LENGTH,' '));
            this.tools.add(String.format(cliElems.getElem("tool-index"),uiMsg.getMessage("tool-number"),i));
            this.tools.addAll(buildTool(tools.get(i)));
        }
    }


    /**
     * this method calculates and builds a string containing the row and column corresponding to an index in a schema card
     * @param index the index (0-19) of a cell in the schema
     * @return a string containing the coordinates
     */
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
        List<String> result = new ArrayList<>(this.roundTrack);

        result.add(padUntil("", SCREEN_WIDTH,'–'));
        result= appendRows(buildSeparator(result.size()),result);

        return result;
    }



    public void updateRoundTrack(List<List<LightDie>> roundTrack){
        List<String> result=new ArrayList<>();
        int maxLength=0;

        //calculating max number of leftover dice in a single round
        for(List<LightDie> round:roundTrack){
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
        padding.add(boldify(uiMsg.getMessage("roundtrack")));
        result= appendRows(result,padding);
        this.roundTrack=result;
    }

    private List<String> buildDraftPool(){

        List<String> result = new ArrayList<>(draftPool);
        result.set(CELL_HEIGHT-1,
                result.get(CELL_HEIGHT-1)+
                        boldify(uiMsg.getMessage("draftpool")+
                                alignRight(bottomInfo,
                                        SCREEN_WIDTH  - (2*numPlayers+1)*CELL_WIDTH - uiMsg.getMessage("draftpool").length())));
        result= appendRows(buildSeparator(draftPool.size()),result);
        return result;
    }






    /**
     * builds a list containing strings that represent the schema of a player
     * @return the representation of the schema
     */
    private  List<String> buildPlayerSchema(LightPlayer player){
        int width=SCHEMA_WIDTH + (playerId==player.getPlayerId()?2:0);

        //add top info
        List<String> schem = new ArrayList<>();


        //add top info
        schem.addAll(0,fitInLength(buildSchemaInfo(player,width), width));
        //add top border
        schem.add(fitInLength(buildSchemaInfo(player,width), width).size(),padUntil("",width,'–'));

        schem.addAll(buildSchema(player.getSchema(),player.getPlayerId()==playerId));


        int height= SCHEMA_HEIGHT+ (playerId==player.getPlayerId()?2:1);
        //add bottom border
        schem.add(height,padUntil("",width,'–'));
        //add left/right borders
        schem = appendRows(
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

        bottomInfo = String.format(cliElems.getElem("player-info"),
                username,
                uiMsg.getMessage("connected-via"),
                mode.toString(),
                uiMsg.getMessage("player-number"),
                playerId);

    }



    /**
     * this creates the info section of a schema card
     * @param player one of the participants
     */
    private String buildSchemaInfo(LightPlayer player,int width) {
        if(player==null){throw new IllegalArgumentException();}

        String info=String.format(cliElems.getElem("username-id"),
                player.getUsername(),alignRight(uiMsg.getMessage("player-number") +
                        player.getPlayerId(),width - player.getUsername().length()));
        return info+(String.format(cliElems.getElem("tokens-info"),uiMsg.getMessage("tokens"),replicate(FAVOR,player.getFavorTokens())));

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
        result.add(boldify(uiMsg.getMessage("pub-obj")));
        result.add(" ");
        for(LightCard card : pubObj){
            result.addAll(buildCard(card));
            result.add("     ");
        }
        result.add(boldify(uiMsg.getMessage("priv-obj")));
        result.addAll(
                appendRows(
                        buildCell(new LightConstraint(privObj.getColor())),
                        buildPrivObj(privObj,OBJ_LENGTH-CELL_WIDTH)));


        return result;
    }



    public void setMatchInfo(int playerId, int numPlayers) {
        this.playerId=playerId;
        this.numPlayers=numPlayers;
    }



}