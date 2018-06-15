package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.GUI;
import it.polimi.ingsw.common.immutables.LightConstraint;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightPrivObj;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
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

public class GUIutil {
    GUI gui;
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
    //die s
    private static final int SCREEN_TO_DIE = 25;
    private static final int DIE_TO_LINE = 10;
    private static final int SPOT_RATIO = 6;

    private static final int LINE_WIDTH = 2;





    public GUIutil(Rectangle2D visualBounds, GUI gui) {
        SCREEN_WIDTH = visualBounds.getWidth();
        SCREEN_HEIGHT = visualBounds.getHeight();
        this.gui = gui;
    }

    public double getLoginWidth(){
        return SCREEN_WIDTH*LOGIN_TO_SCREEN_RATIO;
    }

    public double getLoginHeight(){
        return getLoginWidth()/LOGIN_RATIO;
    }

    private double getDieDimension(){
        return SCREEN_WIDTH/SCREEN_TO_DIE;
    }

    public double getSchemaWidth(){
        return getDieDimension()*NUM_COLS;
    }

    public double getSchemaHeigth(){
        return getDieDimension()*NUM_ROWS;
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

    public int getLineWidth(){
        int lineDim = (int)getDieDimension()/DIE_TO_LINE;
        if(lineDim == 0){
            return 1;
        }else{
            return lineDim;
        }
    }



    public GridPane schemaToGrid(LightSchemaCard lightSchemaCard, double width, double heigth){
        GridPane grid = new GridPane();
        double dieDim = getDieDimension();
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
        GraphicsContext gc = dieCanvas.getGraphicsContext2D();
        drawDie(die,dieCanvas.getGraphicsContext2D(),dieDim);
        return dieCanvas;
    }

    private Canvas lightConstraintToCanvas(LightConstraint constraint, double dieDim){
        Canvas dieCanvas = new Canvas(dieDim,dieDim);
        GraphicsContext gc = dieCanvas.getGraphicsContext2D();
        drawConstraint(constraint,dieCanvas.getGraphicsContext2D(),0,0,dieDim);
        return dieCanvas;
    }

    public Scene waitingForGameStartScene() {
        Text waitingText = new Text("waiting for game to start");
        StackPane p = new StackPane(waitingText);
        return new Scene(p);
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

    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double cellDim) {
        if (constraint.hasColor()) {
            gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(constraint.getColor()));
            gc.fillRect(x, y, cellDim, cellDim);
        }else{
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, cellDim, cellDim);
            drawSpots(gc,x,y,cellDim,constraint.getShade().toInt());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim*LINE_TO_CELL);
        gc.strokeRect(x, y, cellDim, cellDim);
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


    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(x - spotDiameter / 2, y - spotDiameter / 2,
                spotDiameter, spotDiameter);
    }

    private void drawSpot(GraphicsContext gc, double x, double y,double dieDim,double xAxisDiePosition,double yAxisDiePosition) {
        double spotDiameter = dieDim/SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(xAxisDiePosition +(x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }


}
