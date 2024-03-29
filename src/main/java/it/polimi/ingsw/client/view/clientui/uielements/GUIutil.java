package it.polimi.ingsw.client.view.clientui.uielements;

import it.polimi.ingsw.client.controller.ClientFSM;
import it.polimi.ingsw.client.controller.ClientFSMState;
import it.polimi.ingsw.client.controller.CmdWriter;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientui.uielements.enums.UIMsg;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.serializables.*;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.controller.ClientFSMState.MAIN;
import static it.polimi.ingsw.client.controller.ClientFSMState.SELECT_DIE;
import static it.polimi.ingsw.client.view.clientui.uielements.CustomGuiEvent.*;
import static it.polimi.ingsw.client.view.clientui.uielements.enums.UIMsg.*;
import static javafx.geometry.Pos.*;

/**
 * implements the methods and stores the needed information for the gui rendering
 */
public class GUIutil {
    private final CmdWriter cmdWrite;
    private final UIMessages uimsg;
    private double screenWidth;
    private double screenHeight;

    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private static final int ROUNDTRACK_SIZE = 10;
    private static final String FONT = "Sans-Serif";
    private static final String IMG_WALL_PNG = "-fx-background-image: url('img/wall.png');";
    private final Object lockWrite= new Object();
    private static final String END_TURN = "end turn";
    private static final String BACK = "back";

    //-----login Stage
    private static final double LOGIN_TO_SCREEN_RATIO = 0.25;
    private static final double LOGIN_RATIO = 0.663;
    //-----Lobby
    private static final double LOBBY_SCENE_RATIO = 1.47482;
    private static final double LOBBY_SCENE_W_TO_SCREEN_RATIO = 0.6;

    //-----Drafted Schema Stage
    private static final double SCHEMA_W_TO_CELL = 5.19481;
    private static final double SCHEMA_ARC_TO_WIDTH = 0.0666;
    private static final double TEXT_HEIGHT_TO_SCHEMA_H = 0.90;
    private static final double TEXT_DIM_TO_SCHEMA_W = 0.0505;
    private static final double FAVOR_DIAM_TO_SCHEMA_W = 0.038;
    private static final double FAVOR_POS_TO_SCHEMA_W = 0.92;

    private static final double DRAFTED_SCHEMAS_TEXT_TO_CELL = 0.7;
    private static final double SCHEMA_LABEL_TO_CELL_DIM = 0.34632;
    private static final double PRIVOBJ_W_TO_CELL_DIM = 3.7518;
    private static final double PRIVATE_OBJ_RATIO = 0.7386;
    private static final double SCHEMA_H_TO_CELL = 4.6176;
    private static final double DRAFTED_SCHEMAS_CELL_DIM_TO_SCENE_WIDTH = 0.05488;
    private static final double DRAFTED_SCHEMAS_CELL_DIM_TO_SCENE_HEIGHT = 0.0809;
    private static final double DRAFTED_SCHEMAS_SPACING_TO_CELL = 0.34;
    //Main game scene
    private static final double MAIN_SCENE_WIDTH_TO_SCREEN_WIDTH = 0.8265;
    private static final double MAIN_GAME_CELL_DIM_TO_HEIGHT = 0.107;
    private static final double MAIN_GAME_CELL_DIM_TO_WIDTH = 0.066;
    private static final double MAIN_GAME_SCENE_RATIO = MAIN_GAME_CELL_DIM_TO_HEIGHT/MAIN_GAME_CELL_DIM_TO_WIDTH;
    private static final double CARD_WIDTH_TO_CELL_DIM = 2.65;
    private static final double CARD_HEIGHT_TO_CELL_DIM = 3.6;
    private static final double ROUNDTRACK_SPACING = 5;
    private static final double FAVOR_TO_TOOL_W = 0.13;
    //Game End
    private static final double GAME_END_TEXT_TO_CELL =0.625 ;
    //Connection Broken
    private static final double CONN_BROKEN_FONT_TO_SCREEN = 0.01;

    //css Ids
    private static final String LOBBY_MESSAGE = "lobby-message";
    private static final String DRAFTED_MESSAGE = "drafted-message";
    private static final String DRAFTED_SCHEMAS = "drafted-schemas";
    private static final String GAME_BUTTON = "game-button";
    private static final String TAB = "tab";
    private static final String TURN_INDICATOR = "turn-indicator";
    private static final String CURRENTLY_PLAYING = "currently-playing";
    private static final String TAB_CONTAINER = "tab-container";
    private static final String CARD_CONTAINER = "card-container";
    private static final String CARD = "card";
    private static final String OPAQUE_BACKGROUND = "opaque-background";
    private static final String PLAYER_PLAYING = "player-playing";
    private static final String PLAYER_DISCONNECTED = "player-disconnected";
    private static final String PLAYER_QUITTED = "player-quitted";
    private static final String PLAYER_INFO = "player-info";
    private static final String ROUNDTRACK_CELL = "roundtrack-cell";
    private static final String FAVOR_TOKENS = "favor-tokens";
    private static final String PLAYER_SCHEMA = "player-schema";
    private static final String LOGIN_BUTTON = "login-button";
    private static final String SCORE_CONTAINER = "score-container";
    private static final String ROUND_DICE = "round-dice";
    private static final String TOP_SECTION = "top-section";
    private static final String CONNECTION_ERROR = "connection-error";


    /**
     * Constructor of the class
     * @param visualBounds a rectangle with the dimension of the screen
     * @param cmdWrite an instance of the CmdWriter that is set and then it's used to write the commands in the QuequedReader
     * @param uimsg a reference to  UImessages used to get the messages to show to the user
     */
    public GUIutil(Rectangle2D visualBounds, CmdWriter cmdWrite, UIMessages uimsg) {
        screenWidth = visualBounds.getWidth();
        screenHeight = visualBounds.getHeight();
        this.cmdWrite = cmdWrite;
        this.uimsg = uimsg;
    }

    public double getStageX() { return (screenWidth - getGameSceneMinWidth())/2; }

    public double getStageY() { return (screenHeight -getGameSceneMinHeight())/2;}

    public double getLoginWidth() { return screenWidth * LOGIN_TO_SCREEN_RATIO; }

    public double getLoginHeight() { return getLoginWidth() / LOGIN_RATIO; }

    public double getLobbyMinHeight() { return getLobbyMinWidth() / LOBBY_SCENE_RATIO; }

    public double getLobbyMinWidth() { return LOBBY_SCENE_W_TO_SCREEN_RATIO * screenWidth; }

