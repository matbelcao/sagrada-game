package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.GUIutil;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
import java.util.Map;
import java.util.Observable;

import static javafx.geometry.Pos.CENTER;

public class GUI extends Application implements ClientUI {
    private GUIutil elementSize;
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private static Client client;
    private static UIMessages uimsg;
    private Stage primaryStage;
    private static GUI instance;
    private Text messageToUser = new Text();


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
        elementSize = new GUIutil(Screen.getPrimary().getVisualBounds());
        primaryStage.setTitle("Login");
        this.primaryStage = primaryStage;
        primaryStage.setScene(logInScene());
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e->client.quit());
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    private Scene logInScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene loginScene = new Scene(grid, elementSize.getLoginWidth(), elementSize.getLoginWidth());

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
        return loginScene;
    }


    @Override
    public void updateConnectionOk() {

    }

    @Override
    public void showLoginScreen() {
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

    private Scene gameScene(){
        VBox layout = new VBox();
        layout.getChildren().add(draftPool(client.getBoard().getDraftPool()));
        Scene scene2 = new Scene(layout);
        return scene2;
    }

    private Node draftPool(Map<Integer, LightDie> draftPool) {
        HBox layout = new HBox();
        for(LightDie l : draftPool.values()){
            layout.getChildren().add(elementSize.lightDieToCanvas(l,200));
        }
        return layout;
    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {
        //Platform.runLater(() -> primaryStage.setScene(gameScene()));
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        Platform.runLater(() -> {
            primaryStage.setTitle("Sagrada");
            primaryStage.setResizable(true);
            Canvas canvas = new Canvas();
            Pane  mouseActionPane = new Pane();
            Group group = new Group(canvas,mouseActionPane);
            StackPane stackPane = new StackPane(group);
            Scene scene = new Scene(stackPane);
            DraftedSchemasRecord draftedSchemasRecord = new DraftedSchemasRecord(canvas,mouseActionPane,draftedSchemas,privObj);
            SceneSizeChangeListener sceneSizeChangeListener = new SceneSizeChangeListener(scene,draftedSchemasRecord);
            scene.widthProperty().addListener(sceneSizeChangeListener);
            scene.heightProperty().addListener(sceneSizeChangeListener);
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(elementSize.getDraftedSchemasMinHeight());
            primaryStage.setMinWidth(elementSize.getDraftedSchemasMinWidth());

        });
    }
    private static class SceneSizeChangeListener implements ChangeListener<Number> {
        private final Scene scene;
        private DraftedSchemasRecord draftedSchemasRecord;

        SceneSizeChangeListener(Scene scene, DraftedSchemasRecord draftedSchemasRecord) {
            this.scene = scene;
            this.draftedSchemasRecord = draftedSchemasRecord;
        }
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            double newWidth = scene.getWidth();
            double newHeight = scene.getHeight();
            draftedSchemasRecord.updateScene(newWidth,newHeight);
        }
    }

    class DraftedSchemasRecord {
        Canvas canvas;
        Pane mouseActionPane;
        List<LightSchemaCard> draftedSchemas;
        LightPrivObj privObj;

        public DraftedSchemasRecord(Canvas canvas,Pane mouseActionPane, List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
            this.canvas = canvas;
            this.mouseActionPane = mouseActionPane;
            this.draftedSchemas = draftedSchemas;
            this.privObj = privObj;
        }

        public void updateScene(double width, double height){
            //update the width and height properties
            canvas.setWidth(width);
            canvas.setHeight(height);
            elementSize.drawDraftedSchemas(draftedSchemas,privObj,canvas,width,height);
            mouseActionPane.getChildren().setAll(elementSize.draftedMouseActionAreas(width,height));
        }

    }

    /*class ResizableCanvas extends Canvas {
        List<LightSchemaCard> draftedSchemas;
        LightPrivObj privObj;

        public ResizableCanvas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
            this.draftedSchemas = draftedSchemas;
            this.privObj = privObj;
            // Redraw canvas when size changes.

        }

        public void draw(double width, double height) {
            //update the width and height properties
            setWidth(width);
            setHeight(height);

            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, width, height);
            elementSize.drawDraftedSchemas(draftedSchemas,privObj,gc,width,height);

            gc.setStroke(Color.RED);//to delete
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(0, height, width, 0);

        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }*/

   /* private void letterbox(final Scene scene, final Pane contentPane) {
        final double initWidth  = scene.getWidth();
        final double initHeight = scene.getHeight();
        final double ratio      = initWidth / initHeight;
        SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
    }

    private static class SceneSizeChangeListener implements ChangeListener<Number> {
        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;

        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth, Pane contentPane) {
            this.scene = scene;
            this.ratio = ratio;
            this.initHeight = initHeight;
            this.initWidth = initWidth;
            this.contentPane = contentPane;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            final double newWidth  = scene.getWidth();
            final double newHeight = scene.getHeight();
            double scaleFactor;

            if( newWidth / newHeight > ratio)
                scaleFactor = newHeight / initHeight;
            else
                scaleFactor = newWidth / initWidth;


            if (scaleFactor >= 1) {
                Scale scale = new Scale(scaleFactor, scaleFactor);
                scale.setPivotX(0);
                scale.setPivotY(0);
                scene.getRoot().getTransforms().setAll(scale);

                contentPane.setPrefWidth (newWidth  / scaleFactor);
                contentPane.setPrefHeight(newHeight / scaleFactor);
            } else {
                contentPane.setPrefWidth (Math.max(initWidth,  newWidth));
                contentPane.setPrefHeight(Math.max(initHeight, newHeight));
            }
        }
    }*/


    @Override
    public void updateBoard(LightBoard board) {

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

    }

    @Override
    public void showMainScreen(ClientFSMState turnState) {

    }


    @Override
    public void update(Observable o, Object arg) {

    }
}