package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.clientController.CmdWriter;
import it.polimi.ingsw.client.clientController.QueuedCmdReader;
import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.textGen;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.Cell;
import it.polimi.ingsw.client.view.clientUI.uielements.GUIutil;
import it.polimi.ingsw.client.view.clientUI.uielements.MyEvent;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.IndexedCellContent;
import it.polimi.ingsw.common.serializables.LightDie;
import it.polimi.ingsw.common.serializables.LightPrivObj;
import it.polimi.ingsw.common.serializables.LightSchemaCard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static it.polimi.ingsw.client.view.clientUI.uielements.MyEvent.MOUSE_EXITED_BACK_PANE;
import static it.polimi.ingsw.client.view.clientUI.uielements.MyEvent.SELECTED_PLAYER;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg.*;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_LEFT;

public class GUI extends Application implements ClientUI {
    private GUIutil sceneCreator;
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private static Client client;
    private static UIMessages uimsg;
    private Stage primaryStage;
    private static GUI instance;
    private static final Object lock = new Object();
    private Text messageToUser = new Text();
    private CmdWriter cmdWrite;
    private int playerId;
    private static Scene mainScene;

    public GUI() {
        instance = this;
    }

    public static GUI getGUI() {
        return instance;
    }


    public static void launch(Client client, UILanguage lang) {
        GUI.client = client;
        GUI.uimsg = new UIMessages(lang);
        Application.launch(GUI.class);
    }

    @Override
    public void start(Stage primaryStage) {
        //get the dimensions of the screen
        synchronized (lock) {
            sceneCreator = new GUIutil(Screen.getPrimary().getVisualBounds(), this, getCmdWrite() ,uimsg);
            lock.notifyAll();
        }
        this.primaryStage = primaryStage;
    }


    @Override
    public void updateConnectionOk() {

    }

