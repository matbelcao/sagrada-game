package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.GUIutil;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.transform.Scale;
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
    public static final int LINE_WIDTH = 2;
    public static final double SPOT_RATIO = 6;
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
            client.setUsername(usernameField.getText());
            client.setPassword(Credentials.hash(client.getUsername(),passwordField.getText().toCharArray()));
        });
        usernameField.addEventHandler(KeyEvent.ANY, e->button.fire()); //delete
        return loginScene;
    }

    /*private Scene draftedSchemaSceneBuilder(List<LightSchemaCard> draftedSchemas) {
        double schemaWidth = elementSize.getSchemaWidth();
        double schemaHeigth = elementSize.getSchemaHeigth();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        GridPane schema0 = schemaToGrid(draftedSchemas.get(0), schemaWidth, schemaHeigth);
        GridPane schema1 = schemaToGrid(draftedSchemas.get(1), schemaWidth, schemaHeigth);
        GridPane schema2 = schemaToGrid(draftedSchemas.get(2), schemaWidth, schemaHeigth);
        GridPane schema3 = schemaToGrid(draftedSchemas.get(3), schemaWidth, schemaHeigth);

        grid.add(schema0, 0, 0);
        grid.add(schema1, 1, 0);
        grid.add(schema2, 2, 0);
        grid.add(schema3, 3, 0);
        schema0.addEventHandler(MouseEvent.MOUSE_ENTERED, e->System.out.println(e));

        Scene scene2 = new Scene(grid);
        return scene2;
    }*/




    private GridPane schemaToGrid(LightSchemaCard lightSchemaCard, double width, double heigth){
        GridPane grid = new GridPane();
        double dieDim = elementSize.getDieDimension();
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
        drawDie(die,dieCanvas.getGraphicsContext2D(),dieDim);
        return dieCanvas;
    }

    private Canvas schemaToCanvas(LightSchemaCard lightSchemaCard,double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawSchema(lightSchemaCard,gc);
        return canvas;
    }

    private void drawSchema(LightSchemaCard lightSchemaCard, GraphicsContext gc) {
        double dieDim = elementSize.getDieDimension();
        double y = 0;
        double x = 0;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                if(lightSchemaCard.hasDieAt(i,j)){
                    drawDie(lightSchemaCard.getDieAt(i,j),gc,x,y,dieDim);
                }else if(lightSchemaCard.hasConstraintAt(i,j)){
                    drawConstraint(lightSchemaCard.getConstraintAt(i,j),gc,x,y,dieDim);
                }else{
                    drawWhiteSquare(gc,x,y,dieDim);
                }
                x += dieDim;
            }
            x = 0;
            y += dieDim;
        }
    }

    private void drawWhiteSquare(GraphicsContext gc, double x, double y, double diceDim) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.fillRect(x,y,diceDim,diceDim);
        gc.strokeRect(x,y,diceDim,diceDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double dieDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(constraint.getColor()));
            gc.fillRect(0, 0, dieDim, dieDim);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, dieDim, dieDim);
            drawSpots(gc,dieDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeRect(0, 0, dieDim, dieDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double dieDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(constraint.getColor()));
            gc.fillRect(x, y, dieDim, dieDim);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, dieDim, dieDim);
            drawSpots(gc,x,y,dieDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeRect(x, y, dieDim, dieDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext graphicsContext2D, double dieDim) {
        graphicsContext2D.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        graphicsContext2D.fillRect(0,0,dieDim,dieDim);
        graphicsContext2D.setStroke(Color.BLACK);
        graphicsContext2D.setLineWidth(LINE_WIDTH);
        graphicsContext2D.strokeRect(0,0,dieDim, dieDim);
    }
    //to be used when drawing schema to canvas
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

    private void drawSpots(GraphicsContext gc,double X_axis_die_position,double Y_axis_die_position,double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2,dieDim, X_axis_die_position, Y_axis_die_position);
                break;
            case 3:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, X_axis_die_position, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4,dieDim, X_axis_die_position, Y_axis_die_position);
                break;
            case 5:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, X_axis_die_position, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, dieDim/ 2,dieDim, X_axis_die_position, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 2,dieDim, X_axis_die_position, Y_axis_die_position);
                break;
        }
    }


    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - spotDiameter / 2, y - spotDiameter / 2,
                spotDiameter, spotDiameter);
    }

    private void drawSpot(GraphicsContext gc, double x, double y,double dieDim,double X_axis_die_position,double Y_axis_die_position) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(X_axis_die_position +(x - spotDiameter / 2), Y_axis_die_position + (y - spotDiameter / 2), spotDiameter, spotDiameter);
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
        Scene scene2 = new Scene(layout);
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

            double schemaWidth = elementSize.getSchemaWidth();
            double schemaHeigth = elementSize.getSchemaHeigth();
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(15, 15, 15, 15));

            Canvas schema0 = schemaToCanvas(draftedSchemas.get(0),schemaWidth,schemaHeigth);
            Canvas schema1 = schemaToCanvas(draftedSchemas.get(1),schemaWidth,schemaHeigth);
            Canvas schema2 = schemaToCanvas(draftedSchemas.get(2),schemaWidth,schemaHeigth);
            Canvas schema3 = schemaToCanvas(draftedSchemas.get(3),schemaWidth,schemaHeigth);

            grid.add(schema0,0,0);
            grid.add(schema1,1,0);
            grid.add(schema2,0,1);
            grid.add(schema3,1,1);

            //Group group = new Group( grid );
            //StackPane rootPane = new StackPane(group);

            Scene scene = new Scene(grid);

            primaryStage.setScene(scene);
            letterbox(scene, grid);


            //primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(1));
            //primaryStage.maxWidthProperty().bind(scene.widthProperty().divide(1));
            //primaryStage.setMinHeight(400);
        });
    }

    private void letterbox(final Scene scene, final Pane contentPane) {
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

            double scaleFactor =
                    newWidth / newHeight > ratio
                            ? newHeight / initHeight
                            : newWidth / initWidth;

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
    public void update(Observable o, Object arg) {

    }
}