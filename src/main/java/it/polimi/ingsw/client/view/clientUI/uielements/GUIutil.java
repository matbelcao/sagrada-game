package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.clientController.CmdWriter;
import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.clientUI.GUI;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.clientFSM.ClientFSMState.NOT_MY_TURN;

public class GUIutil {
    private final CmdWriter cmdWrite;
    private GUI gui;
    //ratio is width/height
    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private final double SCREEN_WIDTH;
    private final double SCREEN_HEIGHT;
    //-----login Stage
    private static final double LOGIN_TO_SCREEN_RATIO = 0.18;
    private static final double LOGIN_RATIO = 0.95;
    //-----Drafted Schema Stage
    private static final double DRAFTED_SCHEMAS_TO_SCREEN_RATIO = 0.6;
    private static final double DRAFTED_CANVAS_SCENE_RATIO = 1.5314;
    private static final double NUM_OF_DRAFTED_SCHEMAS = 4;
    private static final double SCHEMA_W_TO_PRIVOBJ_W = 1.3846;
    private static final double PRIVATE_OBJ_RATIO = 0.7386;
    private static final double COMPLETE_SCHEMA_RATIO = 1.125;
    private static final double SCHEMA_W_TO_DRAFTED_W = 0.3358;
    private static final double SCHEMA_ARC_TO_WIDTH = 0.0666;
    private static final double SCHEMA_LINE_TO_WIDTH = 0.02;
    private static final double CELL_TO_SCHEMA_W = 0.1925;
    private static final double LINE_TO_CELL = 0.12;
    private static final double TEXT_HEIGHT_TO_SCHEMA_H = 0.90;
    private static final double TEXT_DIM_TO_SCHEMA_W = 0.0505;
    private static final double FAVOR_DIAM_TO_SCHEMA_W = 0.038;
    private static final double FAVOR_POS_TO_SCHEMA_W = 0.92;
     //Those are the ratios between the schemas and their padding respectively to the window, other schemas and priv obj
    private static final double SCHEMA_W_TO_EXTERNAL_PADDING = 18;
    private static final double SCHEMA_W_TO_INTERNAL_PADDING = 18;
    private static final double SCHEMA_W_TO_PRIV_OBJ_PADDING = 9;
    //Main scene
    private static  final double MAIN_SCENE_RATIO =  1.4286;
    private static final double MAIN_SCENE_TO_SCREEN = 0.8;
    private static final double DIE_ARC_TO_DIM = 0.35;
    private static final double DIE_LINE_TO_DIM = 0.1;
    //die s..
    private static final int SCREEN_TO_DIE = 25;
    private static final int DIE_TO_LINE = 10;
    private static final int SPOT_RATIO = 6;

    private static final int LINE_WIDTH = 2;





    public GUIutil(Rectangle2D visualBounds, GUI gui, CmdWriter cmdWrite) {
        SCREEN_WIDTH = visualBounds.getWidth();
        SCREEN_HEIGHT = visualBounds.getHeight();
        this.gui = gui;
        this.cmdWrite = cmdWrite;
    }

    public double getLoginWidth(){
        return SCREEN_WIDTH*LOGIN_TO_SCREEN_RATIO;
    }

    public double getLoginHeight(){
        return getLoginWidth()/LOGIN_RATIO;
    }

    public double getDraftedSchemasMinHeight(){
        return  getDraftedSchemasMinWidth()/DRAFTED_CANVAS_SCENE_RATIO;
    }

    public double getDraftedSchemasMinWidth(){
        return DRAFTED_SCHEMAS_TO_SCREEN_RATIO*SCREEN_WIDTH;
    }

    private double getDraftedSchemasWidth(double drawingHeight){
        return drawingHeight* DRAFTED_CANVAS_SCENE_RATIO;
    }

    private double getDraftedSchemasHeight(double drawingWidth){
        return drawingWidth/ DRAFTED_CANVAS_SCENE_RATIO;
    }