    @Override
    public void showLoginScreen() {
        Platform.runLater(() -> {
        GridPane grid = new GridPane();
        grid.setAlignment(CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Sagrada");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        Label username = new Label("Username:");
        grid.add(username, 0, 1);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setText(textGen.getRandomString()); //TODO delete
        grid.add(usernameField, 1, 1);
        Label password = new Label("Password:");
        grid.add(password, 0, 2);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setText(textGen.getRandomString()); //TODO delete
        grid.add(passwordField, 1, 2);
        Button button = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(button);
        grid.add(hbBtn, 1, 4);
        messageToUser.setFont(new Font(10));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(grid,messageToUser);
        synchronized (lock) {
            while (sceneCreator == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
            Scene loginScene = new Scene(vbox, sceneCreator.getLoginWidth(), sceneCreator.getLoginWidth());

            button.setOnAction(e -> {
                client.setUsername(usernameField.getText());
                client.setPassword(Credentials.hash(client.getUsername(), passwordField.getText().toCharArray()));
        });
        usernameField.addEventHandler(KeyEvent.ANY, e->button.fire()); //delete
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e->client.quit());
        primaryStage.sizeToScene();
        primaryStage.show();
        });
    }

    @Override
    public void updateLogin(boolean logged) {
        if (logged) {
            Platform.runLater(() -> {
                messageToUser.setFill(Color.GREEN);
                messageToUser.setText(String.format(uimsg.getMessage(LOGIN_OK),client.getUsername()));
            });
        } else {
            Platform.runLater(() -> {
                messageToUser.setFill(Color.FIREBRICK);
                messageToUser.setText(uimsg.getMessage(LOGIN_KO));
            });
        }
    }

    @Override
    public void showLatestScreen() {

    }

    @Override
    public void updateLobby(int numUsers) {
        Platform.runLater(() -> {
            messageToUser.setFill(Color.GREEN);
            /* TODO add other text field */
           // messageToUser.setText("lobby "+numUsers);
            messageToUser.setText(String.format(uimsg.getMessage(LOBBY_UPDATE),numUsers));
        });

    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        Platform.runLater(() -> {
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);
            DraftedSchemasGroup draftedSchemasGroup = new DraftedSchemasGroup(draftedSchemas,privObj);
            stackPane.getChildren().add(draftedSchemasGroup);
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                draftedSchemasGroup.updateScene(newWidth,newHeight);
            });
            scene.heightProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                draftedSchemasGroup.updateScene(newWidth,newHeight);
            });
            primaryStage.setTitle("Sagrada");
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(sceneCreator.getDraftedSchemasMinHeight());
            primaryStage.setMinWidth(sceneCreator.getDraftedSchemasMinWidth());

        });
    }

    public void updateBoard(LightBoard board) {
        Platform.runLater(() -> {
            if (board == null) {
                throw new IllegalArgumentException();
            }
            switch (client.getTurnState()){
                case CHOOSE_SCHEMA:
                    System.out.println("choose----------------------------------------------------------------");
                    break;
                case NOT_MY_TURN:
                    System.out.println("not my-----------------------------------------------------------------");
                    break;
                case MAIN:
                    System.out.println("main-------------------------------------------------------------------");
                    break;
                case SELECT_DIE:
                    System.out.println("select die-------------------------------------------------------------");
                    break;
                case CHOOSE_OPTION:
                    if(board.getLatestOptionsList().size()>1){
                        System.out.println("choose option------------------------------------------------------");
                    }
                    break;
                case CHOOSE_TOOL:
                    System.out.println("choose tool-------------------------------------------------------------");
                    break;
                case CHOOSE_PLACEMENT:
                    System.out.println("choose placement--------------------------------------------------------");
                    break;
                case TOOL_CAN_CONTINUE:
                    System.out.println("tool can continue------------------------------------------------------------------");
                    break;
            }
            primaryStage.setMinWidth(sceneCreator.getGameSceneMinWidth());
            primaryStage.setMinHeight(sceneCreator.getGameSceneMinHeight());
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            if(mainScene == null){
                mainScene = new Scene(bulidMainPane(currentWidth,currentHeight,board));
                primaryStage.setScene(mainScene);
            }else{
                mainScene.setRoot(bulidMainPane(currentWidth,currentHeight,board));
            }



            //root.setStyle("-fx-background-color: black;"); //todo change
            mainScene.addEventHandler(MOUSE_EXITED_BACK_PANE, e->mainScene.setRoot(buildFrontPane(mainScene.getWidth(), mainScene.getHeight(),board)));
            mainScene.addEventHandler(MyEvent.MOUSE_ENTERED_MULTIPLE_DICE_CELL, e ->mainScene.setRoot(showMultipleDiceScreen(e.getCellIndex(),mainScene.getWidth(), mainScene.getHeight(),board)));
            mainScene.addEventHandler(SELECTED_PLAYER, e -> mainScene.setRoot(showSelectedPlayer(e.getCellIndex(),mainScene.getWidth(), mainScene.getHeight(),board)));

            mainScene.widthProperty().addListener((observable, oldValue, newValue) -> mainScene.setRoot(bulidMainPane(mainScene.getWidth(),mainScene.getHeight(),board)));
            mainScene.heightProperty().addListener((observable, oldValue, newValue) -> mainScene.setRoot(bulidMainPane(mainScene.getWidth(),mainScene.getHeight(),board)));
        });
    }

    private Parent showSelectedPlayer(int playerId, double width, double height, LightBoard board) {
        BorderPane frontPane = buildFrontPane(width,height,board);
        BorderPane backPane = sceneCreator.buildSelectdPlayerPane(playerId,width, height, board);
        StackPane p = new StackPane(backPane,frontPane);
        backPane.toFront();
        return p;
    }

    StackPane showMultipleDiceScreen(int selectedTrackCellIndex, double newWidth, double newHeight, LightBoard board){
        BorderPane frontPane = buildFrontPane(newWidth,newHeight,board);
        BorderPane backPane = bulidBackPaneRoundTrack(selectedTrackCellIndex,newWidth,newHeight,board);
        StackPane p = new StackPane(backPane,frontPane);
        backPane.toFront();
        return p;
    }
    StackPane bulidOptionScreen(double newWidth, double newHeight, LightBoard board){
        BorderPane frontPane = buildFrontPane(newWidth,newHeight,board);
        BorderPane backPane = sceneCreator.bulidBackPaneOptions(newWidth,newHeight,board);
        StackPane p = new StackPane(backPane,frontPane);
        backPane.toFront();
        return p;
    }

    StackPane bulidMainPane(double newWidth, double newHeight, LightBoard board){
        BorderPane frontPane = buildFrontPane(newWidth,newHeight,board);
        List <Actions> latestOptionsList = board.getLatestOptionsList();
        StackPane p = new StackPane(frontPane);
        if (client.getTurnState().equals(ClientFSMState.SELECT_DIE) && !latestOptionsList.isEmpty() && (latestOptionsList.get(0).equals(Actions.SET_SHADE) || latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
            BorderPane backPane = sceneCreator.bulidBackPaneOptions(newWidth,newHeight,board);
            p.getChildren().add(backPane);
        }
        return p;
    }

    private BorderPane bulidBackPaneRoundTrack(int selectedTrackCellIndex, double newWidth, double newHeight, LightBoard board){
        double                      cellDim = sceneCreator.getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrack = board.getRoundTrack();
        List <LightDie> draftPool = board.getDraftPool();
        List <IndexedCellContent>   latestDiceList = board.getLatestDiceList();
        List <Integer>              latestPlacementsList = board.getLatestPlacementsList();
        IndexedCellContent          latestSelectedDie = board.getLatestSelectedDie();
        List <Actions>              latestOptionsList = board.getLatestOptionsList();
        LightSchemaCard             schemaCard = board.getPlayerById(playerId).getSchema();
        int favorTokens =           board.getPlayerById(board.getMyPlayerId()).getFavorTokens();
        ClientFSMState              turnState = client.getTurnState();

        BorderPane backPane = new BorderPane();
        HBox d1 = sceneCreator.buildDummyTrack(cellDim,roundTrack,turnState,latestDiceList,latestPlacementsList,latestSelectedDie,favorTokens);
        HBox d2 = sceneCreator.buildMultipleDiceBar(cellDim,selectedTrackCellIndex,roundTrack,turnState,latestDiceList,latestPlacementsList,latestSelectedDie,favorTokens);
        VBox vbox =new VBox(d1,d2);
        vbox.setSpacing(10); //todo make dynamic?
        Event mouseExited = new MyEvent(MOUSE_EXITED_BACK_PANE);
        vbox.setOnMouseExited(e->vbox.fireEvent(mouseExited));
        backPane.setTop(vbox);
        vbox.setAlignment(TOP_LEFT);
        backPane.setStyle("-fx-background-color: rgb(255,255,255,0.4);"); //todo hookup with css
        return backPane;
    }

    private BorderPane buildFrontPane(double newWidth, double newHeight, LightBoard board){
        double                      cellDim = sceneCreator.getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrackList = board.getRoundTrack();
        List <LightDie> draftPool = board.getDraftPool();
        List <IndexedCellContent>   latestDiceList = board.getLatestDiceList();
        List <Integer>              latestPlacementsList = board.getLatestPlacementsList();
        IndexedCellContent          latestSelectedDie = board.getLatestSelectedDie();
        List <Actions>              latestOptionsList = board.getLatestOptionsList();
        LightSchemaCard             schemaCard = board.getPlayerById(playerId).getSchema();
        int favorTokens =           board.getPlayerById(board.getMyPlayerId()).getFavorTokens();
        ClientFSMState              turnState = client.getTurnState();

        BorderPane frontPane = new BorderPane();

        ArrayList<Cell> draftPoolCells = sceneCreator.getDraftPoolCells(draftPool,cellDim);
        ArrayList<Cell> schemaCells = sceneCreator.getSchemaCells(schemaCard,cellDim);
        ArrayList<Cell> roundTrackCells = sceneCreator.getRoundTrackCells(roundTrackList,turnState,latestDiceList,cellDim);
        sceneCreator.addActionListeners(draftPoolCells,schemaCells,roundTrackCells,turnState,board,cellDim);
        frontPane.setStyle("-fx-background-color: rgba(245,220,112,0.4);"); //todo hookup with css



        //Top side of the border pane
        //todo update
        HBox roundTrack = sceneCreator.buildRoundTrack(roundTrackCells);
        Region separator = new Region();
        HBox.setHgrow(separator,Priority.ALWAYS);
        VBox menuButtons = sceneCreator.getMenuButtons(turnState);
        roundTrack.getChildren().addAll(separator,menuButtons);
        frontPane.setTop(roundTrack);

        //Center side of the border pane
        //todo update
        Group schema = sceneCreator.buildSchema(schemaCells,favorTokens,cellDim);
        HBox playersSelector = sceneCreator.getPlayersStatusBar(board.getMyPlayerId(),board);
        StackPane schemaContainer = new StackPane(schema);
        VBox.setVgrow(schemaContainer,Priority.ALWAYS);
        frontPane.setCenter(new VBox(schemaContainer,playersSelector));

        //Right side of the border pane
        VBox cards = sceneCreator.drawCards(board.getPrivObj(),board.getPubObjs(),board.getTools(),cellDim,turnState);
        StackPane cardsContainer = new StackPane(cards);
        cards.setAlignment(CENTER);
        //todo update
        GridPane draftpool = sceneCreator.buildDraftPool(draftPoolCells);
        VBox.setVgrow(cardsContainer,Priority.ALWAYS);
        frontPane.setRight(new VBox(cardsContainer,draftpool));
        return frontPane;
    }

    @Override
    public void updateConnectionClosed() {

    }

    @Override
    public void updateConnectionBroken() {

    }


    @Override
    public void showWaitingForGameStartScreen() {
        Platform.runLater(() -> {
            String message = String.format("%s%n", uimsg.getMessage(WAIT_FOR_GAME_START));
            primaryStage.setScene(sceneCreator.waitingForGameStartScene(message));
        });

    }

    //todo delete class
    class DraftedSchemasGroup extends Group{
        Canvas canvas;
        Pane mouseActionPane;
        List<LightSchemaCard> draftedSchemas;
        LightPrivObj privObj;

        public DraftedSchemasGroup(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
            this.canvas = new Canvas();
            this.mouseActionPane = new Pane();
            this.draftedSchemas = draftedSchemas;
            this.privObj = privObj;
            this.getChildren().addAll(canvas,mouseActionPane);
        }

        private void updateScene(double width, double height){
            //update the width and height properties
            canvas.setWidth(width);
            canvas.setHeight(height);
            double borderLineWidth = sceneCreator.getSelectedSchemaLineWidth(width,height);
            sceneCreator.drawDraftedSchemas(draftedSchemas,privObj,canvas,width,height);
            List<Rectangle> actionRects = sceneCreator.draftedMouseActionAreas(width,height);
            setDraftedSchemasAction(actionRects,borderLineWidth);
            mouseActionPane.getChildren().setAll(actionRects);
        }

        private void setDraftedSchemasAction(List<Rectangle> actionRects, double borderLineWidth){
            for (Rectangle r : actionRects) {
                r.setFill(Color.TRANSPARENT);
                r.setOnMouseEntered(e->r.setFill(Color.rgb(0,0,0,0.4)));
                r.setOnMouseExited(e->r.setFill(Color.TRANSPARENT));
                r.setOnMouseClicked(e->{
                    cmdWrite.write(actionRects.indexOf(r)+"");
                    r.setStroke(Color.BLUE);
                    r.setStrokeWidth(borderLineWidth);
                    showWaitingForGameStartScreen();
                });
            }
        }
    }

    @Override
    public QueuedReader getCommandQueue() {
        if (cmdWrite == null) {
            cmdWrite = new QueuedCmdReader();
        }
        return (QueuedReader) cmdWrite;
    }

    public CmdWriter getCmdWrite(){
        if (cmdWrite == null) {
            cmdWrite = new QueuedCmdReader();
        }
        return cmdWrite;
    }


    @Override
    public void update(Observable o, Object arg) {
        updateBoard((LightBoard) o);
    }
}