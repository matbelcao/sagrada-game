package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.controller.CmdWriter;
import it.polimi.ingsw.client.controller.QueuedCmdReader;
import it.polimi.ingsw.client.controller.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.textGen;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.DieContainer;
import it.polimi.ingsw.client.view.clientUI.uielements.GUIutil;
import it.polimi.ingsw.client.view.clientUI.uielements.SizeListener;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.IndexedCellContent;
import it.polimi.ingsw.common.serializables.LightDie;
import it.polimi.ingsw.common.serializables.LightPrivObj;
import it.polimi.ingsw.common.serializables.LightSchemaCard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;
import java.util.Observable;

import static it.polimi.ingsw.client.view.clientUI.uielements.CustomGuiEvent.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg.*;
import static javafx.geometry.Pos.CENTER;

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
    private int playerId;// TODO: 03/07/2018
    private SizeListener sizeListener;
    private LightBoard tempBoard;

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
        sizeListener = new SizeListener(this);

    }


    @Override
    public void updateConnectionOk() { messageToUser.setText(uimsg.getMessage(UIMsg.CONNECTION_OK)); }

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
                    Thread.currentThread().interrupt();
                }
            }
        }
            Scene loginScene = new Scene(vbox, sceneCreator.getLoginWidth(), sceneCreator.getLoginWidth());

            button.setOnAction(e -> {
                client.setUsername(usernameField.getText());
                client.setPassword(Credentials.hash(client.getUsername(), passwordField.getText().toCharArray()));
        });
       usernameField.addEventHandler(KeyEvent.ANY, e->button.fire()); //todo delete
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e->client.quit());
        primaryStage.sizeToScene();

        //disable the size listener because login is non resizable
        sizeListener.disable();
        //assign the size listener to the stage
        primaryStage.widthProperty().addListener(sizeListener);
        primaryStage.heightProperty().addListener(sizeListener);
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
    public void showLatestScreen() {/*this method is useful only to CLI*/}

    @Override //TODO make another scene
    public void updateLobby(int numUsers) {
        Platform.runLater(() -> {
            sizeListener.disable();
            primaryStage.getScene().setRoot(buildLobbyPane(numUsers));
            messageToUser.setFill(Color.GREEN);
        });

    }

    private StackPane buildLobbyPane(int numUsers) {
        StackPane p = new StackPane();
        messageToUser.setText(String.format(uimsg.getMessage(LOBBY_UPDATE),numUsers));
        Label lobbyLabel = new Label(String.format(uimsg.getMessage(LOBBY_UPDATE),numUsers));
        p.getChildren().add(lobbyLabel);
        return new StackPane(p);
    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        Platform.runLater(() -> {
            primaryStage.setTitle("Sagrada");
            primaryStage.setResizable(true);
            double minWidt = sceneCreator.getDraftedSchemasMinWidth();
            double minHeight = sceneCreator.getDraftedSchemasMinHeight();
            Scene draftedSchemaScene = primaryStage.getScene();
            draftedSchemaScene.setRoot(sceneCreator.buildDraftedSchemasPane(draftedSchemas,privObj,minWidt, minHeight));
            primaryStage.setMinHeight(minHeight);
            primaryStage.setMinWidth(minWidt);


            //OLD VERSION
            /*draftedSchemaScene.widthProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = draftedSchemaScene.getWidth();
                double newHeight = draftedSchemaScene.getHeight();
                draftedSchemaScene.setRoot(sceneCreator.buildDraftedSchemasPane(draftedSchemas, privObj, newWidth,newHeight));
            });
            draftedSchemaScene.heightProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = draftedSchemaScene.getWidth();
                double newHeight = draftedSchemaScene.getHeight();
                draftedSchemaScene.setRoot(sceneCreator.buildDraftedSchemasPane(draftedSchemas, privObj, newWidth,newHeight));
            });*/

        });
    }

    public void updateBoard(LightBoard board) {
        tempBoard=board;
        Platform.runLater(() -> {
            if (board == null) {
                throw new IllegalArgumentException();
            }
            switch (client.getFsmState()){
                case CHOOSE_SCHEMA:
                    System.out.println("choose----------------------------------------------------------------");
                    break;
                case SCHEMA_CHOSEN:
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
                case GAME_ENDED:
                    System.out.println("game ended------------------------------------------------------------------");
                    break;
            }
            primaryStage.setMinWidth(sceneCreator.getGameSceneMinWidth());
            primaryStage.setMinHeight(sceneCreator.getGameSceneMinHeight());
            //double currentWidth = primaryStage.getWidth();
            //double currentHeight = primaryStage.getHeight();
           // primaryStage.getScene().setRoot(bulidMainPane(currentWidth,currentHeight));

            /*if(mainScene == null){
                mainScene = new Scene(bulidMainPane(currentWidth,currentHeight,board));
                primaryStage.setScene(mainScene);
            }else{
                mainScene.setRoot(bulidMainPane(currentWidth,currentHeight,board));
            }*/
           // primaryStage.addEventFilter(Event.ANY, e->System.out.println(e));
                sizeListener.enable();
                sizeListener.purgeTimer();
                drawMainGameScene();

                //primaryStage.widthProperty().addListener(sizeListener);
                //primaryStage.heightProperty().addListener(sizeListener);

            });
    }

    public void drawMainGameScene(){
        primaryStage.getScene().setRoot(bulidMainPane(primaryStage.getWidth(), primaryStage.getHeight()));
    }

    private StackPane bulidMainPane(double newWidth, double newHeight){
        StackPane p = new StackPane();
        if(client.getFsmState().equals(ClientFSMState.GAME_ENDED)){
            BorderPane gameEndedPane = sceneCreator.buildGameEndedPane(newWidth,newHeight,tempBoard.sortFinalPositions());
            p.getChildren().add(gameEndedPane);
        }else{
            BorderPane frontPane = buildFrontPane(newWidth,newHeight,tempBoard);
            List <Actions> latestOptionsList = tempBoard.getLatestOptionsList();
            ClientFSMState turnState = client.getFsmState();
            p.getChildren().add(frontPane);
            //the pane listens for custom events to know when it has to which layer
            p.addEventFilter(MOUSE_ENTERED_MULTIPLE_DICE_CELL, e -> p.getChildren().setAll(frontPane, sceneCreator.showMultipleDiceRoundTrack(e.getEventObjectIndex(),newWidth,newHeight,tempBoard,turnState)));
            p.addEventHandler(SELECTED_PLAYER, e -> {
                p.getChildren().setAll(frontPane,sceneCreator.buildSelectdPlayerPane(e.getEventObjectIndex(),newWidth, newHeight,tempBoard)); //todo create everything at once? note that two events are executed
                System.out.println("selected player");
            });
            p.addEventHandler(MOUSE_EXITED_BACK_PANE, e->frontPane.toFront());


            if (client.getFsmState().equals(ClientFSMState.SELECT_DIE) && !latestOptionsList.isEmpty() && (latestOptionsList.get(0).equals(Actions.SET_SHADE) || latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
                BorderPane backPane = sceneCreator.bulidSelectDiePane(newWidth,newHeight,tempBoard);
                p.getChildren().add(backPane);
            }
        }
        return p;
    }

    private BorderPane buildFrontPane(double newWidth, double newHeight, LightBoard board){
        //System.out.println("BULIDING FRONT PANE");
        double                      cellDim = sceneCreator.getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrackList = board.getRoundTrack();
        List <LightDie> draftPool = board.getDraftPool();
        List <IndexedCellContent>   latestDiceList = board.getLatestDiceList();
        LightSchemaCard             schemaCard = board.getPlayerById(board.getMyPlayerId()).getSchema();
        int favorTokens =           board.getPlayerById(board.getMyPlayerId()).getFavorTokens();
        ClientFSMState              turnState = client.getFsmState();

        BorderPane frontPane = new BorderPane();

        List<DieContainer> draftPoolCells = sceneCreator.getDraftPoolCells(draftPool,cellDim);
        List<DieContainer> schemaCells = sceneCreator.getSchemaCells(schemaCard,cellDim);
        List<DieContainer> roundTrackCells = sceneCreator.getRoundTrackCells(roundTrackList,turnState,latestDiceList,cellDim);
        sceneCreator.addActionListeners(draftPoolCells,schemaCells,roundTrackCells,turnState,board,cellDim);
        frontPane.setStyle("-fx-background-color: rgba(245,220,112);"); //todo hookup with css



        //Top side of the border pane
        HBox roundTrack = sceneCreator.buildRoundTrack(roundTrackCells);
        Region separator = new Region();
        HBox.setHgrow(separator,Priority.ALWAYS);
        VBox menuButtons = sceneCreator.buildMenuButtons(turnState);
        roundTrack.getChildren().addAll(separator,menuButtons);
        frontPane.setTop(roundTrack);

        //Center side of the border pane
        Group schema = sceneCreator.buildSchema(schemaCells,favorTokens,cellDim);
        HBox playersStatusBar = sceneCreator.getPlayersStatusBar(board.getMyPlayerId(),board);
        StackPane schemaContainer = new StackPane(schema);
        VBox.setVgrow(schemaContainer,Priority.ALWAYS);
        frontPane.setCenter(new VBox(schemaContainer,playersStatusBar));

        //Right side of the border pane
        VBox cards = sceneCreator.drawCards(board.getPrivObj(),board.getPubObjs(),board.getTools(),cellDim,turnState);
        StackPane cardsContainer = new StackPane(cards);
        cards.setAlignment(CENTER);
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