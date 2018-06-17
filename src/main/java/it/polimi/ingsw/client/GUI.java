package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.*;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.immutables.LightPrivObj;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
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
    private static final Object lock = new Object();
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
        synchronized (lock) {
            sceneCreator = new GUIutil(Screen.getPrimary().getVisualBounds(), this, getCmdWrite());
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
           // messageToUser.setText("lobby "+numUsers);
            messageToUser.setText(String.format(uimsg.getMessage("lobby-update"),numUsers));
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
            MainSceneGroup root = new MainSceneGroup(board);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.sizeToScene();
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                root.redraw(newWidth,newHeight);
            });
            scene.heightProperty().addListener((observable, oldValue, newValue) -> {
                double newWidth = scene.getWidth();
                double newHeight = scene.getHeight();
                root.redraw(newWidth,newHeight);
            });
        });
    }

    class MainSceneGroup extends Group{
        LightBoard board;
        BorderPane b;
        HBox roundTrack;
        GridPane schema;
        HBox draftpool;
        VBox schemaVbox;
        MainSceneGroup(LightBoard board){
            this.board = board;
            this.b = new BorderPane();
            this.roundTrack = new HBox();
            this.schema = new GridPane();
            this.draftpool = new HBox();
            this.schemaVbox = new VBox(schema,draftpool);
            b.setTop(roundTrack);
            b.setCenter(schemaVbox);
            this.getChildren().add(b);
            redraw(200,200);
        }

        void redraw(double newWidth, double newHeight) {
            double cellDim = sceneCreator.getMainSceneCellDim(newWidth,newHeight);
            ClientFSMState turnState = client.getTurnState();
            roundTrack.getChildren().setAll(sceneCreator.drawRoundTrack(board.getRoundTrack(),newWidth,newHeight));
            schema.getChildren().add(sceneCreator.drawSchema(board.getPlayerById(playerId).getSchema(),cellDim,turnState));
            draftpool.getChildren().setAll(sceneCreator.drawDraftPool(board.getDraftPool(),cellDim,turnState));
        }
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
    public void showOptions(List<Commands> optionsList) {

    }

    @Override
    public void showWaitingForGameStartScreen() {
        Platform.runLater(() -> {
            String message = String.format("%s%n", uimsg.getMessage("waiting-game-start"));
            primaryStage.setScene(sceneCreator.waitingForGameStartScene(message));
        });

    }

    @Override
    public void showMainScreen(ClientFSMState turnState) {
        //no usage
    }

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