    public double getGameSceneMinWidth(){ return MAIN_SCENE_WIDTH_TO_SCREEN_WIDTH* screenWidth; }

    public double getGameSceneMinHeight(){ return (getGameSceneMinWidth()/MAIN_GAME_SCENE_RATIO); }

    private double getCardWidth(double cellDim) { return cellDim*CARD_WIDTH_TO_CELL_DIM; }

    private double getCardHeight(double cellDim) { return cellDim*CARD_HEIGHT_TO_CELL_DIM; }

    /**
     * This method computes the dimensions of the cell in a scene based on the dimensions of the stage containing the scene
     * and a predifined aspect ratio. The cell dimensions are then used as an unit of mesure for drawing every other component in the scene. The ratio
     * ensures that all the components in the scene are drawn to fit the stage
     * @param newWidth the width of the stage in witch the all the components are going to be drawn
     * @param newHeight the height of the stage in witch the all the components are going to be drawn
     * @return the dimension of the cell in the new stage
     */
    private double getMainSceneCellDim(double newWidth, double newHeight) {
        double cellDim;
        double sceneRatio = newWidth / newHeight;
        if (sceneRatio >= MAIN_GAME_SCENE_RATIO) {
            cellDim = newHeight* MAIN_GAME_CELL_DIM_TO_HEIGHT;
        } else {
            cellDim = newWidth* MAIN_GAME_CELL_DIM_TO_WIDTH;
        }
        return cellDim;
    }

    /**
     * This method builds the parent to be set as a root in the lobby scene
     * @param numUsers the number of users currently in the lobby
     * @return the root of the scene
     */
    public StackPane buildLobbyPane(int numUsers) {
        StackPane p = new StackPane();
        p.setStyle(IMG_WALL_PNG);
        Label lobbyLabel = new Label(String.format(uimsg.getMessage(LOBBY_UPDATE),numUsers));
        lobbyLabel.setId(LOBBY_MESSAGE);
        p.getChildren().add(lobbyLabel);
        return new StackPane(p);
    }

    /**
     * This method builds the scene shown when the connection is broken
     * @return the scene already with its nodes
     */
    public Scene buildConnecionBrokenScene() {
        Label connectionBrokeMessage = new Label(uimsg.getMessage(BROKEN_CONNECTION));
        connectionBrokeMessage.setFont(new Font(FONT, screenWidth *CONN_BROKEN_FONT_TO_SCREEN));
        connectionBrokeMessage.setId(CONNECTION_ERROR);
        StackPane layout = new StackPane(connectionBrokeMessage);
        layout.setStyle("-fx-background-image: url('img/wall.png')");
        Scene connectionBrokeScene = new Scene(layout,screenWidth*0.32,screenWidth*0.16);
        connectionBrokeScene.getStylesheets().add("css/style.css");
        return connectionBrokeScene;
    }

    /**
     * This method builds the parent to be set as a root in the waiting for game start scene
     * @return a stack pane already populated with nodes
     */
    public StackPane buildWaitingForGameStartScene() {
        String message = String.format("%s%n", uimsg.getMessage(WAIT_FOR_GAME_START));
        Label waitingText = new Label(message);
        waitingText.setId(LOBBY_MESSAGE);
        StackPane stackPane = new StackPane(waitingText);
        stackPane.setStyle(IMG_WALL_PNG);
        return stackPane;
    }

    /**
     * This method computes the dimensions of the cell in the drafted Schemas Scene based on the dimensions of the stage containing the scene
     * and a predefined aspect ratio. The cell dimensions are then used as an unit of mesure for drawing every other component in the scene. The ratio
     * ensures that all the components in the scene are drawn to fit the stage
     * @param newWidth the width of the stage in witch the all the components are going to be drawn
     * @param newHeight the height of the stage in witch the all the components are going to be drawn
     * @return the dimension of the cell in the new stage
     */
    private double getDraftedSchemasCellDim(double newWidth, double newHeight) {
        double cellDim;
        double sceneRatio = newWidth / newHeight;
        if (sceneRatio >= LOBBY_SCENE_RATIO) {
            cellDim = newHeight* DRAFTED_SCHEMAS_CELL_DIM_TO_SCENE_HEIGHT;
        } else {
            cellDim = newWidth* DRAFTED_SCHEMAS_CELL_DIM_TO_SCENE_WIDTH;
        }
        return cellDim;
    }

