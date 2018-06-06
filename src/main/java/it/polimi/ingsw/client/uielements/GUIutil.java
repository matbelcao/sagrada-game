package it.polimi.ingsw.client.uielements;

import javafx.geometry.Rectangle2D;

public class GUIutil {
    //ratio is width/height

    public static final int NUM_COLS = 5;
    public static final int NUM_ROWS = 4;
    private final double SCREEN_WIDTH;
    private final double SCREEN_HEIGHT;
    //login Stage
    private final double LOGIN_TO_SCREEN_RATIO = 0.18;
    private final double LOGIN_RATIO = 0.95;
    //Drafted Schema Stage
    private final double NUM_OF_DRAFTED_SCHEMAS = 4;
    //die
    private final int SCREEN_TO_DIE = 25;
    public  final int DIE_TO_LINE = 10;
    public static final int SPOT_RATIO = 7;


    public GUIutil(Rectangle2D visualBounds) {
        SCREEN_WIDTH = visualBounds.getWidth();
        SCREEN_HEIGHT = visualBounds.getHeight();
    }

    public double getLoginWidth(){
        return SCREEN_WIDTH*LOGIN_TO_SCREEN_RATIO;
    }

    public double getLoginHeight(){
        return getLoginWidth()/LOGIN_RATIO;
    }

    public double getDieDimension(){
        return SCREEN_WIDTH/SCREEN_TO_DIE;
    }

    public double getSchemaWidth(){
        return getDieDimension()*NUM_COLS;
    }

    public double getSchemaHeigth(){
        return getDieDimension()*NUM_ROWS;
    }

    public double getDraftedSchemasWidth(){
        return getSchemaWidth()*2;
    }
    public double getDraftedSchemasHeight(){
        return getSchemaHeigth()*2;
    }

    public int getLineWidth(){
        int lineDim = (int)getDieDimension()/DIE_TO_LINE;
        if(lineDim == 0){
            return 1;
        }else{
            return lineDim;
        }
    }
}
