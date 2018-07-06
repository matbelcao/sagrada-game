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
import it.polimi.ingsw.common.serializables.LightDie;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;
import java.util.Observable;

import static it.polimi.ingsw.client.view.clientUI.uielements.CustomGuiEvent.*;
import static it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg.*;
import static javafx.geometry.Pos.CENTER;

public class GUI extends Application implements ClientUI {
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;

    private static Client client;
    private LightBoard board;
    private GUIutil sceneCreator;
    private CmdWriter cmdWrite;
    private static UIMessages uimsg;
    private Stage primaryStage;
    private static GUI instance;
    private SizeListener sizeListener;
    private static final Object lock = new Object();
    private Label messageToUser = new Label();

    public static GUI getGUI() {
        if(instance == null){
            return null;
        }else{
            return instance;
        }
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
            instance = this;
            sceneCreator = new GUIutil(Screen.getPrimary().getVisualBounds(), getCmdWrite() ,uimsg);
            lock.notifyAll();
        }
        primaryStage.setX(sceneCreator.getStageX());
        primaryStage.setY(sceneCreator.getStageY());
        sizeListener = new SizeListener(this);
        this.primaryStage = primaryStage;

    }


    @Override
    public void updateConnectionOk() { Platform.runLater(()->messageToUser.setText(uimsg.getMessage(UIMsg.CONNECTION_OK))); }

    @Override
    public void showLoginScreen() {
        Platform.runLater(() -> {
            synchronized (lock) {
                while (sceneCreator == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            GridPane grid = new GridPane();
            grid.setAlignment(CENTER);
            grid.setHgap(10);
            grid.setVgap(10);

            grid.setPadding(new Insets(sceneCreator.getLoginHeight() * 0.35, 25, 25, 25));

            TextField usernameField = new TextField();
            usernameField.setText(textGen.getRandomString()); //TODO delete
            usernameField.setPromptText("Username");
            usernameField.setMinHeight(sceneCreator.getLoginWidth() * 0.08);
            usernameField.setMinWidth(sceneCreator.getLoginWidth() * 0.75);
            grid.add(usernameField, 1, 1);

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            passwordField.setMinHeight(sceneCreator.getLoginWidth() * 0.08);
            passwordField.setMinWidth(sceneCreator.getLoginWidth() * 0.75);
            grid.add(passwordField, 1, 2);

            Button button = new Button("LOGIN");
            button.setMinHeight(sceneCreator.getLoginWidth() * 0.1);
            button.setMinWidth(sceneCreator.getLoginWidth() * 0.3);
            button.setId("login-button");

            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(button);
            grid.add(hbBtn, 1, 4);

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.TOP_CENTER);
            vbox.setSpacing(60);
            vbox.getChildren().addAll(grid, messageToUser);


            vbox.setStyle("-fx-background-image: url('img/Login/background.png');" +
                    "-fx-background-size: " + sceneCreator.getLoginWidth() + " " + sceneCreator.getLoginHeight() + ";" +
                    "-fx-background-position: center center;");

            Scene loginScene = new Scene(vbox, sceneCreator.getLoginWidth(), sceneCreator.getLoginHeight());
            //I can trigger the login also pressing ENTER
            loginScene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    button.fire();
                }
            });

            loginScene.getStylesheets().add("css/style.css");

            button.setOnAction(e -> {
                client.setUsername(usernameField.getText());
                client.setPassword(Credentials.hash(client.getUsername(), passwordField.getText().toCharArray()));
            });

            primaryStage.setTitle("Login");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(e -> {
                if (client.isLogged())
                    client.quit();
                else {
                    client.disconnect();
                }
            });
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
                messageToUser.setId("login-ok");
                messageToUser.setText(" "+String.format(uimsg.getMessage(LOGIN_OK),client.getUsername())+" ");
            });
        } else {
            Platform.runLater(() -> {
                messageToUser.setId("login-ko");
                messageToUser.setText(" "+uimsg.getMessage(LOGIN_KO)+" ");
            });
        }
    }

    @Override
    public void showLatestScreen() {/*this method is useful only to CLI*/}

    @Override
    public void updateLobby(int numUsers) {
        Platform.runLater(() -> {
            sizeListener.disable();
            primaryStage.setTitle("Sagrada");
            primaryStage.setMinWidth(sceneCreator.getLobbyMinWidth());
            primaryStage.setMinHeight(sceneCreator.getLobbyMinHeight());
            primaryStage.setResizable(true); //it was already set but I set it anyway
            primaryStage.getScene().setRoot(sceneCreator.buildLobbyPane(numUsers));
        });

    }

    private void updateBoard(LightBoard board) {
        this.board =board;
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
            primaryStage.setMinHeight(sceneCreator.getGameSceneMinHeight()); //todo decide if I want to keep it
            sizeListener.purgeTimer();
            drawMainGameScene();
            sizeListener.enable();

            });
    }

    public void drawMainGameScene(){
        Platform.runLater(()-> primaryStage.getScene().setRoot(bulidMainPane(primaryStage.getWidth(), primaryStage.getHeight())));
    }

    private StackPane bulidMainPane(double newWidth, double newHeight){
        StackPane p = new StackPane();
        if(client.getFsmState().equals(ClientFSMState.CHOOSE_SCHEMA)){
            BorderPane draftedSchemasPane = sceneCreator.buildDraftedSchemasPane(board.getDraftedSchemas(), board.getPrivObj(), newWidth, newHeight) ;
            p.getChildren().add(draftedSchemasPane);
        }else if(client.getFsmState().equals(ClientFSMState.SCHEMA_CHOSEN)){
            p.getChildren().add(sceneCreator.buildWaitingForGameStartScene(newWidth, newHeight));
        }else if(client.getFsmState().equals(ClientFSMState.GAME_ENDED)){
            BorderPane gameEndedPane = sceneCreator.buildGameEndedPane(newWidth,newHeight, board.sortFinalPositions());
            p.getChildren().add(gameEndedPane);
        }else{
            BorderPane frontPane = buildFrontPane(newWidth,newHeight, board);
            List <Actions> latestOptionsList = board.getLatestOptionsList();
            ClientFSMState turnState = client.getFsmState();
            p.getChildren().add(frontPane);
            //the pane listens for custom events to know when it has to which layer
            p.addEventFilter(MOUSE_ENTERED_MULTIPLE_DICE_CELL, e -> p.getChildren().setAll(frontPane, sceneCreator.showMultipleDiceRoundTrack(e.getEventObjectIndex(),newWidth,newHeight, board,turnState)));
            p.addEventHandler(SELECTED_PLAYER, e -> p.getChildren().setAll(frontPane,sceneCreator.buildSelectdPlayerPane(e.getEventObjectIndex(),newWidth, newHeight, board)));
            p.addEventHandler(MOUSE_EXITED_BACK_PANE, e->frontPane.toFront());

            if (client.getFsmState().equals(ClientFSMState.SELECT_DIE) && !latestOptionsList.isEmpty() && (latestOptionsList.get(0).equals(Actions.SET_SHADE) || latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
                BorderPane backPane = sceneCreator.bulidSelectDiePane(newWidth,newHeight, board);
                p.getChildren().add(backPane);
            }
        }
        return p;
    }

    private BorderPane buildFrontPane(double newWidth, double newHeight, LightBoard board){
        double                      cellDim = sceneCreator.getMainSceneCellDim(newWidth,newHeight);
        List <List<LightDie>>       roundTrackList = board.getRoundTrack();
        List <LightDie> draftPool = board.getDraftPool();
        LightSchemaCard             schemaCard = board.getPlayerById(board.getMyPlayerId()).getSchema();
        int favorTokens =           board.getPlayerById(board.getMyPlayerId()).getFavorTokens();
        ClientFSMState              turnState = client.getFsmState();

        BorderPane frontPane = new BorderPane();

        frontPane.setStyle("-fx-background-image: url('img/blue-wall.png')");

        List<DieContainer> draftPoolCells = sceneCreator.getDraftPoolCells(draftPool,cellDim);
        List<DieContainer> schemaCells = sceneCreator.getSchemaCells(schemaCard,cellDim);
        List<DieContainer> roundTrackCells = sceneCreator.getRoundTrackCells(roundTrackList,cellDim);
        sceneCreator.addActionListeners(draftPoolCells,schemaCells,roundTrackCells,turnState,board,cellDim);

        //Top side of the border pane
        HBox topSection = sceneCreator.buildRoundTrack(roundTrackCells);
        Region separator = new Region();
        HBox.setHgrow(separator,Priority.ALWAYS);
        VBox menuButtons = sceneCreator.buildMenuButtons(turnState);
        topSection.getChildren().addAll(separator,menuButtons);
        topSection.setId("top-section");
        frontPane.setTop(topSection);

        //Center side of the border pane
        Group schema = sceneCreator.buildSchema(schemaCells,favorTokens,cellDim);
        HBox playersStatusBar = sceneCreator.getPlayersStatusBar(board.getMyPlayerId(),board);
        StackPane schemaContainer = new StackPane(schema);
        VBox.setVgrow(schemaContainer,Priority.ALWAYS);
        frontPane.setCenter(new VBox(schemaContainer,playersStatusBar));

        //Right side of the border pane
        VBox cards = sceneCreator.drawCards(this.board,cellDim,turnState);
        StackPane cardsContainer = new StackPane(cards);
        cards.setAlignment(CENTER);
        GridPane draftpool = sceneCreator.buildDraftPool(draftPoolCells);
        VBox.setVgrow(cardsContainer,Priority.ALWAYS);
        frontPane.setRight(new VBox(cardsContainer,draftpool));
        return frontPane;
    }

    @Override
    public void updateConnectionClosed() {
        //It's always the client that closes the connection
    }

    @Override
    public void updateConnectionBroken(){
        Platform.runLater(()->{
            sizeListener.disable(); //defensive programming
            Stage connectionBrokStage = new Stage();
            connectionBrokStage.initModality(Modality.APPLICATION_MODAL);
            connectionBrokStage.setTitle(uimsg.getMessage(BROKEN_CONNECTION_TITLE));
            connectionBrokStage.setResizable(false);
            primaryStage.setScene(sceneCreator.buildConnecionBrokenScene());
            connectionBrokStage.sizeToScene();
            primaryStage.showAndWait();
        });
    }



    @Override
    public void showWaitingForGameStartScreen() {
       Platform.runLater(() -> primaryStage.getScene().setRoot(sceneCreator.buildWaitingForGameStartScene(primaryStage.getWidth(), primaryStage.getHeight())));
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