package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.common.serializables.CellContent;
import it.polimi.ingsw.common.serializables.LightConstraint;
import it.polimi.ingsw.common.serializables.LightDie;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class represents a cell where both dice and constraints and borders can be placed, removed or hidden
 * */
public class DieContainer extends StackPane{
    private static final double ROUNDTRACK_TEXT_SIZE_TO_CELL = 0.7;
    private static final double BORDER_LINE_TO_CELL = 0.12;
    private static final double DIE_DIM_TO_CELL_DIM = 0.9;
    private static final double DIE_ARC_TO_DIM = 0.35;
    private static final double BORDER_LINE_TO_DIE = 0.02; //0.045
    private static final int SPOT_RATIO = 6;


    private double dieDim;
    private Rectangle outerRect;
    private Rectangle innerRect;
    private Text indexText;
    private Canvas content;

    /**
     * Constructor of the class
     * @param cellDim the dimension of the outer border of the die container
     */
    DieContainer(double cellDim){
        this.dieDim = cellDim*DIE_DIM_TO_CELL_DIM;
        this.outerRect = new Rectangle(0, 0, cellDim, cellDim);
        this.indexText = new Text();
        this.content = new Canvas(dieDim,dieDim);
        double lineThickness = cellDim * BORDER_LINE_TO_CELL;
        double innerCellDim = cellDim - lineThickness;
        this.innerRect = new Rectangle(lineThickness, lineThickness, innerCellDim, innerCellDim);
        outerRect.setFill(Color.BLACK);
        innerRect.setFill(Color.WHITE);
        this.getChildren().addAll(outerRect,innerRect,indexText,content);
    }

    /**
     * Overloaded constructor that allows to draw an index at the bottom of the container, this method is used only bty roundtrack cells
     * @param index the index to be drawn
     * @param cellDim the dimension of the outer border of the die container
     */
    DieContainer(int index, double cellDim){
        this(cellDim);
        int displayedIndex = index + 1;
        double textSize = ROUNDTRACK_TEXT_SIZE_TO_CELL * cellDim;
        indexText.setText(displayedIndex + "");
        indexText.setFont(Font.font("Verdana", textSize));
        indexText.setFill(Color.BLACK);
    }

    /**
     * Overloaded constructor that allows to build the cell with the dice or constarint already drawn inside
     * @param cellDim the dimension of the outer border of the die container
     */
    DieContainer(CellContent cellContent, double cellDim) {
        this(cellDim);
        this.content = indexedCellToCanvas(cellContent,dieDim);
        this.getChildren().add(content);
    }

    /**
     * This method allows to draw two halves of respectively two dices in the cell
     * @param die1 the die where the first half will be taken
     * @param die2 the die where the second half will be taken
     */
    public void putDoubleDice(LightDie die1, LightDie die2) {
        GraphicsContext gc = content.getGraphicsContext2D();
        drawDie(die1, gc,0,0, dieDim);
        drawDie(die2, gc, dieDim / 2, 0, dieDim);
    }

    /**
     * This method allows to draw a dice in the cell
     * @param lightDie the dice to be drawn
     */
    public void putDie(LightDie lightDie) {
        drawDie(lightDie,content.getGraphicsContext2D(),0,0,dieDim);
    }

    /**
     * This method allows to draw a constraint in the cell
     * @param constraintAt the constraint to be drawn
     */
    public void putConstraint(LightConstraint constraintAt) {
        drawConstraint(constraintAt,content.getGraphicsContext2D(),0,0,dieDim);
    }

    /**
     * This method draws the CellContent of an IndexCellContent in a Canvas
     * @param cellContent the CellContent to be drawn
     * @param dieDim the dimension of the cell
     * @return the drawn Canvas
     */
    private Canvas indexedCellToCanvas(CellContent cellContent, double dieDim) {
        Canvas canvas = new Canvas(dieDim, dieDim);
        if (cellContent.isDie()) {
            drawDie(cellContent.getDieColor(), cellContent.getShade(), canvas.getGraphicsContext2D(), 0, 0, dieDim);
        } else {
            drawConstraint(cellContent, canvas.getGraphicsContext2D(), 0, 0, dieDim);
        }
        return canvas;
    }

    private void drawConstraint(CellContent cell, GraphicsContext gc, double x, double y, double cellDim) {
        if (!cell.isDie()) {
            if (cell.hasColor()) {
                drawColorConstraint(cell.getDieColor(), gc, x, y, cellDim);
            } else {
                drawShadeConstraint(cell.getShade(), gc, x, y, cellDim);
            }
        }
    }

    private void drawDie(LightDie lightDie, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = BORDER_LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(Color.web(lightDie.getDieColor().getFXColor()));
        gc.fillRoundRect(x+lineWidth, y+lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, lightDie.getShade().toInt());
    }

