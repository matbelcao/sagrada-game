package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.clientController.CmdWriter;
import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.GUI;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.Shade;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.clientFSM.ClientFSMState.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.MyEvent.*;

public class GUIutil {
    private final CmdWriter cmdWrite;
    private GUI gui;
    //ratio is width/height
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private final double SCREEN_WIDTH;
    private final double SCREEN_HEIGHT;
    //-----login Stage
    private static final double LOGIN_TO_SCREEN_RATIO = 0.18;
    private static final double LOGIN_RATIO = 0.95;
    //-----Drafted Schema Stage
    private static final double DRAFTED_SCHEMAS_TO_SCREEN_RATIO = 0.6;
    private static final double DRAFTED_CANVAS_SCENE_RATIO = 1.5314;
    private static final double NUM_OF_DRAFTED_SCHEMAS = 4;
    private static final double SCHEMA_W_TO_PRIVOBJ_W = 1.3846;
    private static final double PRIVATE_OBJ_RATIO = 0.7386;
    private static final double COMPLETE_SCHEMA_RATIO = 1.125;
    private static final double SCHEMA_W_TO_DRAFTED_W = 0.3358;
    private static final double SCHEMA_ARC_TO_WIDTH = 0.0666;
    private static final double SCHEMA_LINE_TO_WIDTH = 0.02;
    private static final double CELL_TO_SCHEMA_W = 0.1925;
    private static final double LINE_TO_CELL = 0.12;
    private static final double TEXT_HEIGHT_TO_SCHEMA_H = 0.90;
    private static final double TEXT_DIM_TO_SCHEMA_W = 0.0505;
    private static final double FAVOR_DIAM_TO_SCHEMA_W = 0.038;
    private static final double FAVOR_POS_TO_SCHEMA_W = 0.92;
    //Those are the ratios between the schemas and their padding respectively to the window, other schemas and priv obj
    private static final double SCHEMA_W_TO_EXTERNAL_PADDING = 18;
    private static final double SCHEMA_W_TO_INTERNAL_PADDING = 18;
    private static final double SCHEMA_W_TO_PRIV_OBJ_PADDING = 9;
    //Main scene
    private static final int ROUNDTRACK_SIZE = 10;
    private static final double ROUNDTRACK_TEXT_SIZE_TO_CELL = 0.7;
    private static final double TEXT_DIM_TO_CELL_DIM = 0.5;
    private static final double MAIN_SCENE_RATIO = 1.4286;
    private static final double MAIN_SCENE_TO_SCREEN = 0.8;
    private static final double DIE_ARC_TO_DIM = 0.35;
    private static final double LINE_TO_DIE = 0.045;

    //die s..
    private static final int SCREEN_TO_DIE = 25;
    private static final int DIE_TO_LINE = 10;
    private static final int SPOT_RATIO = 6;

    private static final int LINE_WIDTH = 2;


    public GUIutil(Rectangle2D visualBounds, GUI gui, CmdWriter cmdWrite) {
        SCREEN_WIDTH = visualBounds.getWidth();
        SCREEN_HEIGHT = visualBounds.getHeight();
        this.gui = gui;
        this.cmdWrite = cmdWrite;
    }

    public double getLoginWidth() {
        return SCREEN_WIDTH * LOGIN_TO_SCREEN_RATIO;
    }

    public double getLoginHeight() {
        return getLoginWidth() / LOGIN_RATIO;
    }

    public double getDraftedSchemasMinHeight() {
        return getDraftedSchemasMinWidth() / DRAFTED_CANVAS_SCENE_RATIO;
    }

    public double getDraftedSchemasMinWidth() {
        return DRAFTED_SCHEMAS_TO_SCREEN_RATIO * SCREEN_WIDTH;
    }

    private double getDraftedSchemasWidth(double drawingHeight) {
        return drawingHeight * DRAFTED_CANVAS_SCENE_RATIO;
    }

    private double getDraftedSchemasHeight(double drawingWidth) {
        return drawingWidth / DRAFTED_CANVAS_SCENE_RATIO;
    }

    private double getCardWidth() {
        double width = 221;
        return width;
    }

    private double getCardHeight() {
        double height = 300;
        return height;
    }

    public double getMainSceneCellDim(double newWidth, double newHeight) { //todo modify
        return 80;
    }

