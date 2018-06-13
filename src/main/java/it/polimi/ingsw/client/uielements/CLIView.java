package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.ClientFSMState;
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

    private String bottomInfo="";
    private String options="";
    private String turnRoundinfo="";
    private final HashMap<Integer,List<String>> schemas= new HashMap<>();
    private List<String> objectives= new ArrayList<>();
    private final List<String> tools = new ArrayList<>();
    private List<String> privObj= new ArrayList<>();
    private List<String> roundTrack = new ArrayList<>();
    private List<String> draftPool = new ArrayList<>();
    private List<String> menuList = new ArrayList<>();

    private static UIMessages uiMsg;
    private int numPlayers;
    private int playerId;
    private int turnNumber;
    private int nowPlaying;
    private String lastScreen="";

    public CLIView(UILanguage lang) {
        this.uiMsg=new UIMessages(lang);
        updateRoundTrack(new ArrayList<>());
    }



    public String showLoginUsername() {
        StringBuilder result= new StringBuilder();
        try {
            result.append(cliElems.getWall());
        } catch (IOException e) {

        }
        result.append(String.format(cliElems.getElem("login-line"),uiMsg.getMessage("login-username")));
        return result.toString();
    }

    public String showLoginPassword() {
        return String.format(cliElems.getElem("login-line"),uiMsg.getMessage("login-password"));
    }


    public void updateDraftedSchemas(List<LightSchemaCard> schemaCards){
        schemas.clear();
        for(int i=0; i<schemaCards.size();i++){
            List<String> schema=new ArrayList<>();

            schema.add(buildDraftedSchemaInfo(schemaCards.get(i),i ));
            //add top border
            schema.add(padUntil("",SCHEMA_WIDTH,'–'));
            schema.addAll(buildSchema(schemaCards.get(i),false) );
            schema.add(padUntil("",SCHEMA_WIDTH,'–'));
            schema= appendRows(
                            buildSeparator(SCHEMA_HEIGHT+1),
                            appendRows(schema,
                                    buildSeparator(SCHEMA_HEIGHT+1)));


            schemas.put(i,schema);
        }
    }

    private String buildDraftedSchemaInfo(LightSchemaCard schemaCard, int number) {
        String indexname= boldify("("+number +") "+schemaCard.getName());
        return indexname + alignRight(printFavorTokens(schemaCard.getFavorTokens()),SCHEMA_WIDTH-printableLength(indexname));
    }


    public String printLastScreen(){
        return resetScreenPosition()+lastScreen;
    }

    public void setLastScreen(String msg) {
        lastScreen=msg;
    }

    public String printMainView(ClientFSMState state){
        StringBuilder builder=new StringBuilder();
        builder.append(resetScreenPosition());
        builder.append(printList(buildRoundTrack())).append(" |%n");


        builder.append(printList(buildTopSection())).append(" |%n");


        builder.append(printList(buildDraftPool())).append(" |%n");

        builder.append(printList(buildBottomSection())).append("%n");

        builder.append(getPrompt(state));
        lastScreen=builder.toString();
        return lastScreen;
    }

    private String getPrompt(ClientFSMState state) {
        return String.format(cliElems.getElem("prompt"), buildOptions(state));
    }

    public String printSchemaChoiceView(){
        StringBuilder builder=new StringBuilder();
        List<String> priv = new ArrayList<>(privObj);
        List<String> drafted = new ArrayList<>(buildDraftedSchemas());
        priv.add(0,boldify(uiMsg.getMessage("priv-obj")));
        priv.add("  ");
        builder.append("%n%n%n%n");
        builder.append(printList(appendRows(appendRows(buildWall(' ',drafted.size(),SCHEMA_WIDTH+10),drafted),priv))).append("%n%n%n");
        builder.append(uiMsg.getMessage("choose-schema")).append("%n%n");
        builder.append(getPrompt(ClientFSMState.CHOOSE_SCHEMA));
        lastScreen=builder.toString();
        schemas.clear();
        return lastScreen;
    }

    public void updatePrivObj(LightPrivObj priv){
        this.privObj=buildPrivObj(priv,OBJ_LENGTH);
    }

    private List<String> buildDraftedSchemas() {
        List<String> result;
        result= appendRows(schemas.get(0),schemas.get(1));
        result.addAll(appendRows(schemas.get(2),schemas.get(3)));
        return result;
    }

    public void updateMenuDiceList(List<IndexedCellContent> dice,Place place){
        menuList.clear();
        menuList.add(String.format(uiMsg.getMessage("dice-list"),uiMsg.getMessage(place.toString().toLowerCase())));
        menuList.addAll(buildDiceList(dice,place));
        fillMenu();
    }


    private List<String> buildDiceList(List<IndexedCellContent> dice, Place place){
        List<String> list= new ArrayList<>();
        for(int i=0; i<dice.size();i++){
            list.add(String.format(cliElems.getElem("li"),i,buildDiceListEntry(dice.get(i),place)));
            list.set(i,list.get(i)+padUntil("",MENU_WIDTH-printableLength(list.get(i)),' ' ));
        }
        return list;
    }


    private String buildDiceListEntry(IndexedCellContent die, Place place){
        StringBuilder entry= new StringBuilder();
        entry.append(CLIViewUtils.buildSmallDie(die.getContent())).append(" ");
        if(place.equals(Place.SCHEMA)){
            entry.append(rowColmumn(die.getPosition()));
        } else if(place.equals(Place.ROUNDTRACK)){
            entry.append(String.format(cliElems.getElem("index"),uiMsg.getMessage("round-number"),die.getPosition()));
        }else{
            entry.append(String.format(cliElems.getElem("index"),uiMsg.getMessage("pos"),die.getPosition()));
        }
        return entry.toString();
    }


    /**
     * this method builds the bottom section of the main view by arranging side by side the schema of the user with the list of possible moves he can do
     * and the three tools of the game
     * @return a List of strings that represent that view
     */
    private List<String> buildBottomSection() {

        fillMenu();
        if(schemas.get(playerId).size()<menuList.size()){
            schemas.get(playerId).addAll(
                    buildWall(' ',
                            menuList.size()-schemas.get(playerId).size(),
                            schemas.get(playerId).get(0).length()));
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
        fillMenu();
    }

    private String buildOptions(ClientFSMState state) {
        StringBuilder defaultMenu= new StringBuilder();

        switch (state){
            case CHOOSE_PLACEMENT:
                defaultMenu.append(discardOption());
            case SELECT_DIE:

            case CHOOSE_OPTION:
                defaultMenu.append(backOption());
            case MAIN:
                defaultMenu.append(endTurnOption());
            case NOT_MY_TURN:
            case CHOOSE_SCHEMA:
                defaultMenu.append(quitOption());
                break;
            case TOOL_CAN_CONTINUE:
                break;
            default:
                break;
        }

        return defaultMenu.toString();
    }

    private String backOption(){ return uiMsg.getMessage("back-option")+" | ";}
    private String discardOption(){
        return uiMsg.getMessage("discard-option")+" | ";
    }

    private String endTurnOption(){
        return uiMsg.getMessage("endturn-option")+" | ";
    }

    private String quitOption(){
        return uiMsg.getMessage("quit-option");
    }
    /**
     * Creates a list of possible placements for a die
     * @param placements the list of placements
     * @param destination the place the possible placements refer to
     * @param die the die to be placed
     */
    public void updateMenuListPlacements(List<Integer> placements, Place destination, LightDie die){
        List<String> msg = new ArrayList<>(buildWall(' ', CELL_HEIGHT - 1, MENU_WIDTH - CELL_WIDTH));
        msg.add(boldify(padUntil(uiMsg.getMessage("can-be-placed"),MENU_WIDTH-CELL_WIDTH,' ')));
        menuList.clear();
        menuList.addAll(appendRows(buildCell(die),msg));
        menuList.add(padUntil("",MENU_WIDTH,' '));

        if(destination.equals(Place.SCHEMA)){
            menuList.addAll(padUntil(buildCoordinatesList(placements),MENU_WIDTH,' '));
        }else{
            menuList.addAll(buildIndexList(placements));
        }
        fillMenu();

    }

    private void fillMenu() {
        menuList.addAll(buildWall(' ',MENU_HEIGHT-menuList.size(),MENU_WIDTH));
    }


    public void updateMenuTurnInit(){
        menuList.clear();
        menuList.addAll(padUntil(buildTurnInitOptions(),MENU_WIDTH,' '));

        fillMenu();
    }

    private List<String> buildTurnInitOptions(){
        List<String> options= new ArrayList<>();
        options.add(String.format(cliElems.getElem("li"),0,uiMsg.getMessage("place-die")));
        options.add(String.format(cliElems.getElem("li"),0,uiMsg.getMessage("use-tool")));
        return options;
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
        this.tools.clear();
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
    private static String rowColmumn(int index){
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

         updateDraftPool(listToMap(draftPool));


    }

    /**
     * updates the draftpool representation
     * @param draftPool the new draftpool
     */
    public void updateDraftPool(Map<Integer,LightDie> draftPool){

        this.draftPool= buildDiceRow( draftPool,0,numPlayers*2+1);

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
        padding.add(boldify(String.format(cliElems.getElem("point-left"),uiMsg.getMessage("roundtrack"))));
        result= appendRows(result,padding);
        this.roundTrack=result;
    }

    private List<String> buildDraftPool(){

        List<String> result = new ArrayList<>(draftPool);
        if(result.size()>=CELL_HEIGHT) {
            result.set(CELL_HEIGHT - 1,
                    result.get(CELL_HEIGHT - 1) +
                            boldify(String.format(cliElems.getElem("point-left"), uiMsg.getMessage("draftpool"))));
            result.set(CELL_HEIGHT - 1, result.get(CELL_HEIGHT - 1) + alignRight(bottomInfo, SCREEN_WIDTH - printableLength(result.get(CELL_HEIGHT - 1))));
            result = appendRows(buildSeparator(draftPool.size()), result);
        }
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
        schem.add(padUntil("",width,'–'));

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
                mode.toString());

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
        info=info+(String.format(cliElems.getElem("tokens-info"),
                uiMsg.getMessage("tokens"),
                alignRight(
                        replicate(FAVOR,player.getFavorTokens()),
                        width-uiMsg.getMessage("tokens").length()-1)));
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
        result.add(boldify(uiMsg.getMessage("pub-obj")));
        result.add(" ");
        for(LightCard card : pubObj){
            result.addAll(buildCard(card,OBJ_LENGTH));
            result.add("     ");
        }

        result.add(boldify(uiMsg.getMessage("priv-obj")));
        result.addAll(buildPrivObj(privObj,OBJ_LENGTH));


        return result;
    }



    public void setMatchInfo(int playerId, int numPlayers) {
        this.playerId=playerId;
        this.numPlayers=numPlayers;
        updateDraftPool(new HashMap<>());
    }



}