    public double getSelectedSchemaLineWidth(double sceneWidth, double sceneHeight){
        DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth,sceneHeight);
        double drawingWidth = sizes.getDrawingWidth();
        double schemaWidth = drawingWidth*SCHEMA_W_TO_DRAFTED_W;
        return LINE_TO_CELL*CELL_TO_SCHEMA_W*schemaWidth;
    }


    public Group drawRoundTrack(List<List<LightDie>> roundTrack,double width,double height, ClientFSMState turnState, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie) {
        double dieDim = getMainSceneCellDim(width,height);
        HBox track = new HBox();
        track.setSpacing(10);
        if(roundTrack.isEmpty()){
            Canvas c = new Canvas(dieDim,dieDim);
            drawWhiteCell(c.getGraphicsContext2D(),0,0,dieDim);
            track.getChildren().add(c);
        }else {
            for (int i = 0; i < roundTrack.size(); i++) {
                Canvas c = new Canvas(dieDim, dieDim);
                GraphicsContext gc = c.getGraphicsContext2D();
                drawDie(roundTrack.get(i).get(0), gc, dieDim);
                track.getChildren().add(c);
            }
        }
        Button endTurn = new Button("end turn");
        endTurn.setOnAction(e->cmdWrite.write("e"));
        Button back = new Button("back");
        back.setOnAction(e->cmdWrite.write("b"));
        Rectangle turnStateIndicator = new Rectangle(100,100);
        if(turnState.equals(NOT_MY_TURN)){
            turnStateIndicator.setFill(Color.RED);
        }else{
            turnStateIndicator.setFill(Color.GREEN);
        }
        track.getChildren().addAll(back,endTurn,turnStateIndicator);
        return new Group(track);
    }

    public Group drawDraftPool(List<LightDie> draftPool, double dieDim, ClientFSMState turnState) {
        HBox pool = new HBox();
        pool.setSpacing(10);
        for(int i = 0 ; i<draftPool.size();i++){
            Canvas c = new Canvas(dieDim,dieDim);
            drawDie(draftPool.get(i),c.getGraphicsContext2D(),dieDim);
            pool.getChildren().add(c);
            int finalI = i;
            c.setOnMouseClicked(e->{
                switch (turnState){
                    case NOT_MY_TURN:
                        System.out.println("clicked not my turn");
                        break;
                    case MAIN:
                        cmdWrite.write("1");
                        System.out.println("selected die at position " + finalI + "in draftpool");
                        cmdWrite.write(finalI +"");
                        break;
                    case CHOOSE_PLACEMENT:
                        cmdWrite.write("b");
                        cmdWrite.write("1");
                        cmdWrite.write(finalI +"");
                }
            });
        }
        return  new Group(pool);

        }

    public Group drawSchema(LightSchemaCard schema, double dieDim, ClientFSMState turnState, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie) {
        GridPane g = schemaToGrid(schema,dieDim*NUM_COLS,dieDim*NUM_ROWS,turnState,latestPlacementsList,latestSelectedDie);
        return new Group(g);
    }

    public GridPane schemaToGrid(LightSchemaCard lightSchemaCard, double width, double heigth, ClientFSMState turnState, List<Integer> latestPlacementsList, IndexedCellContent latestSelectedDie){
        GridPane grid = new GridPane();
        double cellDIm = width/5;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                Canvas cell = new Canvas(cellDIm,cellDIm);
                if(lightSchemaCard.hasConstraintAt(i,j)){
                    cell = lightConstraintToCanvas(lightSchemaCard.getConstraintAt(i,j),cellDIm);
                    grid.add(cell,j,i);
                }else{
                    cell = whiteCanvas(cellDIm);
                    grid.add(cell,j,i);
                }
                if(lightSchemaCard.hasDieAt(i,j)){
                    cell = lightDieToCanvas(lightSchemaCard.getDieAt(i,j),cellDIm);
                    grid.add(cell,j,i);
                }
                int position = i*NUM_COLS+j;
                if(turnState.equals(ClientFSMState.CHOOSE_PLACEMENT)&& latestSelectedDie.getPlace().equals(Place.DRAFTPOOL)&& latestPlacementsList.contains(position)){
                    highlight(cell,cellDIm);
                    cell.setOnMouseClicked(e->{
                        cmdWrite.write(latestPlacementsList.indexOf(position) +"");
                        System.out.println("selected position " + position);
                    });
                    continue;
                }
                cell.setOnMouseClicked(e->{
                    switch (turnState){
                        case NOT_MY_TURN:
                            System.out.println("clicked not my turn");
                            break;
                    }
                });
            }
        }
        return grid;
    }

    private void highlight(Canvas cell, double cellDim) {
        GraphicsContext gc = cell.getGraphicsContext2D();
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(cellDim*LINE_TO_CELL);
        gc.strokeRect(0,0,cellDim,cellDim);
    }

    private Canvas whiteCanvas(double dim){
        Canvas whiteCanvas = new Canvas(dim,dim);
        GraphicsContext gc = whiteCanvas.getGraphicsContext2D();
        drawWhiteCell(gc,0,0,dim);
        return whiteCanvas;
    }

   /* private Canvas schemaToCanvas(LightSchemaCard lightSchemaCard,double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawSchema(lightSchemaCard,gc);
        return canvas;
    }*/

    public Canvas lightDieToCanvas(LightDie die, double dieDim){
        Canvas dieCanvas = new Canvas(dieDim,dieDim);
        drawDie(die,dieCanvas.getGraphicsContext2D(),dieDim);
        return dieCanvas;
    }

    private Canvas lightConstraintToCanvas(LightConstraint constraint, double dieDim){
        Canvas dieCanvas = new Canvas(dieDim,dieDim);
        drawConstraint(constraint,dieCanvas.getGraphicsContext2D(),0,0,dieDim);
        return dieCanvas;
    }

    public Scene waitingForGameStartScene(String message) {
        Text waitingText = new Text(message);
        StackPane p = new StackPane(waitingText);
        return new Scene(p);
    }

    public double getMainSceneCellDim(double newWidth, double newHeight) {
        return 100;
    }



   /* private void drawSchema(LightSchemaCard lightSchemaCard, GraphicsContext gc) {
        double dieDim = getDieDimension();
        double y = 0;
        double x = 0;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                if(lightSchemaCard.hasDieAt(i,j)){
                    drawDie(lightSchemaCard.getDieAt(i,j),gc,x,y,dieDim);
                }else if(lightSchemaCard.hasConstraintAt(i,j)){
                    drawConstraint(lightSchemaCard.getConstraintAt(i,j),gc,x,y,dieDim);
                }else{
                    drawWhiteCell(gc,x,y,dieDim);
                }
                x += dieDim;
            }
            x = 0;
            y += dieDim;
        }
    }*/
   //todo delete class
    //just a class to avoid having repeated code
    private class DraftedSchemasWindowDim {
        double sceneWidth;
        double sceneHeight;
        double drawingHeight;
        double drawingWidth;
        double x;
        double y;

        private DraftedSchemasWindowDim(double sceneWidth, double sceneHeight){
            double sceneRatio = sceneWidth/sceneHeight;
            if(sceneRatio >= DRAFTED_CANVAS_SCENE_RATIO){
                drawingHeight = sceneHeight;
                drawingWidth = getDraftedSchemasWidth(drawingHeight);
                x = (sceneWidth-drawingWidth)/2;
            }else{
                drawingWidth = sceneWidth;
                drawingHeight = getDraftedSchemasHeight(drawingWidth);
                y = (sceneHeight - drawingHeight)/2;
            }
        }

        double getX(){return x;}
        double getY(){return y;}
        double getDrawingWidth(){return drawingWidth;}
        double getDrawingHeight(){return drawingHeight;}

    }

    public List<Rectangle> draftedMouseActionAreas(double sceneWidth, double sceneHeight) {
       DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth,sceneHeight);
       double x = sizes.getX();
       double y = sizes.getY();
       double drawingWidth = sizes.getDrawingWidth();
       double drawingHeight;
       double schemaWidth = drawingWidth*SCHEMA_W_TO_DRAFTED_W;
       double schemaHeight = schemaWidth/COMPLETE_SCHEMA_RATIO;
       double extPadding = schemaWidth/SCHEMA_W_TO_EXTERNAL_PADDING;
       double intPadding = schemaWidth/SCHEMA_W_TO_INTERNAL_PADDING;

       Rectangle r0 = new Rectangle(x+extPadding,y+extPadding,schemaWidth,schemaHeight);
       Rectangle r1 = new Rectangle(x+extPadding+schemaWidth+intPadding,y+extPadding,schemaWidth,schemaHeight);
       Rectangle r2 = new Rectangle(x+extPadding,y+extPadding+schemaHeight+intPadding,schemaWidth,schemaHeight);
       Rectangle r3 = new Rectangle(x+extPadding+schemaWidth+intPadding,y+extPadding+intPadding+schemaHeight,schemaWidth,schemaHeight);

       ArrayList<Rectangle> rects = new ArrayList<>();
       rects.add(r0);
       rects.add(r1);
       rects.add(r2);
       rects.add(r3);
       return rects;
   }



    public void drawDraftedSchemas(List<LightSchemaCard> lightSchemaCard, LightPrivObj privObj, Canvas canvas, double sceneWidth, double sceneHeight) {
        DraftedSchemasWindowDim sizes = new DraftedSchemasWindowDim(sceneWidth,sceneHeight);
        double x = sizes.getX();
        double y = sizes.getY();
        double drawingWidth = sizes.getDrawingWidth();
        double drawingHeight = sizes.getDrawingHeight();
        double schemaWidth = drawingWidth*SCHEMA_W_TO_DRAFTED_W;
        double schemaHeight = schemaWidth/COMPLETE_SCHEMA_RATIO;
        double extPadding = schemaWidth/SCHEMA_W_TO_EXTERNAL_PADDING;
        double intPadding = schemaWidth/SCHEMA_W_TO_INTERNAL_PADDING;

        double privObjX = x+extPadding+schemaWidth+ intPadding+ schemaWidth+ extPadding;
        double privObjY = y + extPadding;
        double privObjWidth = schemaWidth/SCHEMA_W_TO_PRIVOBJ_W;
        double privObjHeight =privObjWidth/PRIVATE_OBJ_RATIO;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        //clean the canvas
        gc.clearRect(0, 0, sceneWidth, sceneHeight);

        drawPrivObj(privObj,gc,privObjX,privObjY,privObjWidth,privObjHeight);
        drawCompleteSchema(gc,lightSchemaCard.get(0),x+extPadding,y+extPadding,schemaWidth,schemaHeight);
        drawCompleteSchema(gc,lightSchemaCard.get(1),x+extPadding+schemaWidth+intPadding,y+extPadding,schemaWidth,schemaHeight);
        drawCompleteSchema(gc,lightSchemaCard.get(2),x+extPadding,y+extPadding+schemaHeight+intPadding,schemaWidth,schemaHeight);
        drawCompleteSchema(gc,lightSchemaCard.get(3),x+extPadding+schemaWidth+intPadding,y+extPadding+intPadding+schemaHeight,schemaWidth,schemaHeight);

        //todo delete
        gc.setLineWidth(1);
        gc.setStroke(Color.RED);
        gc.strokeRect(x,y,drawingWidth,drawingHeight);
        gc.strokeLine(0, 0, sceneWidth, sceneHeight);
        gc.strokeLine(0, sceneHeight, sceneWidth, 0);

    }

    private void drawCompleteSchema(GraphicsContext gc,LightSchemaCard lightSchemaCard, double x, double y, double schemaWidth, double schemaHeight) {
        double initialX = x;
        double initialY = y;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x,y,schemaWidth,schemaHeight,SCHEMA_ARC_TO_WIDTH*schemaWidth,SCHEMA_ARC_TO_WIDTH*schemaWidth);
        x = x + SCHEMA_LINE_TO_WIDTH*schemaWidth;
        y = y + SCHEMA_LINE_TO_WIDTH*schemaWidth;
        double cellDim = CELL_TO_SCHEMA_W*schemaWidth;
        drawSchema(gc,lightSchemaCard,x,y,cellDim);
        int textLen = lightSchemaCard.getName().length();
        x = initialX + schemaWidth/2;
        y = initialY + TEXT_HEIGHT_TO_SCHEMA_H*schemaHeight;
        drawText(gc,x,y,schemaWidth,lightSchemaCard);
        drawFavorTokens(gc,initialX,y,schemaWidth,lightSchemaCard);

    }

    private void drawFavorTokens(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard) {
        int favorTokens = lightSchemaCard.getFavorTokens();
        double favTokDiameter = schemaWidth* FAVOR_DIAM_TO_SCHEMA_W;
        x = x + FAVOR_POS_TO_SCHEMA_W*schemaWidth;
        for(int i = 0; i < favorTokens;i++){
            gc.setFill(Color.WHITE);
            gc.fillOval(x,y+favTokDiameter/3,favTokDiameter,favTokDiameter);
            x = x - favTokDiameter-favTokDiameter/10;
        }

    }

    public void drawText(GraphicsContext gc, double x, double y, double schemaWidth, LightSchemaCard lightSchemaCard){
        double textSize = TEXT_DIM_TO_SCHEMA_W*schemaWidth;
        gc.setFont(Font.font("Serif", textSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.AZURE);
        gc.fillText(lightSchemaCard.getName(),x,y);
    }

    private void drawSchema(GraphicsContext gc,LightSchemaCard lightSchemaCard,double x,double y, double cellDim) {
        double initX = x;
        for(int i = 0; i < NUM_ROWS; i++){
            for(int j = 0; j < NUM_COLS; j++){
                if(lightSchemaCard.hasDieAt(i,j)){
                    drawDie(lightSchemaCard.getDieAt(i,j),gc,x,y,cellDim);
                }else if(lightSchemaCard.hasConstraintAt(i,j)){
                    drawConstraint(lightSchemaCard.getConstraintAt(i,j),gc,x,y,cellDim);
                }else{
                    drawWhiteCell(gc,x,y,cellDim);
                }
                x += cellDim;
            }
            x = initX;
            y += cellDim;
        }
    }

    private void drawPrivObj(LightPrivObj privObj, GraphicsContext gc, double x, double y, double imageWidth, double imageHeight) {
       // Image image = new Image(getClass().getResourceAsStream("src"+ File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"1.png"));
       // Image image = new Image(client.class.getResourceAsStream("src"+ File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"1.png"));
        //TODO hookup with resources
        //try (InputStream is = new FileInputStream("src" + File.separator + "img" + File.separator + "PrivObjectiveCard" + File.separator + "1.png")) {
        try (InputStream is = new FileInputStream(privObj.getImgSrc()+".png")) {
            Image img = new Image(is);
            gc.drawImage(img,x,y,imageWidth,imageHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void drawWhiteCell(GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim*LINE_TO_CELL);
        gc.fillRect(x,y,cellDim,cellDim);
        gc.strokeRect(x,y,cellDim,cellDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double dieDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXConstraintColor(constraint.getColor()));
            gc.fillRect(0, 0, dieDim, dieDim);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, dieDim, dieDim);
            drawConstraintSpots(gc,dieDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeRect(0, 0, dieDim, dieDim);
    }

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double cellDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXConstraintColor(constraint.getColor()));
            gc.fillRect(x, y, cellDim, cellDim);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, cellDim, cellDim);
            drawConstraintSpots(gc,x,y,cellDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim*LINE_TO_CELL);
        gc.strokeRect(x, y, cellDim, cellDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext graphicsContext2D, double dieDim) {
        //todo update line width
        graphicsContext2D.setFill(Color.BLACK);
        graphicsContext2D.fillRoundRect(0,0,dieDim,dieDim, DIE_ARC_TO_DIM*dieDim, DIE_ARC_TO_DIM *dieDim);
        graphicsContext2D.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        double lineWidth = 4.5;
        graphicsContext2D.fillRoundRect(lineWidth,lineWidth,dieDim-2*lineWidth,dieDim-2*lineWidth, DIE_ARC_TO_DIM*dieDim, DIE_ARC_TO_DIM*dieDim);
        drawSpots(graphicsContext2D,dieDim,lightDie.getShade().toInt());
    }
    //to be used when drawing schema to canvas
    private void drawDie(LightDie lightDie, GraphicsContext gc, double x, double y, double dieDim) {
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        gc.fillRoundRect(x,y,dieDim,dieDim, DIE_ARC_TO_DIM *dieDim, DIE_ARC_TO_DIM *dieDim);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(DIE_LINE_TO_DIM*dieDim);
       // gc.strokeRoundRect(0,0,dieDim,dieDim, DIE_ARC_TO_DIM*dieDim, DIE_ARC_TO_DIM *dieDim);


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

    private void drawConstraintSpots(GraphicsContext gc, double dieDim, int count) {
        switch (count) {
            case 1:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2,dieDim);
                break;
            case 3:
                drawConstraintSpot(gc, dieDim/ 2, dieDim/ 2,dieDim);
                // Fall thru to next case
            case 2:
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 4,dieDim);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4,dieDim);
                break;
            case 5:
                drawConstraintSpot(gc, dieDim/ 2, dieDim/ 2,dieDim);
                // Fall thru to next case
            case 4:
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 4,dieDim);
                drawConstraintSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim);
                drawConstraintSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim);
                break;
            case 6:
                drawConstraintSpot(gc, dieDim / 4, dieDim/ 4,dieDim);
                drawConstraintSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim);
                drawConstraintSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim);
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 2,dieDim);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 2,dieDim);
                break;
        }
    }

    private void drawSpots(GraphicsContext gc,double xAxisDiePosition,double Y_axis_die_position,double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim/ 4, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim/ 4, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpots(GraphicsContext gc,double xAxisDiePosition,double Y_axis_die_position,double dieDim, int count) {
        switch (count) {
            case 1:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawConstraintSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawConstraintSpot(gc, dieDim/ 2, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawConstraintSpot(gc, dieDim / 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim/ 4, 3 * dieDim/ 4,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim/ 4, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim/ 4, dieDim/ 2,dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(spotDiameter/5);
        gc.fillOval(x - spotDiameter / 2, y - spotDiameter / 2, spotDiameter, spotDiameter);
        gc.strokeOval(x - spotDiameter / 2, y - spotDiameter / 2, spotDiameter, spotDiameter);
    }
    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - spotDiameter / 2, y - spotDiameter / 2, spotDiameter, spotDiameter);
    }

    private void drawSpot(GraphicsContext gc, double x, double y,double dieDim,double xAxisDiePosition,double yAxisDiePosition) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(xAxisDiePosition +(x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y,double dieDim,double xAxisDiePosition,double yAxisDiePosition) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(spotDiameter/5);
        gc.fillOval(xAxisDiePosition +(x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
        gc.strokeOval(xAxisDiePosition +(x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }


}
