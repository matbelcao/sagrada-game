package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.common.enums.Place;
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


public class Cell extends StackPane{
    private static final double ROUNDTRACK_TEXT_SIZE_TO_CELL = 0.7;
    private static final double BORDER_LINE_TO_CELL = 0.12;
    private static final double DIE_DIM_TO_CELL_DIM = 0.9;
    private static final double DIE_ARC_TO_DIM = 0.35;
    private static final double BORDER_LINE_TO_DIE = 0.02; //0.045
    private static final int SPOT_RATIO = 6;


    private double cellDim;
    private double dieDim;
    private Rectangle outerRect;
    private Rectangle innerRect;
    private Text indexText;
    private Canvas content;

    public Cell(double cellDim){
        this.cellDim = cellDim;
        this.dieDim = cellDim*DIE_DIM_TO_CELL_DIM;
        this.outerRect = new Rectangle(0, 0, cellDim, cellDim);
        this.indexText = new Text();
        this.content = new Canvas(dieDim,dieDim);
        double lineWidth = cellDim * BORDER_LINE_TO_CELL;
        double innerCellDim = cellDim - lineWidth;
        this.innerRect = new Rectangle(lineWidth, lineWidth, innerCellDim, innerCellDim);
        outerRect.setFill(Color.BLACK);
        innerRect.setFill(Color.WHITE);
        this.getChildren().addAll(outerRect,innerRect,indexText,content);
    }
    public Cell(int index, double cellDim){
        this(cellDim);
        int displayedIndex = index + 1;
            double textSize = ROUNDTRACK_TEXT_SIZE_TO_CELL * cellDim;
            indexText.setText(displayedIndex + "");
            indexText.setFont(Font.font("Verdana", textSize));
            indexText.setFill(Color.BLACK);
    }

    public Cell(double cellDim, Place place) {
        this(cellDim);
        switch (place){
            case DRAFTPOOL:
                this.hideCellBorders();
                break;
        }
    }

    public Cell(CellContent cellContent, double cellDim) {
        this(cellDim);
        this.content = indexedCellToCanvas(cellContent,dieDim);
        this.getChildren().add(content);
    }

    public void putDoubleDice(LightDie die1, LightDie die2) {
        GraphicsContext gc = content.getGraphicsContext2D();
        drawDie(die1, gc,0,0, dieDim);
        drawDie(die2, gc, dieDim / 2, 0, dieDim);
    }

    public void putDie(LightDie lightDie) {
        drawDie(lightDie,content.getGraphicsContext2D(),0,0,dieDim);
    }

    public void putConstraint(LightConstraint constraintAt) {
       drawConstraint(constraintAt,content.getGraphicsContext2D(),0,0,dieDim);
    }

    private Canvas indexedCellToCanvas(CellContent cellContent, double dieDim) {
        Canvas canvas = new Canvas(dieDim, dieDim);
        if (cellContent.isDie()) {
            drawDie(cellContent.getColor(), cellContent.getShade(), canvas.getGraphicsContext2D(), 0, 0, dieDim);
        } else {
            drawConstraint(cellContent, canvas.getGraphicsContext2D(), 0, 0, dieDim);
        }
        return canvas;
    }

    private void drawConstraint(CellContent cell, GraphicsContext gc, double x, double y, double cellDim) {
        if (cell.isDie()) {
            return;
        } else {
            if (cell.hasColor()) {
                drawColorConstraint(cell.getColor(), gc, x, y, cellDim);
            } else {
                drawShadeConstraint(cell.getShade(), gc, x, y, cellDim);
            }
        }
    }

    private void drawDie(LightDie lightDie, GraphicsContext graphicsContext2D, double dieDim) {
        drawDie(lightDie, graphicsContext2D, 0, 0, dieDim);
    }

    private void drawDie(LightDie lightDie, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = BORDER_LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(lightDie.getColor()));
        gc.fillRoundRect(x+lineWidth, y+lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, lightDie.getShade().toInt());
    }

    private void drawDie(it.polimi.ingsw.common.enums.Color color, Shade shade, GraphicsContext gc, double x, double y, double dieDim) {
        double lineWidth = BORDER_LINE_TO_DIE * dieDim;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, dieDim, dieDim, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXColor(color));
        gc.fillRoundRect(x + lineWidth, y + lineWidth, dieDim - 2 * lineWidth, dieDim - 2 * lineWidth, DIE_ARC_TO_DIM * dieDim, DIE_ARC_TO_DIM * dieDim);
        drawSpots(gc, x, y, dieDim, shade.toInt());
    }


    private void drawConstraint(LightConstraint constraint, GraphicsContext gc, double x, double y, double cellDim) {
        if (constraint.hasColor()) {
            drawColorConstraint(constraint.getColor(), gc, x, y, cellDim);
        } else {
            drawShadeConstraint(constraint.getShade(), gc, x, y, cellDim);
        }
    }

    private void drawShadeConstraint(Shade shade, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, cellDim, cellDim);
        drawConstraintSpots(gc, x, y, cellDim, shade.toInt());
        gc.setStroke(Color.BLACK);
    }

    private void drawColorConstraint(it.polimi.ingsw.common.enums.Color color, GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(it.polimi.ingsw.common.enums.Color.toFXConstraintColor(color));
        gc.fillRect(x, y, cellDim, cellDim);
        gc.setStroke(Color.BLACK);
    }

    private void drawSpots(GraphicsContext gc, double dieDim, int count) {
        drawSpots(gc, 0, 0, dieDim, count);
    }

    private void drawSpots(GraphicsContext gc, double xAxisDiePosition, double Y_axis_die_position, double dieDim, int count) {
        switch (count) {
            case 1:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpots(GraphicsContext gc, double dieDim, int count) {
        drawConstraintSpots(gc, 0, 0, dieDim, count);
    }

    private void drawConstraintSpots(GraphicsContext gc, double xAxisDiePosition, double Y_axis_die_position, double dieDim, int count) {
        switch (count) {
            case 1:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 3:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 2:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 5:
                drawConstraintSpot(gc, dieDim / 2, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                // Fall thru to next case
            case 4:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
            case 6:
                drawConstraintSpot(gc, dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, 3 * dieDim / 4, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                drawConstraintSpot(gc, 3 * dieDim / 4, dieDim / 2, dieDim, xAxisDiePosition, Y_axis_die_position);
                break;
        }
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim) {
        drawConstraintSpot(gc, x, y, dieDim, 0, 0);
    }

    private void drawConstraintSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(spotDiameter / 5);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
        gc.strokeOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }

    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim) {
        drawSpot(gc, x, y, dieDim, 0, 0);
    }

    private void drawSpot(GraphicsContext gc, double x, double y, double dieDim, double xAxisDiePosition, double yAxisDiePosition) {
        double spotDiameter = dieDim / SPOT_RATIO;
        gc.setFill(Color.BLACK);
        gc.fillOval(xAxisDiePosition + (x - spotDiameter / 2), yAxisDiePosition + (y - spotDiameter / 2), spotDiameter, spotDiameter);
    }
    private void drawWhiteCell(GraphicsContext gc, double x, double y, double cellDim) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(cellDim * BORDER_LINE_TO_CELL);
        gc.fillRect(x, y, cellDim, cellDim);
        gc.strokeRect(x, y, cellDim, cellDim);
    }

    public void highlightGreen() {
        outerRect.setFill(Color.GREEN);
    }

    public void highlightBlue() {
        outerRect.setFill(Color.BLUE);
    }

    public void hideCellBorders() {
        outerRect.setFill(Color.TRANSPARENT);
        innerRect.setFill(Color.TRANSPARENT);
    }
}
