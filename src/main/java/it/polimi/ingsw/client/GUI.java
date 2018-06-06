package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.immutables.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    public static final int LINE_WIDTH = 1;
    public static final int SPOT_RATIO = 7;
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
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
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
        Scene loginScene = new Scene(grid, 325, 300);

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
        //-----------------
        Button button = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(button);
        grid.add(hbBtn, 1, 4);

        //---

        grid.add(messageToUser, 1, 6);

        //----
        button.setOnAction(e -> {
            client.setUsername(usernameField.getText());
            client.setPassword(Credentials.hash(client.getUsername(),passwordField.getText().toCharArray()));
        });
        return loginScene;
    }

    //TODO uncomment
    private Scene draftedSchemaSceneBuilder(List<LightSchemaCard> draftedSchemas, double width, double heigth) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        GridPane schema0 = schemaToGrid(draftedSchemas.get(0), width, heigth);
        GridPane schema1 = schemaToGrid(draftedSchemas.get(1), width, heigth);
        GridPane schema2 = schemaToGrid(draftedSchemas.get(2), width, heigth);
        GridPane schema3 = schemaToGrid(draftedSchemas.get(3), width, heigth);

        Button b0 = new Button("Select");
        Button b1 = new Button("Select");
        Button b2 = new Button("Select");
        Button b3 = new Button("Select");

        b0.setAlignment(CENTER);
        b1.setAlignment(CENTER);
        b2.setAlignment(CENTER);
        b3.setAlignment(CENTER);

        b0.setOnAction(e ->client.getClientConn().chooseSchema(0));
        b1.setOnAction(e ->client.getClientConn().chooseSchema(1));
        b2.setOnAction(e ->client.getClientConn().chooseSchema(2));
        b3.setOnAction(e ->client.getClientConn().chooseSchema(3));

        grid.add(schema0, 0, 0);
        grid.add(schema1, 1, 0);
        grid.add(schema2, 2, 0);
        grid.add(schema3, 3, 0);
        grid.add(b0, 0, 1);
        grid.add(b1, 1, 1);
        grid.add(b2, 2, 1);
        grid.add(b3, 3, 1);

        schema0.addEventHandler(MouseEvent.MOUSE_ENTERED, e->System.out.println(e));

        Scene scene2 = new Scene(grid, 800, 200);
        return scene2;
    }


    /*private Scene draftedSchemaSceneBuilder(List<LightSchemaCard> draftedSchemas, double width, double heigth) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Canvas schema0 = schemaToCanvas(draftedSchemas.get(0),width,heigth);
        Canvas schema1 = schemaToCanvas(draftedSchemas.get(1),width,heigth);
        Canvas schema2 = schemaToCanvas(draftedSchemas.get(2),width,heigth);
        Canvas schema3 = schemaToCanvas(draftedSchemas.get(3),width,heigth);
        grid.add(schema0,0,0);
        grid.add(schema1,1,0);
        grid.add(schema2,2,0);
        grid.add(schema3,3,0);

        Scene scene2 = new Scene(grid, 1200, 300);
        return scene2;
    }*/

    private GridPane schemaToGrid(LightSchemaCard lightSchemaCard, double width, double heigth){
        GridPane grid = new GridPane();
        double dieDim = width / NUM_COLS;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                if(lightSchemaCard.hasDieAt(i,j)){
                        grid.add(lightDieToCanvas(lightSchemaCard.getDieAt(i,j),dieDim),j,i);
                }else if(lightSchemaCard.hasConstraintAt(i,j)){
                    grid.add(lightConstraintToCanvas(lightSchemaCard.getConstraintAt(i,j),dieDim),j,i);
                }else{
                    grid.add(whiteCanvas(dieDim),j,i);
                }
            }
        }
        return grid;
    }
    private Canvas whiteCanvas(double dim){
        Canvas whiteCanvas = new Canvas(dim,dim);
        GraphicsContext gc = whiteCanvas.getGraphicsContext2D();
        drawWhiteSquare(gc,0,0,dim);
        return whiteCanvas;
    }
    private Canvas lightConstraintToCanvas(LightConstraint constraint,double dieDim){
        Canvas dieCanvas = new Canvas(dieDim,dieDim);
        GraphicsContext gc = dieCanvas.getGraphicsContext2D();
        drawConstraint(constraint,dieCanvas.getGraphicsContext2D(),0,0,dieDim);
        return dieCanvas;
    }
    private Canvas lightDieToCanvas(LightDie die,double dieDim){
        Canvas dieCanvas = new Canvas(dieDim,dieDim);
        GraphicsContext gc = dieCanvas.getGraphicsContext2D();
        drawDie(die,dieCanvas.getGraphicsContext2D(),0,0,dieDim);
        return dieCanvas;
    }

    private Canvas schemaToCanvas(LightSchemaCard lightSchemaCard,double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawSchema(lightSchemaCard,gc, width,  height);
        return canvas;
    }

    private void drawSchema(LightSchemaCard lightSchemaCard, GraphicsContext gc, double width, double height) {
        double diceDim = width / NUM_COLS;
        double y = 0;
        double x = 0;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                if(lightSchemaCard.hasDieAt(i,j)){
                    drawDie(lightSchemaCard.getDieAt(i,j),gc,x,y,diceDim);
                }else if(lightSchemaCard.hasConstraintAt(i,j)){
                    drawConstraint(lightSchemaCard.getConstraintAt(i,j),gc,x,y,diceDim);
                }else{
                    drawWhiteSquare(gc,x,y,diceDim);
                }
                x += diceDim;
            }
            x = 0;
            y += diceDim;
        }
    }

    private void drawWhiteSquare(GraphicsContext gc, double x, double y, double diceDim) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.fillRect(x,y,diceDim,diceDim);
        gc.strokeRect(x,y,diceDim,diceDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double dieDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(constraint.getColor()));
            gc.fillRect(x, y, dieDim, dieDim);
        }else{
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, dieDim, dieDim);
            drawSpots(gc,dieDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeRect(x, y, dieDim, dieDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext gc, double x, double y, double diceDim) {
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        gc.fillRect(x,y,diceDim,diceDim);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeRect(x, y, diceDim, diceDim);
    }
    private void drawSpots(GraphicsContext gc, double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2,dieDim);
                break;
            case 3:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim);
                // Fall thru to next case
            case 2:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4,dieDim);
                break;
            case 5:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim);
                // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim/ 4,dieDim);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawSpot(gc, dieDim/ 4, dieDim/ 2,dieDim);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 2,dieDim);
                break;
        }
    }

    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - spotDiameter / 2, y - spotDiameter / 2,
                spotDiameter, spotDiameter);
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

        Scene scene2 = new Scene(layout, 1200, 300);
        return scene2;
    }

    private Node draftPool(Map<Integer, LightDie> draftPool) {
        HBox layout = new HBox();
        for(LightDie l : draftPool.values()){
            layout.getChildren().add(lightDieToCanvas(l,200));
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
            primaryStage.sizeToScene();
            primaryStage.setScene(draftedSchemaSceneBuilder(draftedSchemas,250,200));
            primaryStage.sizeToScene();
        });
    }

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
    public void showTurnInitScreen() {

    }

    @Override
    public void showNotYourTurnScreen() {

    }
    @Override
    public void updateToolUsage(List<LightTool> tools) {

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
    public void update(Observable o, Object arg) {

    }
}