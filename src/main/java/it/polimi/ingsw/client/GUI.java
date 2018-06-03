package it.polimi.ingsw.client;

import it.polimi.ingsw.client.ClientUI;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.common.immutables.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;
import java.util.Map;

public class GUI extends Application implements ClientUI {
    private static Client client;
    private static UILanguage language;
    private static Stage primaryStage;
    private boolean logged;

    public GUI(Client client, UILanguage language){
        GUI.client =client;
        GUI.language =language;
    }

    public GUI(){
    }



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LOGIN WINDOW");
        GUI.primaryStage =primaryStage;
        //---

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //----------
        Scene scene = new Scene(grid, 325, 300);
        primaryStage.setScene(scene);

        Text scenetitle = new Text("Sagrada");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label username = new Label("User Name:");
        grid.add(username, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label password = new Label("Password:");
        grid.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);
        //-----------------
        Button button = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(button);
        grid.add(hbBtn, 1, 4);

        //---
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        //----
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                System.out.println(usernameField.getText());
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Sign in button pressed");
                System.out.println(usernameField.getText()+"   "+passwordField.getText().toCharArray());
                client.setUsername(usernameField.getText());
                client.setPassword(passwordField.toString().toCharArray());
            }
        });
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    @Override
    public void updateConnectionOk() {

    }

    @Override
    public void showLoginScreen() {
        launch();
    }

    @Override
    public void updateLogin(boolean logged) {
        if(logged){
            System.out.println("Succ logged");
        }else{
            System.out.println("Fail login");
        }
        //primaryStage.close();
    }

    @Override
    public void updateLobby(int numUsers) {

    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {

    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {

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
    public void showRoundTrackWithCoordinates(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void updateRoundStart(int numRound, List<List<LightDie>> roundtrack) {

    }

    @Override
    public void updateTurnStart(int playerId, boolean isFirstTurn, Map<Integer, LightDie> draftpool) {

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
        System.out.println(msg);
    }

    @Override
    public String getCommand() {
        return null;
    }
}