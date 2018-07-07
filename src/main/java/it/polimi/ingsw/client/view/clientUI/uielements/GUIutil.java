package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.controller.ClientFSMState;
import it.polimi.ingsw.client.controller.CmdWriter;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.controller.ClientFSMState.MAIN;
import static it.polimi.ingsw.client.controller.ClientFSMState.SELECT_DIE;
import static it.polimi.ingsw.client.view.clientUI.uielements.CustomGuiEvent.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg.*;
import static javafx.geometry.Pos.*;

public class GUIutil {
    private final CmdWriter cmdWrite;
    private final UIMessages uimsg;
    private double screenWidth;
    private double screenHeight;

    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private static final double NUM_OF_TOOLS = 3;
    private static final int ROUNDTRACK_SIZE = 10;
    private static final String FONT = "Sans-Serif";
    private static final String IMG_WALL_PNG = "-fx-background-image: url('img/wall.png');";
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
    private static final double MAIN_GAME_CELL_DIM_TO_HEIGHT = 0.121;
    private static final double MAIN_GAME_CELL_DIM_TO_WIDTH = 0.075;
    private static final double MAIN_GAME_SCENE_RATIO = MAIN_GAME_CELL_DIM_TO_HEIGHT/MAIN_GAME_CELL_DIM_TO_WIDTH;
    private static final double CARD_WIDTH_TO_CELL_DIM = 2.65;
    private static final double CARD_HEIGHT_TO_CELL_DIM = 3.6;
    private static final double ROUNDTRACK_SPACING = 5;
    private static final double FAVOR_TO_TOOL_W = 0.13;
    //Game End
    private static final double GAME_END_TEXT_TO_CELL =0.625 ;
    //Connection Broken
    private static final double CONN_BROKEN_FONT_TO_SCREEN = 0.018;



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

    public StackPane buildLobbyPane(int numUsers) {
        StackPane p = new StackPane();
        p.setStyle(IMG_WALL_PNG);
        Label lobbyLabel = new Label(String.format(uimsg.getMessage(LOBBY_UPDATE),numUsers));
        lobbyLabel.setId("lobby-message");
        p.getChildren().add(lobbyLabel);
        return new StackPane(p);
    }


    public Scene buildConnecionBrokenScene() {
        Text connectionBrokeMessage = new Text(uimsg.getMessage(BROKEN_CONNECTION));
        connectionBrokeMessage.setFont(new Font(FONT, screenWidth *CONN_BROKEN_FONT_TO_SCREEN));
        StackPane layout = new StackPane(connectionBrokeMessage);
        return new Scene(layout);
    }

    public StackPane buildWaitingForGameStartScene() {
        String message = String.format("%s%n", uimsg.getMessage(WAIT_FOR_GAME_START));
        Label waitingText = new Label(message);
        waitingText.setId("lobby-message");
        StackPane stackPane = new StackPane(waitingText);
        stackPane.setStyle(IMG_WALL_PNG);
        return stackPane;
    }

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

    public BorderPane buildDraftedSchemasPane(List<LightSchemaCard> draftedSchemas, LightPrivObj lightPrivObj, double newWidth, double newHeight){
        double cellDim = getDraftedSchemasCellDim(newWidth,newHeight);
        double spacing = DRAFTED_SCHEMAS_SPACING_TO_CELL*cellDim;

        BorderPane draftedSchemasPane = new BorderPane();
        draftedSchemasPane.setStyle(IMG_WALL_PNG);

        GridPane schemasGrid = new GridPane();
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                int schemaIndex =i*2+j;
                Group completeSchema = buildDreftedSchema(draftedSchemas.get(schemaIndex),cellDim);
                schemasGrid.add(completeSchema,j,i);
                completeSchema.setOnMouseClicked(e-> cmdWrite.write(schemaIndex+""));
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
        selectSchemaText.setId("drafted-message");
        selectSchemaText.setAlignment(CENTER);
        selectSchemaText.setMinWidth(newWidth);
        selectSchemaText.setFont(Font.font(FONT, DRAFTED_SCHEMAS_TEXT_TO_CELL*cellDim));
        draftedSchemasPane.setTop(new StackPane(selectSchemaText));

        return draftedSchemasPane ;
    }

