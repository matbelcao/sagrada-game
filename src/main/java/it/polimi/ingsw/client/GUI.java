package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.*;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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

import java.util.List;
import java.util.Observable;

import static javafx.geometry.Pos.CENTER;

public class GUI extends Application implements ClientUI {
    private GUIutil sceneCreator;
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private static Client client;
    private static UIMessages uimsg;
    private Stage primaryStage;
    private static GUI instance;
    private Text messageToUser = new Text();
    private CmdWriter cmdWrite;
    private int playerId;

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
        sceneCreator = new GUIutil(Screen.getPrimary().getVisualBounds(),this);
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
        Scene loginScene = new Scene(grid, sceneCreator.getLoginWidth(), sceneCreator.getLoginWidth());
        Text scenetitle = new Text("Sagrada");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        Label username = new Label("User Name:");
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
        grid.add(messageToUser, 1, 6);
        button.setOnAction(e -> {
            synchronized (client.getLockCredentials()) {
                client.setUsername(usernameField.getText());
                client.setPassword(Credentials.hash(client.getUsername(), passwordField.getText().toCharArray()));
                client.getLockCredentials().notifyAll();
            }
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
                messageToUser.setText(String.format(uimsg.getMessage("login-ok"),client.getUsername()));
            });
        } else {
            Platform.runLater(() -> {
                messageToUser.setFill(Color.FIREBRICK);
                messageToUser.setText(uimsg.getMessage("login-ko"));
            });
        }
    }

    @Override
    public void showLastScreen() {

    }

    @Override
    public void updateLobby(int numUsers) {
        Platform.runLater(() -> {
            messageToUser.setFill(Color.GREEN);
            /* TODO add other text field */
            messageToUser.setText("lobby "+numUsers);
        });

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
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);
            DraftedSchemasGroup draftedSchemasGroup = new DraftedSchemasGroup(draftedSchemas,privObj,scene);
            stackPane.getChildren().add(draftedSchemasGroup);
            scene.widthProperty().addListener((SceneSizeListener) (observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                draftedSchemasGroup.updateScene(newWidth,newHeight);
            });
            scene.heightProperty().addListener((SceneSizeListener) (observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                draftedSchemasGroup.updateScene(newWidth,newHeight);
            });
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(sceneCreator.getDraftedSchemasMinHeight());
            primaryStage.setMinWidth(sceneCreator.getDraftedSchemasMinWidth());

        });
    }

    @Override
    public void updateBoard(LightBoard board) {
        Platform.runLater(() -> {
            if (board == null) {
                throw new IllegalArgumentException();
            }
            BorderPane b = new BorderPane();
            b.setTop(sceneCreator.getRoundTrack());
            HBox roundtrack = sceneCreator.getRoundTrack();
            Group schema = sceneCreator.getSchema(primaryStage);
            HBox draftpool = sceneCreator.getDraftPool();
            VBox schemaVbox = new VBox(schema,draftpool);
            b.setCenter(schemaVbox);
            Scene s = new Scene(b);
            primaryStage.setScene(s);
        });
    }

    @Override
    public void updateDraftPool(List<LightDie> draftpool) {

    }

    @Override
    public void updateSchema(LightPlayer player) {

    }

    @Override
    public void updateRoundTrack(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void showRoundtrackDiceList(List<IndexedCellContent> roundtrack) {

    }

    @Override
    public void showDraftPoolDiceList(List<IndexedCellContent> draftpool) {

    }

    @Override
    public void showSchemaDiceList(List<IndexedCellContent> schema) {

    }

    @Override
    public void updateToolUsage(List<LightTool> tools) {

    }

    @Override
    public void showPlacementsList(List<Integer> placements, Place to, LightDie die) {

    }

    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

    }

    @Override
    public void updateConnectionClosed() {

    }

    @Override
    public void updateConnectionBroken() {

    }

    @Override
    public void printmsg(String msg) {

    }

    @Override
    public String getCommand() {
        return null;
    }

    @Override
    public void showOptions(List<Commands> optionsList) {

    }

    @Override
    public void showWaitingForGameStartScreen() {
        Platform.runLater(() -> {
            primaryStage.setScene(sceneCreator.waitingForGameStartScene());
        });

    }

    @Override
    public void showMainScreen(ClientFSMState turnState) {

    }

    class DraftedSchemasGroup extends Group{
        Scene scene;
        Canvas canvas;
        Pane mouseActionPane;
        List<LightSchemaCard> draftedSchemas;
        LightPrivObj privObj;

        public DraftedSchemasGroup(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj, Scene scene) {
            this.scene = scene;
            this.canvas = new Canvas();
            this.mouseActionPane = new Pane();
            this.draftedSchemas = draftedSchemas;
            this.privObj = privObj;
            this.getChildren().addAll(canvas,mouseActionPane);
        }

        public void updateScene(double width, double height){
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
                    System.out.println("Selected schema " + actionRects.indexOf(r));
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
        cmdWrite = new QueuedCmdReader();
        return (QueuedReader) cmdWrite;
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}