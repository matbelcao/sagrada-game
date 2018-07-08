package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.controller.ClientFSMState;
import it.polimi.ingsw.client.controller.CmdWriter;
import it.polimi.ingsw.client.controller.QueuedCmdReader;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.GUIutil;
import it.polimi.ingsw.client.view.clientUI.uielements.SizeListener;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Actions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
/**
 * This is the class that implements the UI for the client as a graphic interface
 */
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
    private boolean loginIsActive = true;


    /**
     * Getter of the instance of the gui
     * @return an instance of the gui
     */
    public static GUI getGUI() {
        //Since the GUI object is instantiated by the javaFx thread client needs a static getter to get an instance
        //of the UI
        if(instance == null){
            return null;
        }else{
            return instance;
        }
    }

    /**
     * Launches the JavaFx thread after setting the static parameters.
     * @param client a reference of the client launching the GUI
     * @param lang the language chose by the client
     */
    public static void launch(Client client, UILanguage lang) {
        //since the GUI is instantiated by the javafx thread the only way for the client to get a reference of the GUI is
        //for the GUI to pass itself to the client after it has been created
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

    /**
     * this method asks via the ui to insert username and password and sets them in the client
     */
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
                if(loginIsActive) {
                        client.setUsername(usernameField.getText());
                        client.setPassword(Credentials.hash(client.getUsername(), passwordField.getText().toCharArray()));
                        loginIsActive = false;
                    }
            });

            primaryStage.setTitle("Login");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(e -> {
                if (client.isLogged())
                    client.quit();
                else {
                    System.exit(1);
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

    /**
     * this method notifies the user whether the login was successful or not
     * @param logged the outcome of the login (true iff it went fine)
     */
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
                loginIsActive = true;
            });
        }
    }

    @Override
    public void showLatestScreen() {/*this method is useful only to CLI*/}

    /**
     * this method sends to the client an update regarding the number of players connected and waiting to begin a match
     * @param numUsers the number of connected players at the moment
     */
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

    /**
     * This method gets called everytime the lightBoard gets updated and creates the GUI's scenes accordingly to the info
     * taken by it
     * @param board the updated board
     */
    private void updateBoard(LightBoard board) {
        if (board == null) {
            throw new IllegalArgumentException();
        }
        this.board =board;
        Platform.runLater(() -> {
            primaryStage.setMinWidth(sceneCreator.getGameSceneMinWidth());
            primaryStage.setMinHeight(sceneCreator.getGameSceneMinHeight());
            sizeListener.purgeTimer();
            drawMainGameScene();
            sizeListener.enable();
        });
    }
    /**
     * This method gets called every time the board gets updated or the scene resizes chenging the root of the scene of the stage
     */
    public void drawMainGameScene(){
        Platform.runLater(()-> primaryStage.getScene().setRoot(bulidMainPane(primaryStage.getWidth(), primaryStage.getHeight())));
    }

    /**
     * This method constructs the root for scenes that get info from the board
     * @param width the width that the newly created root must have
     * @param height the width that the newly created root must have
     * @return a StackPane that gets set as a root of the newly created scene
     */
    private synchronized StackPane bulidMainPane(double width, double height){
        StackPane p = new StackPane();
        if(client.getFsmState().equals(ClientFSMState.CHOOSE_SCHEMA)){
            BorderPane draftedSchemasPane = sceneCreator.buildDraftedSchemasPane(board.getDraftedSchemas(), board.getPrivObj(), width, height) ;
            p.getChildren().add(draftedSchemasPane);
        }else if(client.getFsmState().equals(ClientFSMState.SCHEMA_CHOSEN)){
            p.getChildren().add(sceneCreator.buildWaitingForGameStartScene());
        }else if(client.getFsmState().equals(ClientFSMState.GAME_ENDED)){
            BorderPane gameEndedPane = sceneCreator.buildGameEndedPane(width,height, board);
            p.getChildren().add(gameEndedPane);
        }else{
            BorderPane frontPane = sceneCreator.buildFrontPane(width,height, board,client.getFsmState());
            List <Actions> latestOptionsList = board.getLatestOptionsList();
            ClientFSMState turnState = client.getFsmState();
            p.getChildren().add(frontPane);
            //the pane listens for custom events to know when it has to which layer
            p.addEventFilter(MOUSE_ENTERED_MULTIPLE_DICE_CELL, e -> p.getChildren().setAll(frontPane, sceneCreator.showMultipleDiceRoundTrack(e.getEventObjectIndex(),width,height, board,turnState)));
            p.addEventHandler(SELECTED_PLAYER, e -> p.getChildren().setAll(frontPane,sceneCreator.buildSelectedPlayerPane(e.getEventObjectIndex(),width, height, board)));
            p.addEventHandler(MOUSE_EXITED_BACK_PANE, e->frontPane.toFront());

            if (client.getFsmState().equals(ClientFSMState.SELECT_DIE) && !latestOptionsList.isEmpty() && (latestOptionsList.get(0).equals(Actions.SET_SHADE) || latestOptionsList.get(0).equals(Actions.INCREASE_DECREASE))) {
                BorderPane backPane = sceneCreator.bulidDieOptionPane(width,height, board);
                p.getChildren().add(backPane);
            }
        }
        return p;
    }

    @Override
    public void updateConnectionClosed() {
        //when the user closes the window he knows the program has stopped so he doesn't need to be updated
    }

    /**
     *This method gets invoked when the connection to the server is lost, it closes the window currently showing and shows an alert box informing the user that the connection is broken
     */
    @Override
    public void updateConnectionBroken(){
        Platform.runLater(()->{
            primaryStage.close();
            sizeListener.disable();
            Stage connectionBrokenStage = new Stage();
            connectionBrokenStage.initModality(Modality.APPLICATION_MODAL);
            connectionBrokenStage.setTitle(uimsg.getMessage(BROKEN_CONNECTION_TITLE));
            connectionBrokenStage.setResizable(false);
            connectionBrokenStage.setScene(sceneCreator.buildConnecionBrokenScene());
            connectionBrokenStage.setOnCloseRequest(e->System.exit(1));
            connectionBrokenStage.sizeToScene();
            connectionBrokenStage.setAlwaysOnTop(true);
            connectionBrokenStage.showAndWait();
});
        }

    /**
     * this tells the user to wait for the new game
     */
    @Override
    public void showWaitingForGameStartScreen() {
        Platform.runLater(() -> primaryStage.getScene().setRoot(sceneCreator.buildWaitingForGameStartScene()));
    }
    /**
     * @return the command queue of the ui
     */
    @Override
    public QueuedReader getCommandQueue() {
        if (cmdWrite == null) {
            cmdWrite = new QueuedCmdReader();
        }
        return (QueuedReader) cmdWrite;
    }

    /**
     * This is a getter for the CmdWrite, if it hasn't been initialized it instantiates a new one
     * @return an instance of CmdWriter
     */
    private CmdWriter getCmdWrite(){
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