package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.*;

import static it.polimi.ingsw.client.view.clientUI.uielements.CLIViewUtils.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.CLIElems.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg.*;

/**
 * this class contains the objects used by the cli to show the board components to the user
 */
public class CLIView {

    private static final int MAX_MENULIST_COLUMNS = 4;

    static final String DOUBLE_COLON ="::" ;
    static final char SPACE=' ';
    static final char DASH='–';
    static final String EMPTY_STRING ="" ;
    static final String NEW_LINE ="%n" ;
    static final String SEPARATOR ="|" ;

    private String bottomInfo=EMPTY_STRING;
    private String turnRoundinfo=EMPTY_STRING;
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
    private String latestScreen =EMPTY_STRING;

    /**
     * this sets the language for the cli and resets some objects
     * @param lang the language
     */
    public CLIView(UILanguage lang) {
        uiMsg=new UIMessages(lang);
        updateRoundTrack(new ArrayList<>());
    }

    /**
     * @return  the line to be printed as a prompt for the username
     */
    public String showLoginUsername() {
        StringBuilder result= new StringBuilder();

        result.append(String.format(cliElements.getElem(LOGIN_LINE),uiMsg.getMessage(LOGIN_USERNAME)));
        return result.toString();
    }

    /**
     * @return  the line to be printed as a prompt for the password
     */
    public String showLoginPassword() {
        return String.format(cliElements.getElem(LOGIN_LINE),uiMsg.getMessage(LOGIN_PASSWORD));
    }


    /**
     * this method sets the schemas that were drafted and their representation
     * @param schemaCards the drafted schemas
     */
    public void updateDraftedSchemas(List<LightSchemaCard> schemaCards){
        schemas.clear();
        for(int i=0; i<schemaCards.size();i++){
            List<String> schema=new ArrayList<>();

            schema.add(buildDraftedSchemaInfo(schemaCards.get(i),i ));
            //add top border
            schema.add(padUntil(EMPTY_STRING,SCHEMA_WIDTH,DASH));
            schema.addAll(buildSchema(schemaCards.get(i),false) );
            schema.add(padUntil(EMPTY_STRING,SCHEMA_WIDTH,DASH));
            schema= appendRows(
                            buildSeparator(SCHEMA_HEIGHT+1),
                            appendRows(schema,
                                    buildSeparator(SCHEMA_HEIGHT+1)));


            schemas.put(i,schema);
        }
    }


    public void updatePrivObj(LightPrivObj priv){
        this.privObj=buildPrivObj(priv,OBJ_LENGTH);
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

        updateDraftPool(toMap(draftPool));


    }

    /**
     * updates the draftpool representation
     * @param draftPool the new draftpool
     */
    public void updateDraftPool(Map<Integer,LightDie> draftPool){

        this.draftPool= buildDiceRow( draftPool,0,numPlayers*2+1);
        this.draftPool.add(padUntil(EMPTY_STRING,SCREEN_WIDTH,DASH));
    }