    private void drawDie(DieColor dieColor, Shade shade, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = BORDER_LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(Color.web(dieColor.getFXColor()));
        gc.fillRoundRect(x + lineWidth, y + lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, shade.toInt());
    }


    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double cellDim) {
        if (constraint.hasColor()) {
            drawColorConstraint(constraint.getDieColor(), gc, x, y, cellDim);
        } else {
            drawShadeConstraint(constraint.getShade(), gc, x, y, cellDim);
        }
    }

    /**
     * This method draws a shade constraint in a graphic context
     * @param shade the shade to be drawn
     * @param gc the graphic context in witch draw
     * @param x x_axis of the beginning of the drawing in canvas
     * @param y y_axis of the beginning of the drawing in canvas
     * @param cellDim the dimension of the Canvas whose Graphic Context gets drawn
     */
    private void drawShadeConstraint(Shade shade, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, cellDim, cellDim);
        drawConstraintSpots(gc, x, y, cellDim, shade.toInt());
        gc.setStroke(Color.BLACK);
    }

    /**
     * This method draws a color constraint in a graphic context
     * @param dieColor the color to be drawn
     * @param gc  the graphic context in witch draw
     * @param x x_axis of the beginning of the drawing in canvas
     * @param y y_axis of the beginning of the drawing in canvas
     * @param cellDim the dimension of the Canvas whose Graphic Context gets drawn
     */
    private void drawColorConstraint(DieColor dieColor, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.web(dieColor.getFXConstraintColor()));
        gc.fillRect(x, y, cellDim, cellDim);
        gc.setStroke(Color.BLACK);
    }

    /**
     * This method draws the spots of dice
     * @param gc the graphic context in witch draw
     * @param xAxisDiePosition x_axis of the beginning of the drawing in canvas
     * @param yAxisDiePosition y_axis of the beginning of the drawing in canvas
     * @param dieDim the dimension of the die in the Canvas whose Graphic Context gets drawn
     * @param count the number of spots to be drawn
     */
    private void drawSpots(GraphicsContext gc, double xAxisDiePosition, double yAxisDiePosition, double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 3:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;

            case 2:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 5:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            default:
                break;
        }
    }

    /**
     * This method draws the spots of the shade constraint
     * @param gc the graphic context in witch draw
     * @param xAxisDiePosition x_axis of the beginning of the drawing in the Canvas
     * @param yAxisDiePosition y_axis of the beginning of the drawing in the Canvas
     * @param dieDim the dimension of the die in the Canvas whose Graphic Context gets drawn
     * @param count the number of spots to be drawn
     */
    private void drawConstraintSpots(GraphicsContext gc, double xAxisDiePosition, double yAxisDiePosition, double dieDim, int count) {
        switch (count) {
            case 1:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 3:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;

            case 2:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 5:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 4:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            case 6:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, yAxisDiePosition);
                break;
            default:
                break;
        }
    }

    /**
     * This method draws a single spot of a constraint
     * @param gc the graphic context in witch draw
     * @param x x_axis of the beginning of the drawing in the Canvas
     * @param y y_axis of the beginning of the drawing in the Canvas
     * @param dieDim the dimension of the die in the Canvas whose Graphic Context gets drawn
     * @param xAxisDiePosition the x-axis position of the die whose spot is being drawn respect to the Canvas where it's being drawn
     * @param yAxisDiePosition the y-axis position of the die whose spot is being drawn respect to the Canvas where it's being drawn
     */
    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(spotDiameter / 5);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
        gc.strokeOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }
    /**
     * This method draws a single spot of a die
     * @param gc the graphic context in witch draw
     * @param x x_axis of the beginning of the drawing in the Canvas
     * @param y y_axis of the beginning of the drawing in the Canvas
     * @param dieDim the dimension of the die in the Canvas whose Graphic Context gets drawn
     * @param xAxisDiePosition the x-axis position of the die whose spot is being drawn respect to the Canvas where it's being drawn
     * @param yAxisDiePosition the y-axis position of the die whose spot is being drawn respect to the Canvas where it's being drawn
     */
    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }

    /**
     * This method highlights the borders of a DieContainer in blue
     */
    public void highlightBlue() { outerRect.setFill(Color.LIGHTSKYBLUE); }

    /**
     * This method highlights the borders of a DieContainer in green
     */
    public void highlightGreen() { outerRect.setFill(Color.LIGHTGREEN); }

    /**
     * This method highlights the borders of a DieContainer in orange
     */
    public void highlightOrange() { outerRect.setFill(Color.ORANGE); }

    /**
     * This method hides the borders of cell leaving only the die showing
     */
    public void hideCellBorders() {
        outerRect.setFill(Color.TRANSPARENT);
        innerRect.setFill(Color.TRANSPARENT);
    }
}