    /**
     * creates a border pane with the four drafted schemas set in the center
     * @param draftedSchemas the four drafted schemas from witch the  player as to choose
     * @param lightPrivObj the light private objective also shown to help the player chose the best schema to achieve it
     * @param newWidth the width of the stage canting the pane
     * @param newHeight the width of the height containing the pane
     * @return the populated pane
     */
    public BorderPane buildDraftedSchemasPane(List<LightSchemaCard> draftedSchemas, LightPrivObj lightPrivObj, double newWidth, double newHeight){
        double cellDim = getDraftedSchemasCellDim(newWidth,newHeight);
        double spacing = DRAFTED_SCHEMAS_SPACING_TO_CELL*cellDim;

        BorderPane draftedSchemasPane = new BorderPane();
        draftedSchemasPane.setStyle(IMG_WALL_PNG);

        GridPane schemasGrid = new GridPane();
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                int schemaIndex =i*2+j;
                Group completeSchema = buildDraftedSchema(draftedSchemas.get(schemaIndex),cellDim);
                schemasGrid.add(completeSchema,j,i);
                completeSchema.setOnMouseClicked(e-> {synchronized (lockWrite){cmdWrite.write(schemaIndex);}});
            }
        }
        schemasGrid.setVgap(spacing);
        schemasGrid.setHgap(spacing);
        StackPane privObj = new StackPane(drawCard(lightPrivObj, PRIVOBJ_W_TO_CELL_DIM*cellDim, PRIVOBJ_W_TO_CELL_DIM*cellDim/PRIVATE_OBJ_RATIO));
        privObj.setPadding(new Insets(0,0,0,spacing));
        StackPane cardsContainer = new StackPane(new Group(new HBox(schemasGrid, privObj)));
        cardsContainer.setAlignment(CENTER);
        draftedSchemasPane.setCenter(cardsContainer);

        Label selectSchemaText = new Label(uimsg.getMessage(CHOOSE_SCHEMA_2));
        selectSchemaText.setId(DRAFTED_MESSAGE);
        selectSchemaText.setAlignment(CENTER);
        selectSchemaText.setMinWidth(newWidth);
        selectSchemaText.setFont(Font.font(FONT, DRAFTED_SCHEMAS_TEXT_TO_CELL*cellDim));
        draftedSchemasPane.setTop(new StackPane(selectSchemaText));

        return draftedSchemasPane ;
    }

    /**
     * This method creates a new schema to be shown in the drafted schemas pane. Said schema other than the cells contains also the name of the schema and the favortokens
     * @param lightSchemaCard the schema to be drawn
     * @param cellDim the dimension of the cells in the schema
     * @return a group containing the complete schema
     */
    private Group buildDraftedSchema(LightSchemaCard lightSchemaCard, double cellDim) {
        double schemaWidth = cellDim*SCHEMA_W_TO_CELL;
        double schemaHeight = cellDim*SCHEMA_H_TO_CELL;
        double nameLabelSize = SCHEMA_LABEL_TO_CELL_DIM*cellDim;
        double arcCurvature = SCHEMA_ARC_TO_WIDTH * schemaWidth;

        Canvas schemaBlackBorders = new Canvas(schemaWidth,schemaHeight);
        GraphicsContext gc = schemaBlackBorders.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(0, 0, schemaWidth, schemaHeight, arcCurvature, arcCurvature);
        double x = schemaWidth / 2;
        double y = TEXT_HEIGHT_TO_SCHEMA_H * schemaHeight;
        drawSchemaText(gc, x, y, schemaWidth, lightSchemaCard);
        drawFavorTokens(gc, y, schemaWidth, lightSchemaCard);

        Rectangle backgroundRect = new Rectangle(0,0,schemaWidth,schemaHeight);
        backgroundRect.setArcWidth(arcCurvature);
        backgroundRect.setArcHeight(arcCurvature);
        backgroundRect.setFill(Color.BLACK);

        Group g = new Group(schemaToGrid(getSchemaCells(lightSchemaCard,cellDim)));
        Rectangle spacer = new Rectangle(nameLabelSize,nameLabelSize);
        spacer.setVisible(false);
        Group cells = new Group(new VBox(g, spacer));
        Group completeSchema = new Group(new StackPane(schemaBlackBorders,cells));
        completeSchema.setId(DRAFTED_SCHEMAS);

        return new Group(new StackPane(backgroundRect,completeSchema));
    }

    /**
     * draws the favor tokens as dots on a Canvas that then will be the frame of a schema
     * @param gc the graphics context of the canvas where the dots are drawn
     * @param y the coordinate of where the dot is drawn
     * @param schemaWidth the width of the schema to be drawn
     * @param lightSchemaCard the light schema that gets drawn
     */
    private void drawFavorTokens(GraphicsContext gc, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        double x = 0;
        int favorTokens = lightSchemaCard.getFavorTokens();
        double favTokDiameter = schemaWidth * FAVOR_DIAM_TO_SCHEMA_W;
        x = x + FAVOR_POS_TO_SCHEMA_W * schemaWidth;
        for (int i = 0; i < favorTokens; i++) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y + favTokDiameter / 3, favTokDiameter, favTokDiameter);
            x = x - favTokDiameter - favTokDiameter / 10;
        }

    }

    /**
     * draws the name of the schema on a Canvas that then will be the frame of a schema
     * @param gc the graphics context of the canvas where the the text is drawn
     * @param x coordinate of where the text is drawn
     * @param y coordinate of where the text is drawn
     * @param schemaWidth the width of the schema  to be drawn
     * @param lightSchemaCard the light schema that gets drawn
     */
    private void drawSchemaText(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        double textSize = TEXT_DIM_TO_SCHEMA_W * schemaWidth;
        gc.setFont(Font.font(FONT, textSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.AZURE);
        gc.fillText(lightSchemaCard.getName(), x, y);
    }

    /**
     * This class builds a border pane used in the main game scene, it contais the schema, draftpool, rondtrack, and cards
     * @param newWidth the width of the stage canting the pane
     * @param newHeight the width of the height containing the pane
     * @param board the lightboard
     * @param turnState the stte of the client Fsm
     * @return the populated BorderPane
     */
    public BorderPane buildFrontPane(double newWidth, double newHeight, LightBoard board, ClientFSMState turnState){
        double                      cellDim = getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrackList = board.getRoundTrack();
        List <LightDie> draftPool = board.getDraftPool();
        LightSchemaCard             schemaCard = board.getPlayerById(board.getMyPlayerId()).getSchema();
        int favorTokens =           board.getPlayerById(board.getMyPlayerId()).getFavorTokens();

        BorderPane frontPane = new BorderPane();

        frontPane.setStyle("-fx-background-image: url('img/blue-wall.png')");

        List<DieContainer> draftPoolCells = getDraftPoolCells(draftPool,cellDim);
        List<DieContainer> schemaCells = getSchemaCells(schemaCard,cellDim);
        List<DieContainer> roundTrackCells = getRoundTrackCells(roundTrackList,cellDim);
        addActionListeners(draftPoolCells,schemaCells,roundTrackCells,turnState,board);

        //Top side of the border pane
        HBox topSection = buildRoundTrack(roundTrackCells);
        Region separator = new Region();
        HBox.setHgrow(separator,Priority.ALWAYS);
        VBox menuButtons = buildMenuButtons(turnState);
        topSection.getChildren().addAll(separator,menuButtons);
        topSection.setId(TOP_SECTION);
        frontPane.setTop(topSection);

        //Center side of the border pane
        Group schema = buildSchema(schemaCells,favorTokens,board.getPlayerById(board.getMyPlayerId()).getUsername());
        HBox playersStatusBar = getPlayersStatusBar(board);
        StackPane schemaContainer = new StackPane(schema);
        VBox.setVgrow(schemaContainer,Priority.ALWAYS);
        frontPane.setCenter(new VBox(schemaContainer,playersStatusBar));

        //Right side of the border pane
        VBox cards = drawCards(board,cellDim,turnState);
        StackPane cardsContainer = new StackPane(cards);
        cards.setAlignment(CENTER);
        GridPane draftpool = buildDraftPool(draftPoolCells);
        VBox.setVgrow(cardsContainer,Priority.ALWAYS);
        frontPane.setRight(new VBox(cardsContainer,draftpool));
        return frontPane;
    }

    /**
     * Calculates the index of a die in a cell of the roundtrack that has multiple dice compared to all the dice in the roundtrack
     * @param index the index of the die in a multiple dice cell compared to the list of dice in that cell
     * @param roundTrack a list of lists that contains all the dice in the roundtrack
     * @return the index of a die compared to all the dice in the roundtrack list
     */
    private int getMultipleDieTrackCellIndex(int index, List<List<LightDie>> roundTrack) {
        int selectedCellindex = 0;
        for (int j = 0; j < index; j++) {
            selectedCellindex += roundTrack.get(j).size();
        }
        return selectedCellindex;
    }

    /**
     * It creates a HBox container that is shown under the roundtrack and shows the dice in a cell of the roundtrack tha has multiple dice
     * @param cellDim the dimension of the cells inside tehe HBox
     * @param selectedTrackCellIndex the index of the cell in the roundtrack whose multiple dice are showing
     * @param roundTrack all the dice in the roundtrack
     * @param turnState the state of the ClientFSM
     * @param latestDiceList the latest dice list sent by the server
     * @return the HBox container with all the multiple dices inside
     */
    private HBox buildMultipleDiceBar(double cellDim, int selectedTrackCellIndex, List<List<LightDie>> roundTrack, ClientFSMState turnState, List<IndexedCellContent> latestDiceList) {
        HBox multipleDiceTrack = new HBox();
        multipleDiceTrack.setSpacing(ROUNDTRACK_SPACING);

        List<LightDie> multipleDiceList = roundTrack.get(selectedTrackCellIndex);
        int multipleDiceListSize = multipleDiceList.size();
        int startingIndex = selectedTrackCellIndex -  (multipleDiceListSize / 2);
        if (startingIndex + multipleDiceListSize > ROUNDTRACK_SIZE) {
            startingIndex = ROUNDTRACK_SIZE - multipleDiceListSize;
        }

        for (int i = 0; i < startingIndex; i++) {
            DieContainer emptyCell = new DieContainer(cellDim);
            emptyCell.hideCellBorders();
            multipleDiceTrack.getChildren().add(emptyCell);
        }
        for (int i = 0; i < multipleDiceListSize; i++) {
            DieContainer cell = new DieContainer(cellDim);
            cell.hideCellBorders();
            cell.setId(ROUND_DICE);
            cell.putDie(multipleDiceList.get(i));
            multipleDiceTrack.getChildren().add(cell);
            if (turnState.equals(SELECT_DIE) && !latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
                cell.highlightOrange();
                int multipleDieIndex = getMultipleDieTrackCellIndex(selectedTrackCellIndex, roundTrack) + i;
                cell.setOnMouseClicked(e -> {synchronized (lockWrite){cmdWrite.write(multipleDieIndex);}});
            }
        }
        return multipleDiceTrack;
    }

    /**
     * This method builds a BorderPane that that gets shown every time the mouse hovers on a cell of the rountrack that contains more than one die
     * @param selectedTrackCellIndex the cell in the roundtrack that has more than one die
     * @param newWidth the width of the new scene
     * @param newHeight the height of the new scene
     * @param board the lighttboard of the player
     * @param turnState the ClientFSm state
     * @return a BorderPane containing a container showing  a row o dice
     */
    public BorderPane showMultipleDiceRoundTrack(int selectedTrackCellIndex, double newWidth, double newHeight, LightBoard board,ClientFSMState turnState){
        double                      cellDim = getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrack = board.getRoundTrack();
        List <IndexedCellContent>   latestDiceList = board.getLatestDiceList();

        BorderPane backPane = new BorderPane();
        HBox d1 = buildDummyTrack(cellDim,selectedTrackCellIndex,roundTrack);
        HBox d2 = buildMultipleDiceBar(cellDim,selectedTrackCellIndex,roundTrack,turnState,latestDiceList);
        VBox vbox =new VBox(d1,d2);
        Event mouseExited = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
        vbox.setOnMouseExited(e->vbox.fireEvent(mouseExited));
        backPane.setTop(vbox);
        vbox.setAlignment(TOP_LEFT);
        backPane.setStyle("-fx-background-color: rgb(255,255,255,0.4);");
        return backPane;
    }

    /**
     * This method creates an invisible track without dice that has to bu put over the real roundtrack that listens for mouse hovering and acts as a spacer for the multiple dice track
     * @param cellDim the dimension of the cell in the dummy truck
     * @param selectedTrackCellIndex the currently selected cell in the real roundtrack, the listener on mouse hover gets disabled for it
     * @param roundTrack a list of lists of all the dice in the roundtrack
     * @return a container containing invisible cells that act as roundtrack cell for mouse MouseEntererd events
     */
    private HBox buildDummyTrack(double cellDim, int selectedTrackCellIndex, List<List<LightDie>> roundTrack) {
        HBox track = new HBox();
        track.setSpacing(ROUNDTRACK_SPACING);
        for (int i = 0; i < ROUNDTRACK_SIZE; i++) {
            DieContainer dummyCell = new DieContainer(cellDim);
            dummyCell.hideCellBorders();
            track.getChildren().add(dummyCell);
            if (i < roundTrack.size() && roundTrack.get(i).size() > 1) {
                if(i == selectedTrackCellIndex){
                    //to avoid having the same event being fired continuously while the mouse is above a roundtrack cell
                    continue;
                }
                Event showMultipleDice = new CustomGuiEvent(MOUSE_ENTERED_MULTIPLE_DICE_CELL, i);
                dummyCell.setOnMouseEntered(e -> dummyCell.fireEvent(showMultipleDice));
            }
        }
        return track;
    }

    /**
     * Creates a container for the buttons to end the turn and to revert a selection
     * @param turnState the state of the ClientFSM
     * @return the container with the buttons
     */
    private VBox buildMenuButtons(ClientFSMState turnState) {
        Button endTurn = new Button(END_TURN);
        Button back = new Button(BACK);
        endTurn.setId(GAME_BUTTON);
        back.setId(GAME_BUTTON);

        if (turnState.equals(ClientFSMState.NOT_MY_TURN)){
            back.setDisable(true);
            endTurn.setDisable(true);
        }else if(turnState.equals(MAIN)){ //can't go back when in main
            back.setDisable(true);
        }
        endTurn.setOnAction(e -> {synchronized (lockWrite){cmdWrite.write(ClientFSM.END_TURN);}});
        back.setOnAction(e -> {synchronized (lockWrite){cmdWrite.write(ClientFSM.BACK);}});
        VBox buttonContainer = new VBox();
        buttonContainer.getChildren().addAll(back, endTurn);
        return buttonContainer;
    }

    /**
     * @param board a copy of the lightBoard
     * @param cellDim the dimension of the cell in the game scene, used for scale when drawing the main game scene
     * @param turnState the current state of the clientFSM
     * @return a VBox containing the cards, the buttons to switch them and the information regarding who is playing and the current turn
     */
    private VBox drawCards(LightBoard board, double cellDim, ClientFSMState turnState) {
        Button priv = new Button(uimsg.getMessage(UIMsg.PRIVATE_OBJ).replaceFirst(":",""));
        Button pub = new Button(uimsg.getMessage(UIMsg.PUBLIC_OBJ).replaceFirst(":",""));
        Button tool = new Button(uimsg.getMessage(UIMsg.TOOLS).replaceFirst(":",""));

        priv.setId(TAB);
        pub.setId(TAB);
        tool.setId(TAB);

        Label turnIndicator = new Label();
        turnIndicator.setId(TURN_INDICATOR);
        if(board.getIsFirstTurn()) {
            turnIndicator.setText(uimsg.getMessage(FIRST_TURN));
        }else{
            turnIndicator.setText(uimsg.getMessage(SECOND_TURN));
        }

        Label currentlyPlaying = new Label();
        currentlyPlaying.setId(CURRENTLY_PLAYING);
        if(board.getNowPlaying() == board.getMyPlayerId()){
            currentlyPlaying.setText(uimsg.getMessage(YOUR_TURN));
        }else{
            currentlyPlaying.setText(board.getPlayerById(board.getNowPlaying()).getUsername()+uimsg.getMessage(IS_PLAYING));
        }
        //the container of the button to switch view of the cards
        HBox buttonContainer = new HBox(priv, pub, tool,currentlyPlaying,turnIndicator);
        buttonContainer.setId(TAB_CONTAINER);

        HBox cardContainer = new HBox();
        VBox primaryContainer = new VBox(buttonContainer, cardContainer);

        cardContainer.setId(CARD_CONTAINER);
        priv.setOnAction(e -> {
            Rectangle privObjImg = drawCard(board.getPrivObj(), getCardWidth(cellDim), getCardHeight(cellDim));
            Rectangle emptyRect1 = new Rectangle(getCardWidth(cellDim), getCardHeight(cellDim),Color.TRANSPARENT);
            Rectangle emptyRect2 = new Rectangle(getCardWidth(cellDim), getCardHeight(cellDim),Color.TRANSPARENT);
            cardContainer.getChildren().setAll(privObjImg,emptyRect1,emptyRect2);
        });
        pub.setOnAction(e -> {
            ArrayList<Rectangle> cards = new ArrayList<>();
            for (LightCard pubObjCard : board.getPubObjs()) {
                cards.add(drawCard(pubObjCard, getCardWidth(cellDim), getCardHeight(cellDim)));

            }
            cardContainer.getChildren().setAll(cards);
        });
        tool.setOnAction(e1 -> {
            ArrayList<Group> cards = new ArrayList<>();
            for (LightTool toolCard : board.getTools()) {
                Group toolRect = drawCard(toolCard, getCardWidth(cellDim), getCardHeight(cellDim),toolCard.isUsed());
                toolRect.setOnMouseClicked(e2 -> {
                    if (turnState.equals(MAIN)) {
                        synchronized (lockWrite) {
                            cmdWrite.write("0");
                            cmdWrite.write(board.getTools().indexOf(toolCard));
                        }
                    }
                });
                toolRect.setId(CARD);
                cards.add(toolRect);
            }
            cardContainer.getChildren().setAll(cards);
        });
        tool.fire();
        return primaryContainer;

    }

    /**
     * Draws a single light card
     * @param toolCard the light card to be drawn
     * @param cardWidth the width of the card to be drawn
     * @param cardHeight the height of the card to be drawn
     * @param used the boolean that indicates weather the card has been used
     * @return a group containing the drawn card and the favor token if the boolean used is set
     */
    private Group drawCard(LightCard toolCard, double cardWidth, double cardHeight, boolean used) {
        Group tool = new Group();
        Rectangle imgRect = drawCard(toolCard,cardWidth,cardHeight);
        if(used){
            Canvas favorToken = new Canvas(cardWidth,cardHeight);
            GraphicsContext gc = favorToken.getGraphicsContext2D();
            gc.setFill(Color.TRANSPARENT);
            gc.fillRect(0,0,cardWidth,cardHeight);
            gc.setFill(Color.BLACK);
            double radius = FAVOR_TO_TOOL_W*cardWidth;
            gc.fillOval(cardWidth-2*radius,radius,radius,radius);
            tool.getChildren().setAll(new StackPane(imgRect,favorToken));
        }else{
            tool.getChildren().addAll(imgRect);
        }

        return tool;
    }

    /**
     * Draws a single light card
     * @param card the light card to be drawn
     * @param imageWidth the width of the card to be drawn
     * @param imageHeight the height of the card to be drawn
     * @return a group containing the drawn card
     */
    private Rectangle drawCard(LightCard card, double imageWidth, double imageHeight) {
        Image image = new Image(card.getImgSrc()+".png");
        Rectangle imgRect = new Rectangle(imageWidth, imageHeight);
        ImagePattern imagePattern = new ImagePattern(image);
        imgRect.setFill(imagePattern);
        return imgRect;
    }

    /**
     * builds the pane to be put in front of the main game pane containing the schema of one of the other players playing the game
     * @param showingPlayerId the player whose schema is contained in the pane
     * @param width the width of the scne containg the pane
     * @param height the height of the scne containg the pane
     * @param board a copy of the lightboard of the user
     * @return builds the pane containing the schema of the other players playing the game
     */
    public BorderPane buildSelectedPlayerPane(int showingPlayerId, double width, double height, LightBoard board){
        BorderPane selectedPlayerPane = new BorderPane();
        HBox playersSelector = getPlayersStatusBar(board);
        Region spacer = new Region();
        HBox.setHgrow(spacer,Priority.ALWAYS);
        Event mouseExited = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
        playersSelector.setOnMouseExited(e->playersSelector.fireEvent(mouseExited));
        HBox bottomContainer = new HBox(playersSelector,spacer);
        bottomContainer.setId(OPAQUE_BACKGROUND);
        selectedPlayerPane.setBottom(bottomContainer);

        double cellDim = getMainSceneCellDim(width,height);
        int favortokens = board.getPlayerById(showingPlayerId).getFavorTokens();
        String username = board.getPlayerById(showingPlayerId).getUsername();
        List<DieContainer> selectedPlayerSchema = getSchemaCells(board.getPlayerById(showingPlayerId).getSchema(), cellDim);
        Group playerSchema = buildSchema(selectedPlayerSchema,favortokens,username);
        StackPane schemaContainer = new StackPane(playerSchema);
        schemaContainer.setId(OPAQUE_BACKGROUND);
        selectedPlayerPane.setCenter(schemaContainer);
        schemaContainer.setAlignment(Pos.CENTER);
        return selectedPlayerPane;
    }

    /**
     * Builds  a container containing the buttons to show other's players schemas
     * @param board a copy of the lightboard
     * @return a container containing the buttons that when are clicked launch the event to show other's players schemas
     */
    private HBox getPlayersStatusBar(LightBoard board) {
        HBox playerSelector = new HBox();
        for(int playerId = 0; playerId<board.getNumPlayers();playerId++){
            Button playerStatusBar = getPlayerStatusButton(playerId,board.getPlayerById(playerId).getUsername(),board.getPlayerById(playerId).getStatus(),board.getNowPlaying());

            if(playerId == board.getMyPlayerId()){
                continue;
            }else{
                Event mouseEnteredPlayerStatusBar = new CustomGuiEvent(SELECTED_PLAYER, playerId);
                playerStatusBar.setOnAction(e ->playerStatusBar.fireEvent(mouseEnteredPlayerStatusBar));
            }
            playerSelector.getChildren().add(playerStatusBar);
        }
        playerSelector.setAlignment(Pos.BOTTOM_LEFT);
        return playerSelector;
    }

    /**
     *
     * @param playerId the player that gets shown after the button is pressed
     * @param username the name of the user that gets shown after the button is pressed
     * @param status the status in the game of the player associated to the button
     * @param nowPlaying the player that is now playng
     * @return  a button that when is clicked launch the event to show other's players schemas
     */
    private Button getPlayerStatusButton(int playerId, String username, LightPlayerStatus status, int nowPlaying){
        Button player = new Button(username);
        if(playerId == nowPlaying ){
            player.setId(PLAYER_PLAYING);
        }else if(status.equals(LightPlayerStatus.DISCONNECTED)){
            player.setId(PLAYER_DISCONNECTED);
        }else if(status.equals(LightPlayerStatus.QUITTED)){
            player.setId(PLAYER_QUITTED);
        }else{
            player.setId(PLAYER_INFO);
        }
        return player;
    }

    /**
     *
     * @param width the width of the scene containing the pane
     * @param height the height of the scene containing the pane
     * @param board a copy of the lightboard of the user
     * @return a pane containing a list of dice in the center that the user as to select
     */
    public BorderPane bulidDieOptionPane(double width, double height, LightBoard board) {
        BorderPane selectDiePane = new BorderPane();
        HBox optionBox = new HBox();
        Group optionBoxContainer = new Group(optionBox);
        List<IndexedCellContent> latestDiceList = board.getLatestDiceList();
        double cellDim = getMainSceneCellDim(width, height);
        for (IndexedCellContent selectableDie : latestDiceList) {
            DieContainer c = new DieContainer(selectableDie.getContent(), cellDim);
            c.setOnMouseClicked(e -> {
                synchronized (lockWrite) {
                    cmdWrite.write(latestDiceList.indexOf(selectableDie));
                }
                Event exitBackPane = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
                c.fireEvent(exitBackPane);
            });
            c.highlightOrange();
            optionBox.getChildren().add(c);
        }
        optionBox.setAlignment(CENTER);
        selectDiePane.setId(OPAQUE_BACKGROUND);
        selectDiePane.setCenter(new StackPane(optionBoxContainer));
        return selectDiePane;
    }

    /**
     * @param roundTrackCells all the cells in the roundtrack
     * @return an HBox containing the cells in the roundtrack
     */
    private HBox buildRoundTrack(List<DieContainer> roundTrackCells) {
        HBox track = new HBox();
        track.setSpacing(ROUNDTRACK_SPACING);
        track.getChildren().addAll(roundTrackCells);
        track.setAlignment(TOP_LEFT);
        return track;
    }

    /**
     * Creates a list of cells in the roundtrack, occupied by dice. If the roundtrack cell has more than one die inside, two dice are drawn in the cell and said cell gets a listener on mouse passed,
     * that when activated fires the event to show all the dice in said cell
     * @param roundTrack a list of all the dice in the track
     * @param cellDim the dimension of the cell to be created
     * @return a list of all the cells to be put on roundtrack
     *
     */
    private List<DieContainer> getRoundTrackCells(List<List<LightDie>> roundTrack, double cellDim) {
        ArrayList<DieContainer> roundTrackCells = new ArrayList<>();
        for (int i = 0; i < ROUNDTRACK_SIZE; i++) {
            DieContainer cell = new DieContainer(i, cellDim);
            cell.setId(ROUNDTRACK_CELL);
            if (i < roundTrack.size()) {
                if (roundTrack.get(i).size() > 1) {
                    //draw to dice in a cell
                    cell.putDoubleDice(roundTrack.get(i).get(0),roundTrack.get(i).get(1));
                    Event myEvent = new CustomGuiEvent(MOUSE_ENTERED_MULTIPLE_DICE_CELL, i);
                    cell.setOnMouseEntered(e -> cell.fireEvent(myEvent));
                } else {
                    cell.putDie(roundTrack.get(i).get(0));
                }

            }
            roundTrackCells.add(cell);
        }
        return roundTrackCells;
    }

    /**
     * Creates a list of cells containing all the cells of the schema, already populated with dice and constraints
     * @param lightSchemaCard the light schema card whose cells are created
     * @param cellDim the dimension of the cells
     * @return a list of DieContainers corresponding to the cells in the schema
     */
    private List<DieContainer> getSchemaCells(LightSchemaCard lightSchemaCard, double cellDim) {
        ArrayList<DieContainer> gridCells = new ArrayList<>();
        for (int i = 0; i < NUM_COLS * NUM_ROWS; i++) {
            DieContainer cell = new DieContainer(cellDim);
            if (lightSchemaCard.hasConstraintAt(i)) {
                cell.putConstraint(lightSchemaCard.getConstraintAt(i));
            }
            if (lightSchemaCard.hasDieAt(i)) {
                cell.putDie(lightSchemaCard.getDieAt(i));
            }

            gridCells.add(cell);

        }
        return gridCells;
    }

    /**
     *Groups a list of DieContainer in a schema and puts a black frame around it showing the number of favor tokens and the name of the player using that schema
     * @param gridCells the cells of the schemas tha are grouped together
     * @param favortokens the number of favor tokens remaing to the player
     * @param userName the name of the user using the created schema
     * @return a group containing the drawn schema
     */
    private Group buildSchema(List<DieContainer> gridCells, int favortokens, String userName) {
        GridPane grid = schemaToGrid(gridCells);
        Label favorTokens = new Label(uimsg.getMessage(REMAINING_TOKENS)+" "+CLIUtils.replicate(CLIUtils.FAVOR,favortokens));
        favorTokens.setId(FAVOR_TOKENS);
        Label username = new Label(userName);
        username.setId(FAVOR_TOKENS);
        HBox nameAndFavor = new HBox(favorTokens,username);
        VBox schema =new VBox(nameAndFavor,new Group(grid));
        schema.setId(PLAYER_SCHEMA);
        favorTokens.prefWidthProperty().bind(grid.widthProperty().divide(1.7));
        return new Group (schema);
    }

    /**
     * Creates a list of cells containing all the cells of the drafpool
     * @param draftPool a list of dice in the draftpool
     * @param cellDim the dimensions of the cell in the game scene, from witch is based the dimension of dice
     * @return al list of DieContainer containing the dices of the draftpool
     */
    private List<DieContainer> getDraftPoolCells(List<LightDie> draftPool, double cellDim) {
        ArrayList<DieContainer> poolDice = new ArrayList<>();
        for (LightDie draftPoolDice : draftPool) {
            DieContainer cell = new DieContainer(cellDim);
            cell.hideCellBorders();
            cell.putDie(draftPoolDice);
            poolDice.add(cell);
        }
        return poolDice;
    }

    /**
     * Places all the dice of the draftpool in a grid
     * @param poolDice a list of dieContainer containing the dice of the draftpool
     * @return a grid containing the dice in the draftpool
     */
    private GridPane buildDraftPool(List<DieContainer> poolDice) {
        GridPane pool = new GridPane();
        int i = 0;
        int coloumnIndex = NUM_COLS;
        while (i < poolDice.size() && coloumnIndex>0){
            pool.add(poolDice.get(i),coloumnIndex,1);
            i++;
            coloumnIndex--;
        }
        coloumnIndex = NUM_COLS;
        while (i < poolDice.size()){
            pool.add(poolDice.get(i),coloumnIndex,0);
            coloumnIndex--;
            i++;
        }
        pool.setPadding(new Insets(10,10,10,10));
        pool.setAlignment(BOTTOM_RIGHT);
        return pool;

    }

    /**
     * Groups a list of die container containing the dice of the draftpool in a grid
     * @param gridCells a list of DieContainer containing the dice of the drafpool
     * @return a GridPane containing the dice of the draftpool
     */
    private GridPane schemaToGrid(List<DieContainer> gridCells) {
        GridPane grid = new GridPane();
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                grid.add(gridCells.get(row * NUM_COLS + col), col, row);
            }
        }
        grid.setStyle("-fx-background-color: rgb(0,0,0);");
        grid.setAlignment(CENTER);
        return grid;
    }

    /**
     * Adds mouse action listeners to all the DieContainers based on the ClientFsm turn state
     * @param draftPoolCells a list containing theDieContainer of the draftpool
     * @param schemaCells draftPoolCells a list containing theDieContainer of the schema card
     * @param roundTrackCells draftPoolCells a list containing theDieContainer of the roundtrack
     * @param turnState the ClientFSM state
     * @param board a copy of the lightboard
     */
    private void addActionListeners(List<DieContainer> draftPoolCells, List<DieContainer> schemaCells, List<DieContainer> roundTrackCells, ClientFSMState turnState, LightBoard board) {
        switch (turnState){
            case MAIN:
                addMainStateActionListeners(draftPoolCells);
                break;
            case SELECT_DIE:
                addSelectDieStateActionListener(draftPoolCells,schemaCells,roundTrackCells,board);
                break;
            case CHOOSE_PLACEMENT:
                addChoosePlacementActionListener(draftPoolCells,schemaCells,board);
                break;
            default:
                break;
        }

    }

    /**
     * Adds mouse action listeners to all the DieContainers when the ClientFSM state is SELECT_DIE
     * @param draftPoolCells the DieContainers containing the dice of draftpool
     * @param schemaCells the DieContainers containing the dice of SchemaCard
     * @param roundTrackCells the DieContainers containing the dice of roundtrack
     * @param board a copy of the client's LightBoard
     */
    private void addSelectDieStateActionListener(List<DieContainer> draftPoolCells, List<DieContainer> schemaCells, List<DieContainer> roundTrackCells, LightBoard board) {
        List<Actions> latestOptionsList = board.getLatestOptionsList();
        List<IndexedCellContent> latestDiceList = board.getLatestDiceList();
        List<List<LightDie>> roundTrack = board.getRoundTrack();

        if(latestDiceList.isEmpty()){
            return;
        }
        //latest dice list has dice
        if (latestDiceList.get(0).getPlace().equals(Place.SCHEMA)) {
            addSelectDieInSchemaAction(schemaCells, latestDiceList);
        } else if (latestDiceList.get(0).getPlace().equals(Place.DRAFTPOOL)) {
            if (latestOptionsList.isEmpty() || (!latestOptionsList.get(0).equals(Actions.SET_SHADE) && !latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
                for (IndexedCellContent activeCell : latestDiceList) {
                    DieContainer cell = draftPoolCells.get(activeCell.getPosition());
                    cell.highlightBlue();
                    cell.setOnMouseClicked(e -> {synchronized (lockWrite) {cmdWrite.write(draftPoolCells.indexOf(cell));}});
                }
            }
        }else if (latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
            addSelectDieInRoudTrackAction(roundTrackCells, roundTrack);
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers of roundtrack when the ClientFSM state is SELECT_DIE
     * @param roundTrackCells the DieContainers containing the dice of roundtrack
     * @param roundTrack all the light dices in the roundtrack
     */
    private void addSelectDieInRoudTrackAction(List<DieContainer> roundTrackCells, List<List<LightDie>> roundTrack) {
        for (int i = 0; i < roundTrack.size(); i++) {
            roundTrackCells.get(i).highlightBlue();
            if (roundTrack.get(i).size() < 2) {
                roundTrackCells.get(i).highlightOrange();
                int finalI = i;
                roundTrackCells.get(i).setOnMouseClicked(e -> {synchronized (lockWrite) {cmdWrite.write(getMultipleDieTrackCellIndex(finalI, roundTrack));}});
            }
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers of SchemaCard when the ClientFSM state is SELECT_DIE
     * @param schemaCells the DieContainers containing the dice of SchemaCard
     * @param latestDiceList the last list of IndexedCellContent sent by the server
     */
    private void addSelectDieInSchemaAction(List<DieContainer> schemaCells, List<IndexedCellContent> latestDiceList) {
        for (IndexedCellContent activeCell : latestDiceList) {
            DieContainer cell = schemaCells.get(activeCell.getPosition());
            cell.highlightOrange();
            cell.setOnMouseClicked(e -> {synchronized (lockWrite) {cmdWrite.write(latestDiceList.indexOf(activeCell));}});
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers when the ClientFSM state is CHOOSE_PLACEMENT
     * @param draftPoolCells the DieContainers containing the dice of draftpool
     * @param schemaCells the DieContainers containing the dice of SchemaCard
     * @param board a copy of the client's LightBoard
     */
    private void addChoosePlacementActionListener(List<DieContainer> draftPoolCells, List<DieContainer> schemaCells, LightBoard board) {
        IndexedCellContent latestSelectedDie = board.getLatestSelectedDie();
        List<Actions> latestOptionsList = board.getLatestOptionsList();
        List<Integer> latestPlacementsList = board.getLatestPlacementsList();
        List<IndexedCellContent> latestDiceList = board.getLatestDiceList();
        if (!latestOptionsList.isEmpty() && latestOptionsList.get(0).equals(Actions.PLACE_DIE)) {
            //highlight all the cells where I can put a die and add listener on selection
            activateChoosePlacemetCells(schemaCells, latestPlacementsList);

            if (latestSelectedDie.getPlace().equals(Place.DRAFTPOOL)) {
                for (DieContainer cell : draftPoolCells) {
                    if(draftPoolCells.indexOf(cell)==latestSelectedDie.getPosition()){
                        continue;
                    }
                    cell.setOnMouseClicked(e -> {
                        synchronized (lockWrite) {
                            cmdWrite.write(ClientFSM.DISCARD);
                            cmdWrite.write(draftPoolCells.indexOf(cell));
                        }
                    });
                }
            }else if(!latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.SCHEMA)){
                addChosePlacementInSchemaActionListener(schemaCells, latestDiceList);
            }
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers of SchemaCard when the ClientFSM state is CHOOSE_PLACEMENT
     * @param schemaCells the DieContainers containing the dice of SchemaCard
     * @param latestDiceList the last list of IndexedCellContent sent by the server
     */
    private void addChosePlacementInSchemaActionListener(List<DieContainer> schemaCells, List<IndexedCellContent> latestDiceList) {
        for(IndexedCellContent idexedSelectableCell : latestDiceList){
            DieContainer selectableCell = schemaCells.get(idexedSelectableCell.getPosition());
            selectableCell.highlightBlue();
            selectableCell.setOnMouseClicked(e->{
                synchronized (lockWrite) {
                    cmdWrite.write(ClientFSM.DISCARD);
                    cmdWrite.write(latestDiceList.indexOf(idexedSelectableCell));
                }
            });
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers of SchemaCard when the ClientFSM state is CHOOSE_PLACEMENT and latestOption list has PLACE DIE
     * @param schemaCells the DieContainers containing the dice of SchemaCard
     * @param latestPlacementsList the last list of IndexedCellContent of placements sent by the server
     */
    private void activateChoosePlacemetCells(List<DieContainer> schemaCells, List<Integer> latestPlacementsList) {
        for (DieContainer cell : schemaCells) {
            if (latestPlacementsList.contains(schemaCells.indexOf(cell))) {
                cell.highlightGreen();
                cell.setOnMouseClicked(e -> {synchronized (lockWrite) { cmdWrite.write(latestPlacementsList.indexOf(schemaCells.indexOf(cell)));}});
            }
        }
    }

    /**
     * Adds mouse action listeners to all the DieContainers when the ClientFSM state is MAIN
     * @param draftPoolCells the DieContainers containing the dice of draftpool
     */
    private void addMainStateActionListeners(List<DieContainer> draftPoolCells) {
        for (DieContainer cell : draftPoolCells) {
            cell.setOnMouseClicked(e -> {synchronized (lockWrite) {cmdWrite.write("1");}});
        }
    }

    /**
     * Builds the pane to be set as root for the scene shown whe the game has ended
     * @param newWidth the width of the scne containg the pane
     * @param newHeight the height of the scne containg the pane
     * @param board the board
     * @return the built pane with the ranking of the game
     */
    public BorderPane buildGameEndedPane(double newWidth, double newHeight, LightBoard board) {
        double fontDim = getMainSceneCellDim(newWidth,newHeight)*GAME_END_TEXT_TO_CELL;
        Label scoreLabel = new Label(uimsg.getMessage(GAME_END));
        scoreLabel.setFont(Font.font(FONT, fontDim*1.1));

        GridPane scoreBoard = new GridPane();
        scoreBoard.setHgap(10);
        scoreBoard.setVgap(10);
        scoreBoard.setPadding(new Insets(50,50,50,50));
        scoreBoard.setAlignment(CENTER);
        for(int pos = 1; pos <= board.getNumPlayers(); pos++){
            Label name = new Label(board.getByFinalPosition(pos).getUsername());
            Label points = new Label(board.getByFinalPosition(pos).getPoints()+"");
            name.setFont(Font.font(FONT, fontDim));
            points.setFont(Font.font(FONT, fontDim));
            scoreBoard.add(name,0,pos);
            scoreBoard.add(points,1,pos);

        }

        Button newGameButton = new Button(uimsg.getMessage(NEW_GAME_OPTION_2));
        newGameButton.setOnMouseClicked(e->{synchronized (lockWrite) {cmdWrite.write(ClientFSM.NEW_GAME);}});
        newGameButton.setId(LOGIN_BUTTON);

        VBox v2=new VBox(scoreLabel,scoreBoard,newGameButton);
        v2.setAlignment(CENTER);
        v2.setId(SCORE_CONTAINER);
        VBox v = new VBox(v2);
        v.setAlignment(CENTER);
        BorderPane containerPane = new BorderPane();
        containerPane.setCenter(v);
        containerPane.setStyle(IMG_WALL_PNG);
        return containerPane;
    }

}