    private Group buildDreftedSchema(LightSchemaCard lightSchemaCard, double cellDim) {
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
        drawFavorTokens(gc, 0, y, schemaWidth, lightSchemaCard);

        Rectangle backgroundRect = new Rectangle(0,0,schemaWidth,schemaHeight);
        backgroundRect.setArcWidth(arcCurvature);
        backgroundRect.setArcHeight(arcCurvature);
        backgroundRect.setFill(Color.BLACK);

        Group g = new Group(schemaToGrid(getSchemaCells(lightSchemaCard,cellDim)));
        Rectangle spacer = new Rectangle(nameLabelSize,nameLabelSize);
        spacer.setVisible(false);
        Group cells = new Group(new VBox(g, spacer));
        Group completeSchema = new Group(new StackPane(schemaBlackBorders,cells));
        completeSchema.setId("drafted-schemas");

        return new Group(new StackPane(backgroundRect,completeSchema));
    }

    private void drawFavorTokens(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        int favorTokens = lightSchemaCard.getFavorTokens();
        double favTokDiameter = schemaWidth * FAVOR_DIAM_TO_SCHEMA_W;
        x = x + FAVOR_POS_TO_SCHEMA_W * schemaWidth;
        for (int i = 0; i < favorTokens; i++) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y + favTokDiameter / 3, favTokDiameter, favTokDiameter);
            x = x - favTokDiameter - favTokDiameter / 10;
        }

    }

    private void drawSchemaText(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        double textSize = TEXT_DIM_TO_SCHEMA_W * schemaWidth;
        gc.setFont(Font.font(FONT, textSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.AZURE);
        gc.fillText(lightSchemaCard.getName(), x, y);
    }


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
        topSection.setId("top-section");
        frontPane.setTop(topSection);

        //Center side of the border pane
        Group schema = buildSchema(schemaCells,favorTokens,cellDim);
        HBox playersStatusBar = getPlayersStatusBar(board.getMyPlayerId(),board);
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

    private int getMultipleDieTrackCellIndex(int index, List<List<LightDie>> roundTrack) {
        int selectedCellindex = 0;
        for (int j = 0; j < index; j++) {
            selectedCellindex += roundTrack.get(j).size();
        }
        return selectedCellindex;
    }


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
            cell.setId("round-dice");
            cell.putDie(multipleDiceList.get(i));
            multipleDiceTrack.getChildren().add(cell);
            if (turnState.equals(SELECT_DIE) && !latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
                cell.highlightOrange();
                int multipleDieIndex = getMultipleDieTrackCellIndex(selectedTrackCellIndex, roundTrack) + i;
                cell.setOnMouseClicked(e -> cmdWrite.write(multipleDieIndex + ""));
            }
        }
        return multipleDiceTrack;
    }

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

    private VBox buildMenuButtons(ClientFSMState turnState) {
        Button endTurn = new Button("end turn");
        Button back = new Button("back");
        endTurn.setId("game-button");
        back.setId("game-button");

        if (turnState.equals(ClientFSMState.NOT_MY_TURN)){
            back.setDisable(true);
            endTurn.setDisable(true);
        }else if(turnState.equals(MAIN)){ //can't go back when in main
            back.setDisable(true);
        }
        endTurn.setOnAction(e -> cmdWrite.write("e"));
        back.setOnAction(e -> cmdWrite.write("b"));
        VBox buttonContainer = new VBox();
        buttonContainer.getChildren().addAll(back, endTurn);
        return buttonContainer;
    }


    private VBox drawCards(LightBoard board, double cellDim, ClientFSMState turnState) {
        Button priv = new Button(uimsg.getMessage(UIMsg.PRIVATE_OBJ).replaceFirst(":",""));
        Button pub = new Button(uimsg.getMessage(UIMsg.PUBLIC_OBJ).replaceFirst(":",""));
        Button tool = new Button(uimsg.getMessage(UIMsg.TOOLS).replaceFirst(":",""));

        priv.setId("tab");
        pub.setId("tab");
        tool.setId("tab");

        Label turnIndicator = new Label();
        turnIndicator.setId("turn-indicator");
        if(board.getIsFirstTurn()) {
            turnIndicator.setText(uimsg.getMessage(FIRST_TURN));
        }else{
            turnIndicator.setText(uimsg.getMessage(SECOND_TURN));
        }

        Label currentlyPlaying = new Label();
        currentlyPlaying.setId("currently-playing");
        if(board.getNowPlaying() == board.getMyPlayerId()){
            currentlyPlaying.setText(uimsg.getMessage(YOUR_TURN));
        }else{
            currentlyPlaying.setText(board.getPlayerById(board.getNowPlaying()).getUsername()+uimsg.getMessage(IS_PLAYING));
        }
        //the container of the button to switch view of the cards
        HBox buttonContainer = new HBox(priv, pub, tool,currentlyPlaying,turnIndicator);
        buttonContainer.setId("tab-container");

        HBox cardContainer = new HBox();
        VBox primaryContainer = new VBox(buttonContainer, cardContainer);

        cardContainer.setId("card-container");
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
                        cmdWrite.write("0");
                        cmdWrite.write(board.getTools().indexOf(toolCard) + "");
                    }
                });
                toolRect.setId("card");
                cards.add(toolRect);
            }
            cardContainer.getChildren().setAll(cards);
        });
        tool.fire();
        return primaryContainer;

    }

    private Group drawCard(LightCard toolCard, double cardWidth, double cardHeight, boolean used) {
        Group tool = new Group();
        Rectangle imgRect = drawCard(toolCard,cardWidth,cardHeight);
        if(used){
            Canvas favorToken = new Canvas(cardWidth,cardHeight);
            GraphicsContext gc = favorToken.getGraphicsContext2D();
            gc.setFill(Color.TRANSPARENT);
            gc.fillRect(0,0,cardWidth,cardHeight);
            gc.setFill(Color.BLUE);
            double radius = FAVOR_TO_TOOL_W*cardWidth;
            gc.fillOval(cardWidth-2*radius,radius,radius,radius);
            tool.getChildren().setAll(new StackPane(imgRect,favorToken));
        }else{
            tool.getChildren().addAll(imgRect);
        }

        return tool;
    }

    private Rectangle drawCard(LightCard card, double imageWidth, double imageHeight) {
        Image image = new Image(card.getImgSrc()+".png");
        Rectangle imgRect = new Rectangle(imageWidth, imageHeight);
        ImagePattern imagePattern = new ImagePattern(image);
        imgRect.setFill(imagePattern);
        return imgRect;
    }

    public BorderPane buildSelectdPlayerPane(int showingPlayerId, double width, double height, LightBoard board){
        BorderPane selectedPlayerPane = new BorderPane();
        HBox playersSelector = getPlayersStatusBar(showingPlayerId,board);
        Region spacer = new Region();
        HBox.setHgrow(spacer,Priority.ALWAYS);
        HBox bottomContainer = new HBox(playersSelector,spacer);
        selectedPlayerPane.setBottom(bottomContainer);

        double cellDim = getMainSceneCellDim(width,height);
        selectedPlayerPane.setRight(new Rectangle(getCardWidth(cellDim)*NUM_OF_TOOLS,getCardHeight(cellDim),Color.TRANSPARENT)); //the space occupied by cards
        selectedPlayerPane.setTop(new Rectangle(cellDim,cellDim,Color.TRANSPARENT)); //the space occupied by roundtrack
        List<DieContainer> selectedPlayerSchema = getSchemaCells(board.getPlayerById(showingPlayerId).getSchema(), cellDim);
        Group playerSchema = buildSchema(selectedPlayerSchema,board.getPlayerById(showingPlayerId).getFavorTokens(),cellDim);
        StackPane schemaContainer = new StackPane(playerSchema);
        Event mouseExited = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
        schemaContainer.setOnMouseExited(e->schemaContainer.fireEvent(mouseExited));
        schemaContainer.setStyle("-fx-background-color: rgba(245,220,112,0);");
        selectedPlayerPane.setCenter(schemaContainer);
        schemaContainer.setAlignment(Pos.CENTER);
        return selectedPlayerPane;
    }


    private HBox getPlayersStatusBar(int showingPlayerId, LightBoard board) {
        HBox playerSelector = new HBox();
        for(int playerId = 0; playerId<board.getNumPlayers();playerId++){
            Button playerStatusBar = getPlayerStatusButton(playerId,board.getPlayerById(playerId).getUsername(),board.getPlayerById(playerId).getStatus(),board.getNowPlaying());
            playerSelector.getChildren().add(playerStatusBar);
            if( playerId == showingPlayerId){
                continue;
            }else if(playerId == board.getMyPlayerId()){
                Event mouseExited = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
                playerStatusBar.setOnAction(e ->playerStatusBar.fireEvent(mouseExited));
            }else{
                Event mouseEnteredPlayerStatusBar = new CustomGuiEvent(SELECTED_PLAYER, playerId);
                playerStatusBar.setOnAction(e ->playerStatusBar.fireEvent(mouseEnteredPlayerStatusBar));
            }
            playerSelector.setAlignment(Pos.BOTTOM_LEFT);
        }
        return playerSelector;
    }


    private Button getPlayerStatusButton(int playerId, String username, LightPlayerStatus status, int nowPlaying){
        Button player = new Button(username);
        if(playerId == nowPlaying ){
            player.setId("player-playing");
        }else if(status.equals(LightPlayerStatus.DISCONNECTED)){
            player.setId("player-disconnected");
        }else if(status.equals(LightPlayerStatus.QUITTED)){
            player.setId("player-quitted");
        }else{
            player.setId("player-info");
        }
        return player;
    }

    public BorderPane bulidSelectDiePane(double width, double height, LightBoard board) {
        BorderPane selectDiePane = new BorderPane();
        HBox optionBox = new HBox();
        List<IndexedCellContent> latestDiceList = board.getLatestDiceList();
        double cellDim = getMainSceneCellDim(width, height);
        for (IndexedCellContent selectableDie : latestDiceList) {
            DieContainer c = new DieContainer(selectableDie.getContent(), cellDim);
            c.setOnMouseClicked(e -> {
                cmdWrite.write(latestDiceList.indexOf(selectableDie) + "");
                Event exitBackPane = new CustomGuiEvent(MOUSE_EXITED_BACK_PANE);
                c.fireEvent(exitBackPane);
            });
            c.highlightOrange();
            optionBox.getChildren().add(c);
        }
        optionBox.setAlignment(CENTER);
        selectDiePane.setStyle("-fx-background-color: rgb(255,255,255,0.4);");
        selectDiePane.setCenter(new StackPane(optionBox));
        return selectDiePane;
    }

    private HBox buildRoundTrack(List<DieContainer> roundTrackCells) {
        HBox track = new HBox();
        track.setSpacing(ROUNDTRACK_SPACING);
        track.getChildren().addAll(roundTrackCells);
        track.setAlignment(TOP_LEFT);
        return track;
    }

    private List<DieContainer> getRoundTrackCells(List<List<LightDie>> roundTrack, double cellDim) {
        ArrayList<DieContainer> roundTrackCells = new ArrayList<>();
        for (int i = 0; i < ROUNDTRACK_SIZE; i++) {
            DieContainer cell = new DieContainer(i, cellDim);
            cell.setId("roundtrack-cell");
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

    private List<DieContainer> getSchemaCells(LightSchemaCard lightSchemaCard, double cellDim) {
        ArrayList<DieContainer> gridCells = new ArrayList<>();
        for (int i = 0; i < NUM_COLS * NUM_ROWS; i++) {
            DieContainer cell = new DieContainer(cellDim,Place.SCHEMA);
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


    //todo update
    private Group buildSchema(List<DieContainer> gridCells, int favortokens, double cellDim) {
        GridPane grid = schemaToGrid(gridCells);
        Label favorTokens = new Label(uimsg.getMessage(REMAINING_TOKENS)+" "+CLIUtils.replicate(CLIUtils.FAVOR,favortokens));
        favorTokens.setId("favor-tokens");
        VBox schema =new VBox(favorTokens,new Group(grid));
        schema.setId("player-schema");
        favorTokens.prefWidthProperty().bind(grid.widthProperty());
        return new Group (schema);
    }

    private List<DieContainer> getDraftPoolCells(List<LightDie> draftPool, double cellDim) {
        ArrayList<DieContainer> poolDice = new ArrayList<>();
        for (LightDie draftPoolDice : draftPool) {
            DieContainer cell = new DieContainer(cellDim, Place.DRAFTPOOL);
            cell.putDie(draftPoolDice);
            poolDice.add(cell);
        }
        return poolDice;
    }


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

    private void addActionListeners(List<DieContainer> draftPoolCells, List<DieContainer> schemaCells, List<DieContainer> roundTrackCells, ClientFSMState turnState, LightBoard board) {
        switch (turnState){
            case CHOOSE_SCHEMA:
                break;
            case SCHEMA_CHOSEN:
                break;
            case NOT_MY_TURN:
                break;
            case MAIN:
                addMainStateActionListeners(draftPoolCells);
                break;
            case SELECT_DIE:
                addSelectDieStateActionListener(draftPoolCells,schemaCells,roundTrackCells,board);
                break;
            case CHOOSE_OPTION:
                break;
            case CHOOSE_TOOL:
                break;
            case CHOOSE_PLACEMENT:
                addChoosePlacementActionListener(draftPoolCells,schemaCells,board);
                break;
            case TOOL_CAN_CONTINUE:
                break;
            case GAME_ENDED:
                break;
        }

    }
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
                    cell.setOnMouseClicked(e -> cmdWrite.write(draftPoolCells.indexOf(cell) + ""));
                }
            }
        }else if (latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
            addSelectDieInRoudTrackAction(roundTrackCells, roundTrack);
        }
    }

    private void addSelectDieInRoudTrackAction(List<DieContainer> roundTrackCells, List<List<LightDie>> roundTrack) {
        for (int i = 0; i < roundTrack.size(); i++) {
            roundTrackCells.get(i).highlightBlue();
            if (roundTrack.get(i).size() < 2) {
                roundTrackCells.get(i).highlightOrange();
                int finalI = i;
                roundTrackCells.get(i).setOnMouseClicked(e -> cmdWrite.write(getMultipleDieTrackCellIndex(finalI, roundTrack) + ""));
            }
        }
    }

    private void addSelectDieInSchemaAction(List<DieContainer> schemaCells, List<IndexedCellContent> latestDiceList) {
        for (IndexedCellContent activeCell : latestDiceList) {
            DieContainer cell = schemaCells.get(activeCell.getPosition());
            cell.highlightBlue();
            cell.setOnMouseClicked(e -> cmdWrite.write(latestDiceList.indexOf(activeCell) + ""));
        }
    }

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
                    cell.setOnMouseClicked(e -> {
                        cmdWrite.write("d");
                        cmdWrite.write(draftPoolCells.indexOf(cell) + "");
                    });
                }
            }else if(!latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.SCHEMA)){
                for(IndexedCellContent idexedSelectableCell : latestDiceList){
                    DieContainer selectableCell = schemaCells.get(idexedSelectableCell.getPosition());
                    selectableCell.highlightBlue();
                    selectableCell.setOnMouseClicked(e->{
                        cmdWrite.write("d");
                        cmdWrite.write(latestDiceList.indexOf(idexedSelectableCell)+"");
                    });
                }
            }
        }
    }

    private void activateChoosePlacemetCells(List<DieContainer> schemaCells, List<Integer> latestPlacementsList) {
        for (DieContainer cell : schemaCells) {
            if (latestPlacementsList.contains(schemaCells.indexOf(cell))) {
                cell.highlightGreen();
                cell.setOnMouseClicked(e -> cmdWrite.write(latestPlacementsList.indexOf(schemaCells.indexOf(cell)) + ""));
            }
        }
    }

    private void addMainStateActionListeners(List<DieContainer> draftPoolCells) {
        for (DieContainer cell : draftPoolCells) {
            cell.setOnMouseClicked(e -> cmdWrite.write("1"));
        }
    }


    public BorderPane buildGameEndedPane(double newWidth, double newHeight, List<LightPlayer> players) {
        double fontDim = getMainSceneCellDim(newWidth,newHeight)*GAME_END_TEXT_TO_CELL;
        Label scoreLabel = new Label(uimsg.getMessage(GAME_END));
        scoreLabel.setFont(Font.font(FONT, fontDim*1.1));

        GridPane scoreBoard = new GridPane();
        scoreBoard.setHgap(10);
        scoreBoard.setVgap(10);
        scoreBoard.setPadding(new Insets(50,50,50,50));
        scoreBoard.setAlignment(CENTER);
        for(int i = 0; i < players.size();i++){
            Label name = new Label(players.get(i).getUsername());
            Label points = new Label(players.get(i).getPoints()+"");
            name.setFont(Font.font(FONT, fontDim));
            points.setFont(Font.font(FONT, fontDim));
            scoreBoard.add(name,0,i);
            scoreBoard.add(points,1,i);

        }

        Button newGameButton = new Button(uimsg.getMessage(NEW_GAME_OPTION_2));
        newGameButton.setOnMouseClicked(e->cmdWrite.write("n"));
        newGameButton.setId("login-button");

        VBox v2=new VBox(scoreLabel,scoreBoard,newGameButton);
        v2.setAlignment(CENTER);
        v2.setId("score-container");
        VBox v = new VBox(v2);
        v.setAlignment(CENTER);
        BorderPane containerPane = new BorderPane();
        containerPane.setCenter(v);
        containerPane.setStyle(IMG_WALL_PNG);
        return containerPane;
    }

}