    /**
     * this updates the roundtrack's representation
     * @param roundTrack the updated roundtrack
     */

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
                    builtRow= appendRows(builtRow,buildWall(CELL_HEIGHT, CELL_WIDTH, SPACE));
                }
            }
            result.addAll(builtRow);
        }

        Map<Integer,CellContent> baseRow= new HashMap<>();

        for(int round=0; round<roundTrack.size(); round++){
            baseRow.put(round, roundTrack.get(round).get(0));
        }

        result.addAll(buildCellRow(baseRow,0, Board.NUM_ROUNDS));
        List<String> padding=buildWall(result.size()-1, 1, SPACE);
        padding.add(boldify(String.format(cliElements.getElem(POINT_LEFT),uiMsg.getMessage(ROUNDTRACK))));
        result= appendRows(result,padding);
        this.roundTrack=result;
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


    /**
     * updates the information abuot the round, turn and who's playing this turn
     * @param roundNumber the number of the round
     * @param isFirstTurn wheter it is or not the first turn in the round
     * @param nowPlaying the user playing the turn
     */
    public void updateRoundTurn(int roundNumber, boolean isFirstTurn, int nowPlaying){
        turnRoundinfo= String.format(cliElements.getElem(ROUND_TURN),
                uiMsg.getMessage(ROUND),
                roundNumber,
                isFirstTurn?
                        uiMsg.getMessage(FIRST_TURN):
                        uiMsg.getMessage(SECOND_TURN)
        );
        List<String> updateSchema;


        updateSchema=schemas.get(nowPlaying);
        Random randomGen = new Random();
        updateSchema.set(0,addColorToLine(boldify(updateSchema.get(0)),Color.values()[randomGen.nextInt(Color.values().length - 1)]));
    }

    /**
     * Updates the tools following a change in the used state of them
     * @param tools the list of the match tools
     */
    public void updateTools(List<LightTool> tools){
        this.tools.clear();
        this.tools.add(uiMsg.getMessage(TOOLS));
        for(int i=0;i<LightBoard.NUM_TOOLS;i++) {

            this.tools.addAll(buildTool(tools.get(i), i));
            this.tools.add(EMPTY_STRING);
        }
        this.tools.remove(this.tools.size()-1);

    }


    /**
     * this method builds the lines that contain the info about the schemas that were drafted
     * @param schemaCard the schema
     * @param number the index (0-3) of the schema among the drafted ones
     * @return the info
     */
    private String buildDraftedSchemaInfo(LightSchemaCard schemaCard, int number) {
        String indexname= boldify(String.format(cliElements.getElem(DRAFTED_INFO),number ,schemaCard.getName()));
        return indexname + alignRight(printFavorTokens(schemaCard.getFavorTokens()),SCHEMA_WIDTH-printableLength(indexname));
    }


    /**
     * @return the string containing the la
     */
    public String printLatestScreen(){
        return resetScreenPosition()+ latestScreen;
    }

    /**
     * this allow to set a screen to be printed when the method printLatestScreen is called (note that this may be overwritten by other methods)
     * @param msg the message to be set
     */
    public void setLatestScreen(String msg) {
        latestScreen =msg;
    }

    /**
     * this builds the main game view according to the state of the players
     * @param state the state of the player
     * @return the string containing the whole screen
     */
    public String printMainView(ClientFSMState state){
        StringBuilder builder=new StringBuilder();
        builder.append(resetScreenPosition());
        builder.append(printList(buildRoundTrack())).append(SPACE+SEPARATOR+NEW_LINE);


        builder.append(printList(buildTopSection()));


        builder.append(printList(buildDraftPool())).append(SPACE+SEPARATOR+NEW_LINE);

        builder.append(printList(buildBottomSection())).append(NEW_LINE);

        builder.append(getPrompt(state));
        latestScreen =builder.toString();
        return latestScreen;
    }

    /**
     * this builds the prompt according to the state of the client
     * @param state the client's state
     * @return the prompt line
     */
    private String getPrompt(ClientFSMState state) {
        return String.format(cliElements.getElem(PROMPT), buildPromptOptions(state));
    }

    /**
     * this completes the building of the schema's choice screen
     * @return the string that represents this
     */
    public String printSchemaChoiceView(){
        StringBuilder builder=new StringBuilder();
        List<String> priv = new ArrayList<>(privObj);
        List<String> drafted = new ArrayList<>(buildDraftedSchemas());
        priv.add(0,boldify(uiMsg.getMessage(PRIVATE_OBJ)));
        priv.add(EMPTY_STRING);
        builder.append(NEW_LINE+NEW_LINE+NEW_LINE+NEW_LINE);
        builder.append(printList(appendRows(appendRows(buildWall(drafted.size(), SCHEMA_WIDTH+10, SPACE),drafted),priv))).append(NEW_LINE+NEW_LINE+NEW_LINE);
        builder.append(uiMsg.getMessage(CHOOSE_SCHEMA)).append(NEW_LINE+NEW_LINE);
        builder.append(getPrompt(ClientFSMState.CHOOSE_SCHEMA));
        latestScreen =builder.toString();
        schemas.clear();
        return latestScreen;
    }


    /**
     * this builds the matrix of four schemas for the initial choice
     * @return the list of strings that represent it
     */
    private List<String> buildDraftedSchemas() {
        List<String> result;
        result= appendRows(schemas.get(0),schemas.get(1));
        result.addAll(appendRows(schemas.get(2),schemas.get(3)));
        return result;
    }

    /**
     * this sets the menu to display inf about whose turn it is
     * @param nowPlaying the name of the player
     */
    public void updateMenuNotMyTurn(String nowPlaying) {
        clearMenu();
        menuList.add(EMPTY_STRING);
        menuList.add(String.format(uiMsg.getMessage(NOT_MY_TURN),nowPlaying));
        padMenu();
        fillMenu();
    }

    /**
     * this shows the list of names of the tools to allow for one to be chosen
     * @param tools the tools to be displayed
     */
    public void updateMenuListTools(List<LightTool> tools){
        clearMenu();
        menuList.add(uiMsg.getMessage(CHOOSE_TOOL));
        for(int i=0; i<LightBoard.NUM_TOOLS;i++) {
            menuList.add(String.format(cliElements.getElem(LIST_ELEMENT),i, tools.get(i).getName()));
        }
        padMenu();
        fillMenu();
    }


    /**
     * this sets the menu so it displays the main choice options (tool/placement)
     */
    public void updateMenuMain(){
        clearMenu();
        menuList.add(EMPTY_STRING);
        menuList.add(uiMsg.getMessage(MAIN_CHOICE));
        menuList.add(EMPTY_STRING);
        menuList.add(String.format(cliElements.getElem(LIST_ELEMENT),0,uiMsg.getMessage(USE_TOOL)));
        if(!ClientFSMState.isPlacedDie())menuList.add(String.format(cliElements.getElem(LIST_ELEMENT),1,uiMsg.getMessage(PLACE_DIE)));

        padMenu();
        fillMenu();
    }

    /**
     * this method sets the menu to display info about a dice list
     * @param dice the list of dice
     */
    public void updateMenuDiceList(List<IndexedCellContent> dice){
        clearMenu();
        if(dice.size()>1 && !dice.get(0).getPlace().equals(dice.get(1).getPlace())){
            menuList.addAll(buildCell(dice.get(0).getContent()));
            dice.remove(0);
            String desc=String.format(uiMsg.getMessage(CHOOSE_FROM_DICE_LIST), uiMsg.getMessage(UIMsg.valueOf(dice.get(0).getPlace().toString().toUpperCase())));
            menuList.set(CELL_HEIGHT-1,
                    menuList.get(CELL_HEIGHT-1)+SPACE+ desc);
            menuList.add(EMPTY_STRING);
        }else {
            menuList.add(EMPTY_STRING);
            menuList.add(String.format(uiMsg.getMessage(CHOOSE_FROM_DICE_LIST), uiMsg.getMessage(UIMsg.valueOf(dice.get(0).getPlace().toString().toUpperCase()))));
            menuList.add(EMPTY_STRING);
        }
        menuList.addAll(buildDiceList(dice));

        padMenu();
        fillMenu();
    }

    /**
     * Creates a list of possible placements for a die
     * @param placements the list of placements
     * @param die the die to be placed
     */
    public void updateMenuListPlacements(List<Integer> placements, CellContent die){

        List<String> msg = new ArrayList<>(buildWall(CELL_HEIGHT - 1, 1, SPACE));
        msg.add(boldify(uiMsg.getMessage(CAN_BE_PLACED)));

        clearMenu();
        menuList.addAll(appendRows(buildCell(die),msg));
        menuList.add(EMPTY_STRING);
        menuList.addAll(buildCoordinatesList(placements));

        padMenu();
        fillMenu();
    }

    private void clearMenu() {
        menuList.clear();
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
        result= appendRows(result,buildWall(result.size(), (LightBoard.MAX_PLAYERS - numPlayers)*(SCHEMA_WIDTH+6), SPACE));
        result= appendRows(result,buildSeparator(SCHEMA_HEIGHT+1));
        result= appendRows(result,objectives);
        return result;
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
                    buildWall(menuList.size()-schemas.get(playerId).size(), schemas.get(playerId).get(0).length(), SPACE
                    ));
        }

        List<String> result=appendRows(schemas.get(playerId),menuList);


        if(tools.size()>result.size()){
            result.addAll(buildWall(tools.size()-result.size(), printableLength(result.get(0)), SPACE));
        }
        result= appendRows(result,buildSeparator(result.size()));
        result= appendRows(result,tools);

        return result;
    }

    /**
     * this builds the prompt line according to the state of the match/turn
     * @param state the turn/match state
     * @return the prompt line
     */
    private String buildPromptOptions(ClientFSMState state) {
        StringBuilder promptLine= new StringBuilder();

        switch (state){
            case CHOOSE_PLACEMENT:
                promptLine.append(discardOption());
                promptLine.append(backOption());
                promptLine.append(endTurnOption());
                promptLine.append(quitOption());
                break;

            case CHOOSE_TOOL:
            case SELECT_DIE:
            case CHOOSE_OPTION:
                promptLine.append(backOption());
                promptLine.append(endTurnOption());
                promptLine.append(quitOption());
                break;

            case MAIN:
                promptLine.append(endTurnOption());
                promptLine.append(quitOption());
                break;



            case NOT_MY_TURN:
            case CHOOSE_SCHEMA:
            case TOOL_CAN_CONTINUE:
                promptLine.append(quitOption());
                break;

            default:
                break;
        }
        return promptLine.toString();
    }

    /**
     * @return a string that contains a message regarding the back option
     */
    private String backOption(){ return uiMsg.getMessage(BACK_OPTION)+SPACE+SEPARATOR+SPACE;}

    /**
     * @return a string that contains a message regarding the discard option
     */
    private String discardOption(){ return uiMsg.getMessage(DISCARD_OPTION)+SPACE+SEPARATOR+SPACE; }

    /**
     * @return a string that contains a message regarding the end-turn option
     */
    private String endTurnOption(){ return uiMsg.getMessage(END_TURN_OPTION)+SPACE+SEPARATOR+SPACE; }

    /**
     * @return a string that contains a message regarding the quit option
     */
    private String quitOption(){ return uiMsg.getMessage(QUIT_OPTION); }



    /**
     * fills the remaining lines of the menu area with spaces
     */
    private void fillMenu() {
        menuList.addAll(buildWall(MENU_HEIGHT-menuList.size(), MENU_WIDTH, SPACE));
    }

    /**
     * pads the lines of the menu so that they all are of the same length (MENU_WIDTH)
     */
    private void padMenu(){

        menuList=padUntil(menuList,MENU_WIDTH,SPACE);

    }


    /**
     * This method sets the line that will be at the top of the interface and will contain generic info about the user
     * @param mode the type of connection the user is using
     * @param username the username
     */
    public void setClientInfo(ConnectionMode mode, String username){
        if(mode==null||username==null){ throw new IllegalArgumentException();}

        bottomInfo = String.format(cliElements.getElem(PLAYER_INFO),
                username,
                uiMsg.getMessage(CONNECTED_VIA),
                mode);

    }

    /**
     * this method sets the info about the match regarding the player
     * @param playerId the user's playerId
     * @param numPlayers the number of players of the match
     */

    public void setMatchInfo(int playerId, int numPlayers) {
        this.playerId=playerId;
        this.numPlayers=numPlayers;
        updateDraftPool(new HashMap<>());
    }

    /**
     * builds a list of dice
     * @param dice the dice list
     * @return the representation of the list
     */
    private List<String> buildDiceList(List<IndexedCellContent> dice){
        int numAddedDice=0;
        List<String> list = new ArrayList<>();
        for(int i=0;i<Math.min(dice.size(),SCHEMA_HEIGHT );i++){
            list.add(EMPTY_STRING);
        }
        for(int column=0;column<MAX_MENULIST_COLUMNS && numAddedDice<dice.size();column++) {
            for (int i = 0; i <  Math.min(dice.size(), (SCHEMA_HEIGHT)) && numAddedDice<dice.size(); i++) {
                list.set(i,list.get(i) +String.format(cliElements.getElem(LIST_ELEMENT), numAddedDice, buildDiceListEntry(dice.get(numAddedDice))));
                numAddedDice++;
            }
        }

        return list;
    }


    /**
     * this method builds a single entry for a dice list
     * @param die the die of the entry
     * @return the built entry
     */
    private String buildDiceListEntry(IndexedCellContent die){
        StringBuilder entry= new StringBuilder();
        entry.append(CLIViewUtils.buildSmallDie(die.getContent())).append(SPACE);
        if(die.getPlace().equals(Place.SCHEMA)){
            entry.append(rowColmumn(die.getPosition()));
        } else if(die.getPlace().equals(Place.ROUNDTRACK)){
            entry.append(String.format(cliElements.getElem(INDEX),uiMsg.getMessage(ROUND_NUMBER),die.getPosition()));
        }else{
            entry.append(String.format(cliElements.getElem(INDEX),uiMsg.getMessage(POS),die.getPosition()));
        }
        return entry.toString();
    }

    /**
     * this method builds a list of the coordinates (row, column) in a schema in which we can place a some selected die
     * @param indexes the indexes (0-19) that point to a cell in the schema
     * @return a list of the coordinates
     */
    private List<String> buildCoordinatesList(List<Integer> indexes) {
        List<String> list= new ArrayList<>();
        for(int i=0;i<Math.min(indexes.size(),SCHEMA_HEIGHT - 1);i++){
            list.add(EMPTY_STRING);
        }
        int numAddedIndexes=0;
        for(int column=0;column<MAX_MENULIST_COLUMNS && numAddedIndexes<indexes.size();column++) {
            for (int i = 0; i <  Math.min(indexes.size(), (SCHEMA_HEIGHT - 2)) && numAddedIndexes<indexes.size(); i++) {
                list.set(i,list.get(i) + String.format(cliElements.getElem(LIST_ELEMENT), numAddedIndexes, rowColmumn(indexes.get(numAddedIndexes))));
                numAddedIndexes++;
            }
        }

        return list;
    }

    /**
     * this method calculates and builds a string containing the row and column corresponding to an index in a schema card
     * @param index the index (0-19) of a cell in the schema
     * @return a string containing the coordinates
     */
    private static String rowColmumn(int index){
        int row= index/SchemaCard.NUM_COLS;
        int column= index%SchemaCard.NUM_COLS;
        return String.format(cliElements.getElem(ROW_COL),uiMsg.getMessage(ROW),row,uiMsg.getMessage(COL),column);
    }


    /**
     * returns the roundtrack with a separation line underneath it
     * @return the roundtrack with a line underneath
     */
    private List<String > buildRoundTrack(){
        List<String> result = new ArrayList<>(this.roundTrack);

        result.add(padUntil(EMPTY_STRING, SCREEN_WIDTH,DASH));
        result= appendRows(buildSeparator(result.size()),result);

        return result;
    }


    /**
     * this builds the lines that contain the draftpool and info about the player
     * @return said lines
     */
    private List<String> buildDraftPool(){

        List<String> result = new ArrayList<>(draftPool);
        if(result.size()>=CELL_HEIGHT) {
            result.set(CELL_HEIGHT - 1,
                    result.get(CELL_HEIGHT - 1) +
                            boldify(String.format(cliElements.getElem(POINT_LEFT), uiMsg.getMessage(DRAFTPOOL)))+replicate(SPACE+EMPTY_STRING,9)+ boldify(turnRoundinfo));
            result.set(CELL_HEIGHT - 1, result.get(CELL_HEIGHT - 1)  + alignRight(boldify(bottomInfo), SCREEN_WIDTH - printableLength(result.get(CELL_HEIGHT - 1))));
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
        schem.add(padUntil(EMPTY_STRING,width,DASH));

        schem.addAll(buildSchema(player.getSchema(),player.getPlayerId()==playerId));


        int height = SCHEMA_HEIGHT + (playerId==player.getPlayerId()?2:1);
        //add bottom border
        schem.add(height,padUntil(EMPTY_STRING,width,DASH));
        //add left/right borders
        schem = appendRows(
                buildSeparator(height+1)
                ,schem);
        schem= appendRows(schem,buildSeparator(height+1));

        return player.isPlaying()?schem:greyLines(schem);
    }


    /**
     * this creates the info section of a schema card
     * @param player one of the participants
     */
    private String buildSchemaInfo(LightPlayer player,int width) {
        if(player==null){throw new IllegalArgumentException();}

        String info=String.format(cliElements.getElem(USERNAME_ID),
                player.getUsername(),alignRight(uiMsg.getMessage(PLAYER_NUMBER) +
                        player.getPlayerId(),width - player.getUsername().length()));
        if(player.getStatus().equals(LightPlayerStatus.PLAYING)) {
            info = info + (String.format(cliElements.getElem(TOKENS_INFO),
                    uiMsg.getMessage(REMAINING_TOKENS),
                    alignRight(
                            replicate(FAVOR, player.getFavorTokens()),
                            width - uiMsg.getMessage(REMAINING_TOKENS).length() - 1)));
        }else{
            info = greyLine(info + padUntil(uiMsg.getMessage(UIMsg.valueOf(player.getStatus().toString())),width,SPACE));
        }
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
        result.add(boldify(uiMsg.getMessage(PUBLIC_OBJ)));
        result.add(EMPTY_STRING);
        for(LightCard card : pubObj){
            result.addAll(buildCard(card,OBJ_LENGTH));
            result.add(EMPTY_STRING);
        }

        result.add(boldify(uiMsg.getMessage(PRIVATE_OBJ)));
        result.addAll(buildPrivObj(privObj,OBJ_LENGTH));


        return result;
    }



}