    public double getSelectedSchemaLineWidth(double sceneWidth, double sceneHeight) {
        DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth, sceneHeight);
        double drawingWidth = sizes.getDrawingWidth();
        double schemaWidth = drawingWidth * SCHEMA_W_TO_DRAFTED_W;
        return LINE_TO_CELL * CELL_TO_SCHEMA_W * schemaWidth;
    }

    public HBox buildRoundTrack(List<List<LightDie>> roundTrack, double width, double height, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, int favortokens) {
        double cellDim = getMainSceneCellDim(width, height);
        ArrayList<StackPane> roundTrackCells = new ArrayList<>();

        for (int i = 0; i < ROUNDTRACK_SIZE; i++) {
            StackPane cell = emptyRoundTrackCell(i, cellDim);
            if (i < roundTrack.size()) {
                Canvas c = new Canvas(cellDim, cellDim);
                GraphicsContext gc = c.getGraphicsContext2D();
                if (roundTrack.get(i).size() > 1) {
                    //draw to dice in a cell
                    drawDie(roundTrack.get(i).get(0), gc, cellDim);
                    drawDie(roundTrack.get(i).get(1), gc, cellDim / 2, 0, cellDim);
                    Event myEvent = new MyEvent(MOUSE_ENTERED_MULTIPLE_DICE_CELL, i);
                    cell.setOnMouseEntered(e -> cell.fireEvent(myEvent));
                } else {
                    drawDie(roundTrack.get(i).get(0), gc, cellDim);
                }
                //todo refactor
                if (turnState.equals(SELECT_DIE) && !latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
                    highlight(c, cellDim);
                }
                cell.getChildren().add(c);
            }
            roundTrackCells.add(cell);
        }

        switch (turnState) {
            case SELECT_DIE:
                if (!latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
                    for (int i = 0; i < roundTrack.size(); i++) {
                        if (roundTrack.get(i).size() < 2) {
                            int finalI = i;
                            roundTrackCells.get(i).setOnMouseClicked(e -> cmdWrite.write(getMultipleDieTrackCellIndex(finalI, roundTrack) + ""));
                        }
                    }
                }
                break;
        }
        HBox track = new HBox();
        track.setSpacing(5); //todo add dynamic spacing
        track.getChildren().addAll(roundTrackCells);
        return track;
    }

    public HBox buildDummyTrack(List<List<LightDie>> roundTrack, double newWidth, double newHeight, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, int favorTokens) {
        double cellDim = getMainSceneCellDim(newWidth, newHeight);
        HBox track = new HBox();
        track.setSpacing(10); ////todo dynamic spacing??
        for (int i = 0; i < ROUNDTRACK_SIZE; i++) {
            Rectangle dummyCell = dummyRoundTrackCell(cellDim);
            track.getChildren().add(dummyCell);
            if (i < roundTrack.size() && roundTrack.get(i).size() > 1) {
                Event showMultipleDice = new MyEvent(MOUSE_ENTERED_MULTIPLE_DICE_CELL, i);
                dummyCell.setOnMouseEntered(e -> {
                    dummyCell.fireEvent(showMultipleDice);
                });
            }
        }
        return track;
    }

    public HBox buildMultipleDiceBar(int selectedTrackCellIndex, List<List<LightDie>> roundTrack, double width, double height, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, int favorTokens) {
        double cellDim = getMainSceneCellDim(width, height);
        HBox multipleDiceTrack = new HBox();
        multipleDiceTrack.setSpacing(10); ////todo dynamic spacing??

        List<LightDie> multipleDiceList = roundTrack.get(selectedTrackCellIndex);
        int multipleDiceListSize = multipleDiceList.size();
        int startingIndex = selectedTrackCellIndex - (int) Math.round((multipleDiceListSize / 2));
        if (startingIndex + multipleDiceListSize > ROUNDTRACK_SIZE) {
            startingIndex = ROUNDTRACK_SIZE - multipleDiceListSize;
        }

        for (int i = 0; i < startingIndex; i++) {
            Canvas c = new Canvas(cellDim, cellDim);
            multipleDiceTrack.getChildren().add(c);
        }
        for (int i = 0; i < multipleDiceListSize; i++) {
            Canvas c = new Canvas(cellDim, cellDim);
            GraphicsContext gc = c.getGraphicsContext2D();
            drawDie(multipleDiceList.get(i), gc, cellDim);
            multipleDiceTrack.getChildren().add(c);
            if (turnState.equals(SELECT_DIE) && !latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.ROUNDTRACK)) {
                highlight(c, cellDim);
                int multipleDieIndex = getMultipleDieTrackCellIndex(selectedTrackCellIndex, roundTrack) + i;
                c.setOnMouseClicked(e -> cmdWrite.write(multipleDieIndex + ""));
            }
        }
        return multipleDiceTrack;
    }

    private int getMultipleDieTrackCellIndex(int index, List<List<LightDie>> roundTrack) {
        int selectedCellindex = 0;
        for (int j = 0; j < index; j++) {
            selectedCellindex += roundTrack.get(j).size();
        }
        return selectedCellindex;
    }

    private StackPane emptyRoundTrackCell(int i, double cellDim) {
        int displayedIndex = i + 1;
        double textSize = ROUNDTRACK_TEXT_SIZE_TO_CELL * cellDim;
        Text t = new Text(displayedIndex + "");
        t.setFont(Font.font("Verdana", textSize));
        t.setFill(Color.BLACK);
        double lineWidth = cellDim * LINE_TO_CELL;
        double innerCellDim = cellDim - lineWidth;
        Rectangle outerRect = new Rectangle(0, 0, cellDim, cellDim);
        Rectangle innerRect = new Rectangle(lineWidth, lineWidth, innerCellDim, innerCellDim);
        innerRect.setFill(Color.WHITE);
        return new StackPane(outerRect, innerRect, t);
    }

    private Rectangle dummyRoundTrackCell(double cellDim) {
        Rectangle transparentRect = new Rectangle(0, 0, cellDim, cellDim);
        transparentRect.setFill(Color.TRANSPARENT);
        return transparentRect;
    }

    private StackPane fullRoundTrackCell(int i, double cellDim) {
        int displayedIndex = i + 1;
        double textSize = ROUNDTRACK_TEXT_SIZE_TO_CELL * cellDim;
        Text t = new Text(displayedIndex + "");
        t.setFont(Font.font("Verdana", textSize));
        t.setFill(Color.GREEN);
        double lineWidth = cellDim * LINE_TO_CELL;
        double innerCellDim = cellDim - lineWidth;
        Rectangle outerRect = new Rectangle(0, 0, cellDim, cellDim);
        Rectangle innerRect = new Rectangle(lineWidth, lineWidth, innerCellDim, innerCellDim);
        innerRect.setFill(Color.PINK);
        return new StackPane(outerRect, innerRect, t);
    }

    private void drawEmptyRoundTrackCell(GraphicsContext gc, int index, double cellDim) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.strokeRect(0, 0, cellDim, cellDim);
        drawRoundTrackNumber(gc, index, cellDim);

    }

    private void drawRoundTrackNumber(GraphicsContext gc, int index, double cellDim) {
        double textSize = TEXT_DIM_TO_CELL_DIM * cellDim;
        gc.setFont(Font.font("Calibri", textSize));
        //gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(index + "", cellDim / 2, cellDim / 2);
    }

    public GridPane buildDraftPool(List<LightDie> draftPool, double dieDim, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, List<Actions> latestOptionsList) {
        ArrayList<Canvas> poolDice = new ArrayList<>();
        for (int i = 0; i < draftPool.size(); i++) {
            Canvas c = new Canvas(dieDim, dieDim);
            drawDie(draftPool.get(i), c.getGraphicsContext2D(), dieDim);
            poolDice.add(c);
        }
        switch (turnState) {
            case MAIN:
                for (Canvas c : poolDice) {
                    c.setOnMouseClicked(e -> {
                        cmdWrite.write("1");
                    });
                }
                break;
            case CHOOSE_PLACEMENT:
                if (latestSelectedDie.getPlace().equals(Place.DRAFTPOOL)) {
                    if(latestOptionsList.get(0).equals(Actions.SET_SHADE)){
                        break;//todo properly higlight the selected die
                    }
                    highlightBlue(poolDice.get(latestSelectedDie.getPosition()), dieDim);
                }
                break;
            case SELECT_DIE:
                if (!latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.DRAFTPOOL)) {
                    if (!latestOptionsList.isEmpty() && (latestOptionsList.get(0).equals(Actions.SET_SHADE) || latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
                    } else {
                        for (IndexedCellContent activeCell : latestDiceList) {
                            Canvas c = poolDice.get(activeCell.getPosition());
                            highlight(c, dieDim);
                            c.setOnMouseClicked(e -> {
                                cmdWrite.write(poolDice.indexOf(c) + "");
                            });
                        }
                    }

                }

        }
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
        return pool;

    }

    //todo update
    public GridPane drawSchema(LightSchemaCard schema, double dieDim, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, List<Actions> latestOptionsList, int favortokens) {
        GridPane g = schemaToGrid(schema, dieDim * NUM_COLS, dieDim * NUM_ROWS, turnState, latestDiceList, latestPlacementsList, latestSelectedDie, latestOptionsList);
        return g;
    }

    public VBox getMenuButtons(ClientFSMState turnState) {
        Button endTurn = new Button("end turn");
        endTurn.setOnAction(e -> cmdWrite.write("e"));
        Button back = new Button("back");
        back.setOnAction(e -> cmdWrite.write("b"));
       /* Rectangle turnStateIndicator = new Rectangle(50, 50);
        if (turnState.equals(NOT_MY_TURN)) {
            turnStateIndicator.setFill(Color.RED);
        } else {
            turnStateIndicator.setFill(Color.GREEN);
        }*/
        Label turn = new Label(turnState.toString());
        VBox buttonContainer = new VBox();
        //buttonContainer.getChildren().addAll(back, endTurn, turnStateIndicator, turn);
        buttonContainer.getChildren().addAll(back, endTurn);
        return buttonContainer;
    }

    public GridPane schemaToGrid(LightSchemaCard lightSchemaCard, double width, double heigth, ClientFSMState turnState, List<IndexedCellContent> latestDiceList, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie, List<Actions> latestOptionsList) {
        double dieDim = width / NUM_COLS;
        ArrayList<Canvas> gridCells = new ArrayList<>();

        for (int i = 0; i < NUM_COLS * NUM_ROWS; i++) {
            Canvas cell;
            if (lightSchemaCard.hasConstraintAt(i)) {
                cell = lightConstraintToCanvas(lightSchemaCard.getConstraintAt(i), dieDim);
                gridCells.add(cell);
            } else if (lightSchemaCard.hasDieAt(i)) {  //todo change it with active cell object
                cell = lightDieToCanvas(lightSchemaCard.getDieAt(i), dieDim);
                gridCells.add(cell);
            } else {
                cell = whiteCanvas(dieDim);
                gridCells.add(cell);
            }
        }

        switch (turnState) {
            case CHOOSE_PLACEMENT:
                if (latestSelectedDie.getPlace().equals(Place.DRAFTPOOL) || !latestOptionsList.isEmpty() && latestOptionsList.get(0).equals(Actions.PLACE_DIE)) {
                    for (Canvas c : gridCells) {
                        if (latestPlacementsList.contains(gridCells.indexOf(c))) {
                            highlight(c, dieDim);
                            c.setOnMouseClicked(e -> cmdWrite.write(latestPlacementsList.indexOf(gridCells.indexOf(c)) + ""));
                        }
                    }
                }
                break;
            case SELECT_DIE:
                if (!latestDiceList.isEmpty() && latestDiceList.get(0).getPlace().equals(Place.SCHEMA)) {
                    for (IndexedCellContent activeCell : latestDiceList) {
                        Canvas c = gridCells.get(activeCell.getPosition());
                        highlight(c, dieDim);
                        c.setOnMouseClicked(e -> {
                            System.out.println("selected die at position " + gridCells.indexOf(c) + " in schema");
                            cmdWrite.write(latestDiceList.indexOf(activeCell) + "");
                        });
                    }
                }
                break;
        }
        GridPane grid = new GridPane();
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                grid.add(gridCells.get(row * NUM_COLS + col), col, row);
            }
        }
        return grid;
    }

    public VBox drawCards(LightCard privObj, List<LightCard> pubObjs, List<LightTool> tools, double cellDim, ClientFSMState turnState) {
        Button priv = new Button("Private Objective");
        Button pub = new Button("Public Objectives");
        Button tool = new Button("Tools");

        HBox buttonContainer = new HBox(priv, pub, tool);
        HBox cardContainer = new HBox();
        VBox primaryContainer = new VBox(buttonContainer, cardContainer);

        priv.setOnAction(e -> {
            Rectangle privObjImg = drawCard(privObj, getCardWidth(), getCardHeight());
            Rectangle emptyRect1 = new Rectangle(getCardWidth(), getCardHeight(),Color.TRANSPARENT);
            Rectangle emptyRect2 = new Rectangle(getCardWidth(), getCardHeight(),Color.TRANSPARENT);
            cardContainer.getChildren().setAll(privObjImg,emptyRect1,emptyRect2);
        });
        pub.setOnAction(e -> {
            ArrayList<Rectangle> cards = new ArrayList();
            for (LightCard pubObjCard : pubObjs) {
                cards.add(drawCard(pubObjCard, getCardWidth(), getCardHeight()));

            }
            cardContainer.getChildren().setAll(cards);
        });
        tool.setOnAction(e1 -> {
            ArrayList<Rectangle> cards = new ArrayList();
            for (LightCard toolCard : tools) {
               Rectangle toolRect = drawCard(toolCard, getCardWidth(), getCardHeight());
                toolRect.setOnMouseClicked(e2 -> {
                    if (turnState.equals(MAIN)) {
                        cmdWrite.write("0");
                        cmdWrite.write(tools.indexOf(toolCard) + "");
                    }
                });
                cards.add(toolRect);
            }
            cardContainer.getChildren().setAll(cards);
        });
        tool.fire();
        return primaryContainer;

    }

    private Rectangle drawCard(LightCard card, double imageWidth, double imageHeight) {
        Image image = new Image(card.getImgSrc()+".png");
        Rectangle imgRect = new Rectangle(imageWidth, imageHeight);
        ImagePattern imagePattern = new ImagePattern(image);
        imgRect.setFill(imagePattern);
        return imgRect;
    }

    private void highlight(Canvas cell, double cellDim) {
        GraphicsContext gc = cell.getGraphicsContext2D();
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.strokeRect(0, 0, cellDim, cellDim);
    }

    private void highlightBlue(Canvas cell, double cellDim) {
        GraphicsContext gc = cell.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.strokeRect(0, 0, cellDim, cellDim);
    }


    private void highlight(StackPane p) {
        highlight((Canvas) p.getChildren().get(1), 100);
    }

    private Canvas whiteCanvas(double dim) {
        Canvas whiteCanvas = new Canvas(dim, dim);
        GraphicsContext gc = whiteCanvas.getGraphicsContext2D();
        drawWhiteCell(gc, 0, 0, dim);
        return whiteCanvas;
    }

    private Canvas indexedCellToCanvas(CellContent cellContent, double dieDim) {
        Canvas canvas = new Canvas(dieDim, dieDim);
        if (cellContent.isDie()) {
            drawDie(cellContent.getColor(), cellContent.getShade(), canvas.getGraphicsContext2D(), 0, 0, dieDim);
        } else {
            drawConstraint(cellContent, canvas.getGraphicsContext2D(), 0, 0, dieDim);
        }
        return canvas;
    }


    private Canvas lightDieToCanvas(LightDie die, double dieDim) {
        Canvas dieCanvas = new Canvas(dieDim, dieDim);
        drawDie(die, dieCanvas.getGraphicsContext2D(), dieDim);
        return dieCanvas;
    }

    private Canvas lightConstraintToCanvas(LightConstraint constraint, double dieDim) {
        Canvas dieCanvas = new Canvas(dieDim, dieDim);
        drawConstraint(constraint, dieCanvas.getGraphicsContext2D(), 0, 0, dieDim);
        return dieCanvas;
    }

    public Scene waitingForGameStartScene(String message) {
        Text waitingText = new Text(message);
        StackPane p = new StackPane(waitingText);
        return new Scene(p);
    }

    public BorderPane buildSelectdPlayerPane(int playerId, int width, int height, LightBoard board){
        BorderPane selectedPlayerPane = new BorderPane();
        HBox playersSelector = getPlayersSelector(board);
        Region spacer = new Region();
        HBox.setHgrow(spacer,Priority.ALWAYS);
        HBox bottomContainer = new HBox(playersSelector,spacer);
        selectedPlayerPane.setBottom(bottomContainer);

        selectedPlayerPane.setRight(new Rectangle(0,0,getCardWidth()*3,1));

        double cellDim = getMainSceneCellDim(width,height);
        selectedPlayerPane.setTop(new Rectangle(cellDim,cellDim,Color.TRANSPARENT));
        Canvas playerSchema = new Canvas(cellDim*NUM_COLS,cellDim*NUM_ROWS);
        drawSchema(playerSchema.getGraphicsContext2D(),board.getPlayerById(playerId).getSchema(),0,0,cellDim);
        StackPane schemaContainer = new StackPane(playerSchema);
        selectedPlayerPane.setCenter(schemaContainer);
        schemaContainer.setAlignment(Pos.CENTER);
        //selectedPlayerPane.getCenter().setStyle("-fx-background-color: rgb(255,255,255,0.2);");
        Event mouseExited = new MyEvent(MOUSE_EXITED_BACK_PANE);
        selectedPlayerPane.getCenter().setOnMouseExited(e->selectedPlayerPane.fireEvent(mouseExited));
        return selectedPlayerPane;
    }

    public HBox getPlayersSelector(LightBoard board) {
        HBox playerSelector = new HBox();
        for(int playerId = 0; playerId<board.getNumPlayers();playerId++){
            Group playerStatusBar = playerStatusBar(playerId,board.getPlayerById(playerId).getUsername(),board.getPlayerById(playerId).getStatus(),board.getNowPlaying());
            playerSelector.getChildren().add(playerStatusBar);
            if(playerId == board.getMyPlayerId()){
                continue;
            }else{
                Event mouseEnteredPlayerStatusBar = new MyEvent(SELECTED_PLAYER, playerId);
                playerStatusBar.setOnMouseEntered(e -> playerStatusBar.fireEvent(mouseEnteredPlayerStatusBar));
            }
        }
        playerSelector.setAlignment(Pos.BOTTOM_LEFT);
        return  playerSelector;
    }

    private Group playerStatusBar(int playerId, String username, LightPlayerStatus status, int nowPlaying){
        double width = 100; //todo update dynamically
        double heigth = 20;
        Text playerName = new Text(username);
        playerName.setFont(Font.font("Serif", 25));
        Circle statusCircle = new Circle(10);
        if(playerId == nowPlaying ){
            statusCircle.setFill(Color.GREEN);
        }else if(status.equals(LightPlayerStatus.DISCONNECTED) || status.equals(LightPlayerStatus.QUITTED)){
            statusCircle.setFill(Color.RED);
        }else{
            statusCircle.setFill(Color.GRAY);
        }
        HBox statusAndName= new HBox(statusCircle,playerName);
        statusAndName.setAlignment(Pos.CENTER);
        playerName.setTextAlignment(TextAlignment.CENTER);
        statusAndName.setStyle("-fx-background-color: rgb(125,125,125,0.3);");
        StackPane p = new StackPane(statusAndName);
        return new Group(p);
    }

    //todo delete class
    //just a class to avoid having repeated code
    private class DraftedSchemasWindowDim {
        double sceneWidth;
        double sceneHeight;
        double drawingHeight;
        double drawingWidth;
        double x;
        double y;

        private DraftedSchemasWindowDim(double sceneWidth, double sceneHeight) {
            double sceneRatio = sceneWidth / sceneHeight;
            if (sceneRatio >= DRAFTED_CANVAS_SCENE_RATIO) {
                drawingHeight = sceneHeight;
                drawingWidth = getDraftedSchemasWidth(drawingHeight);
                x = (sceneWidth - drawingWidth) / 2;
            } else {
                drawingWidth = sceneWidth;
                drawingHeight = getDraftedSchemasHeight(drawingWidth);
                y = (sceneHeight - drawingHeight) / 2;
            }
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        double getDrawingWidth() {
            return drawingWidth;
        }

        double getDrawingHeight() {
            return drawingHeight;
        }

    }

    public List<Rectangle> draftedMouseActionAreas(double sceneWidth, double sceneHeight) {
        DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth, sceneHeight);
        double x = sizes.getX();
        double y = sizes.getY();
        double drawingWidth = sizes.getDrawingWidth();
        double drawingHeight;
        double schemaWidth = drawingWidth * SCHEMA_W_TO_DRAFTED_W;
        double schemaHeight = schemaWidth / COMPLETE_SCHEMA_RATIO;
        double extPadding = schemaWidth / SCHEMA_W_TO_EXTERNAL_PADDING;
        double intPadding = schemaWidth / SCHEMA_W_TO_INTERNAL_PADDING;

        Rectangle r0 = new Rectangle(x + extPadding, y + extPadding, schemaWidth, schemaHeight);
        Rectangle r1 = new Rectangle(x + extPadding + schemaWidth + intPadding, y + extPadding, schemaWidth, schemaHeight);
        Rectangle r2 = new Rectangle(x + extPadding, y + extPadding + schemaHeight + intPadding, schemaWidth, schemaHeight);
        Rectangle r3 = new Rectangle(x + extPadding + schemaWidth + intPadding, y + extPadding + intPadding + schemaHeight, schemaWidth, schemaHeight);

        ArrayList<Rectangle> rects = new ArrayList<>();
        rects.add(r0);
        rects.add(r1);
        rects.add(r2);
        rects.add(r3);
        return rects;
    }


    public void drawDraftedSchemas(List<LightSchemaCard> lightSchemaCard, LightPrivObj privObj, Canvas canvas, double sceneWidth, double sceneHeight) {
        DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth, sceneHeight);
        double x = sizes.getX();
        double y = sizes.getY();
        double drawingWidth = sizes.getDrawingWidth();
        double drawingHeight = sizes.getDrawingHeight();
        double schemaWidth = drawingWidth * SCHEMA_W_TO_DRAFTED_W;
        double schemaHeight = schemaWidth / COMPLETE_SCHEMA_RATIO;
        double extPadding = schemaWidth / SCHEMA_W_TO_EXTERNAL_PADDING;
        double intPadding = schemaWidth / SCHEMA_W_TO_INTERNAL_PADDING;

        double privObjX = x + extPadding + schemaWidth + intPadding + schemaWidth + extPadding;
        double privObjY = y + extPadding;
        double privObjWidth = schemaWidth / SCHEMA_W_TO_PRIVOBJ_W;
        double privObjHeight = privObjWidth / PRIVATE_OBJ_RATIO;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        //clean the canvas
        gc.clearRect(0, 0, sceneWidth, sceneHeight);

       //drawCard(privObj, gc, privObjX, privObjY, privObjWidth, privObjHeight); //todo reimplemet
        drawCompleteSchema(gc, lightSchemaCard.get(0), x + extPadding, y + extPadding, schemaWidth, schemaHeight);
        drawCompleteSchema(gc, lightSchemaCard.get(1), x + extPadding + schemaWidth + intPadding, y + extPadding, schemaWidth, schemaHeight);
        drawCompleteSchema(gc, lightSchemaCard.get(2), x + extPadding, y + extPadding + schemaHeight + intPadding, schemaWidth, schemaHeight);
        drawCompleteSchema(gc, lightSchemaCard.get(3), x + extPadding + schemaWidth + intPadding, y + extPadding + intPadding + schemaHeight, schemaWidth, schemaHeight);

        //todo delete
        gc.setLineWidth(1);
        gc.setStroke(Color.RED);
        gc.strokeRect(x, y, drawingWidth, drawingHeight);
        gc.strokeLine(0, 0, sceneWidth, sceneHeight);
        gc.strokeLine(0, sceneHeight, sceneWidth, 0);

    }

    private void drawCompleteSchema(GraphicsContext gc, LightSchemaCard lightSchemaCard, double x, double y, double schemaWidth, double schemaHeight) {
        double initialX = x;
        double initialY = y;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, schemaWidth, schemaHeight, SCHEMA_ARC_TO_WIDTH * schemaWidth, SCHEMA_ARC_TO_WIDTH * schemaWidth);
        x = x + SCHEMA_LINE_TO_WIDTH * schemaWidth;
        y = y + SCHEMA_LINE_TO_WIDTH * schemaWidth;
        double cellDim = CELL_TO_SCHEMA_W * schemaWidth;
        drawSchema(gc, lightSchemaCard, x, y, cellDim);
        int textLen = lightSchemaCard.getName().length();
        x = initialX + schemaWidth / 2;
        y = initialY + TEXT_HEIGHT_TO_SCHEMA_H * schemaHeight;
        drawSchemaText(gc, x, y, schemaWidth, lightSchemaCard);
        drawFavorTokens(gc, initialX, y, schemaWidth, lightSchemaCard);

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

    public void drawSchemaText(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        double textSize = TEXT_DIM_TO_SCHEMA_W * schemaWidth;
        gc.setFont(Font.font("Serif", textSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.AZURE);
        gc.fillText(lightSchemaCard.getName(), x, y);
    }


    private void drawSchema(GraphicsContext gc, LightSchemaCard lightSchemaCard, double x, double y, double cellDim) {
        double initX = x;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (lightSchemaCard.hasDieAt(i, j)) {
                    drawDie(lightSchemaCard.getDieAt(i, j), gc, x, y, cellDim);
                } else if (lightSchemaCard.hasConstraintAt(i, j)) {
                    drawConstraint(lightSchemaCard.getConstraintAt(i, j), gc, x, y, cellDim);
                } else {
                    drawWhiteCell(gc, x, y, cellDim);
                }
                x += cellDim;
            }
            x = initX;
            y += cellDim;
        }
    }

    private void drawWhiteCell(GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.fillRect(x, y, cellDim, cellDim);
        gc.strokeRect(x, y, cellDim, cellDim);
    }

    private void drawConstraint(CellContent cell, GraphicsContext gc, double x, double y, double cellDim) {
        if (cell.isDie()) {
            return;
        } else {
            if (cell.hasColor()) {
                drawColorConstraint(cell.getColor(), gc, x, y, cellDim);
            } else {
                drawShadeConstraint(cell.getShade(), gc, x, y, cellDim);
            }
        }
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double cellDim) {
        drawConstraint(constraint, gc, 0, 0, cellDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double cellDim) {
        if (constraint.hasColor()) {
            drawColorConstraint(constraint.getColor(), gc, x, y, cellDim);
        } else {
            drawShadeConstraint(constraint.getShade(), gc, x, y, cellDim);
        }
    }

    private void drawShadeConstraint(Shade shade, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, cellDim, cellDim);
        drawConstraintSpots(gc, x, y, cellDim, shade.toInt());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.strokeRect(x, y, cellDim, cellDim);
    }

    private void drawColorConstraint(it.polimi.ingsw.common.enums.Color color, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXConstraintColor(color));
        gc.fillRect(x, y, cellDim, cellDim);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim * LINE_TO_CELL);
        gc.strokeRect(x, y, cellDim, cellDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext graphicsContext2D, double dieDim) {
        drawDie(lightDie, graphicsContext2D, 0, 0, dieDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        gc.fillRoundRect(x + lineWidth, y + lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, lightDie.getShade().toInt());
    }

    private void drawDie(it.polimi.ingsw.common.enums.Color color, Shade shade, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(color));
        gc.fillRoundRect(x + lineWidth, y + lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, shade.toInt());
    }

    private void drawSpots(GraphicsContext gc, double dieDim, int count) {
        drawSpots(gc, 0, 0, dieDim, count);
    }

    private void drawSpots(GraphicsContext gc, double xAxisDiePosition, double Y_axis_die_position, double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpots(GraphicsContext gc, double dieDim, int count) {
        drawConstraintSpots(gc, 0, 0, dieDim, count);
    }

    private void drawConstraintSpots(GraphicsContext gc, double xAxisDiePosition, double Y_axis_die_position, double dieDim, int count) {
        switch (count) {
            case 1:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim) {
        drawConstraintSpot(gc, x, y, dieDim, 0, 0);
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(spotDiameter / 5);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
        gc.strokeOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }

    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        drawSpot(gc, x, y, dieDim, 0, 0);
    }

    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }

    public BorderPane bulidBackPaneOptions(double width, double height, LightBoard board) {
        BorderPane backPane = new BorderPane();
        HBox optionBox = new HBox();
        List<IndexedCellContent> latestDiceList = board.getLatestDiceList();
        double cellDim = getMainSceneCellDim(width, height);
        for (IndexedCellContent selectableDie : latestDiceList) {
            Canvas c = indexedCellToCanvas(selectableDie.getContent(), cellDim);
            c.setOnMouseClicked(e -> {
                cmdWrite.write(latestDiceList.indexOf(selectableDie) + "");
                Event exitBackPane = new MyEvent(MOUSE_EXITED_BACK_PANE);
                c.fireEvent(exitBackPane);
            });
            highlight(c, cellDim);
            optionBox.getChildren().add(c);
        }
        backPane.setStyle("-fx-background-color: rgb(255,255,255,0.4);"); //todo hookup with css
        backPane.setCenter(optionBox);
        return backPane;
    }
}