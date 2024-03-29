package it.polimi.ingsw.client.view.clientui.uielements;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.SchemaCard;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.client.view.clientui.uielements.CLIObjects.*;
import static it.polimi.ingsw.client.view.clientui.uielements.enums.CLIFormats.*;

/**
 * This class contains all utility methods for the cliview class. All methods here are static
 * and can be used to execute different tasks for the CLIObjects.
 */
public class CLIUtils {


    private static final String NIL_CHAR = "\0";
    private static final String BOLD = "\u001B[1m";
    static final int SCHEMA_WIDTH = 35;
    static final int SCHEMA_HEIGHT = 18;
    static final int CELL_HEIGHT = 4;
    static final int CELL_WIDTH = 7;
    static final int OBJ_LENGTH = 42;
    static final int SCREEN_WIDTH = 164;
    private static final String SCREEN_CLEAR ="\033[H\033[2J";
    static final int MENU_WIDTH = 80;
    static final int MENU_HEIGHT = 21;
    static final String FAVOR= "●";
    private static final String ESCAPE="\\u001B\\[([0-9]|([0-9][0-9]))m";

    private static final String GREY = "\u001b[37m";
    private static final String CMD = "cmd";
    private static final String C = "/c";
    private static final String CLS = "cls";
    private static final String IS_NULL = "is null" ;
    private static final String INVALID_NEGATIVE_PARAM = "parameter is invalidly negative";
    private static final String LONGER_THAN_EXPECTED = "already longer than length:";

    static CLIFormatter cliFormatter;

    static {
        try {
            cliFormatter = new CLIFormatter();
        } catch (InstantiationException e) {
            System.exit(2);
        }
    }

    private CLIUtils(){}

    /**
     * this computes the length of a string based on the number of characters that can actually be printed to screen
     * @param line the line to calculate the length of
     * @return the length of the line
     */
    static int printableLength(String line){
        String[] chars=line.split(ESCAPE);
        int length=0;
        for(String part: chars){
            length+=part.length();
        }
        return length;
    }

    /**
     * this prints n bullets to represent the favor tokens
     * @param tokens the number of tokens to print
     * @return the string containing the favors
     */
    static String printFavorTokens(int tokens){
        return replicate(FAVOR,tokens);
    }

    /**
     * this method is used to clean the screen and to make sure the lines of the page are printed starting from the top of the screen
     */
    public static synchronized   String resetScreenPosition() {


        if(Client.isWindows()){
            try {
                new ProcessBuilder(CMD, C, CLS).inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                Logger.getGlobal().log(Level.INFO,e.getMessage());
                System.exit(1);
            }
            return "";
        }

        return SCREEN_CLEAR;
    }

    /**
     * this builds a representation for the dice list of a small die
     * @param die the die to be represented
     * @return the representation of the die
     */
    static String buildSmallDie(CellContent die){
        if(die==null || !die.isDie()){
            throw new IllegalArgumentException();
        }

        if(Client.isWindows()){
            return boldify(addColorToLine(die.getShade().toInt()+"",die.getDieColor()));
        }
            return addColorToLine(die.getShade().getUtf(),die.getDieColor());
    }

    /**
     * this method is used to create a string that represents a list of strings by appending them one to another separated by a newline
     * @param toPrint the list to be put in the string
     * @return said string
     */
    static String printList(List<String> toPrint){
        if(toPrint==null){throw new IllegalArgumentException("toPrint "+IS_NULL);}
        StringBuilder builder=new StringBuilder();
        for(String line : toPrint) {
            builder.append(line).append("%n");
        }
        return builder.toString();
    }

    /**
     * this method is used to fill up the space following a certain string until a defined total length is reached
     * @param toPad the string to be padded
     * @param finalLength the final desired length
     * @param filler the character uset to fill the remaining space
     * @return a list of strings that represent that
     */
    static List<String> padUntil(List<String> toPad,int finalLength, char filler){
        if(toPad==null){ throw new IllegalArgumentException("toPad "+ IS_NULL);}
        List<String> result= new ArrayList<>();
        List<String> fittedLines= fitInWidth(toPad,finalLength);
        for (String line : fittedLines) {
            result.add(padUntil(line, finalLength, filler));
        }
        return result;
    }




    /**
     * creates a rectangle filled with the filler of the  selected sizes
     * @param height the height of the rectangle
     * @param width the width of the rectangle
     * @param filler the character used as a filler
     * @return the wall
     */
    static  List<String> buildWall(int height, int width, char filler){
        List<String> result=new ArrayList<>();
        if(height<=0||width<=0){ return result;}
        for(int row=0;row<height;row++){
            result.add(padUntil(EMPTY_STRING,width,filler));
        }
        return result;
    }
    /**
     * converts a list of light dice in a map of light dice preserving the order
     * @param list the list to convert
     * @return the converted list as a map
     */
    static Map<Integer,LightDie> toMap(List<LightDie> list) {
        if(list==null){
            throw new IllegalArgumentException("list "+ IS_NULL);
        }
        HashMap<Integer, LightDie> map = new HashMap<>();
        for (int i = 0; i<list.size(); i++){
            map.put(i,list.get(i));
        }
        return map;
    }

    /**
     * builds a row of LightDie
     * @param elems the map containing the dice
     * @param from the index where to start from
     * @param to the index where to end(this will not be in the result)
     * @return the representation of the row
     */
    static List<String> buildDiceRow(Map<Integer,LightDie> elems, int from, int to){
        assert(from<=to && from>=0);
        List<String> result=new ArrayList<>();
        for(int i=from;i<to;i++) {
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert (result.size() == CELL_HEIGHT);

        return result;
    }


    /**
     * creates a list of  four strings  that represent a row of cells containing or not dice or constraints
     * @param elems a map containing the elements to be represented
     * @param from the index of the first element to be put inside the result (if an element is not in the Map an empty cell is added)
     * @param to the index of the last one
     * @return the said representation
     */
    static List<String> buildCellRow(Map<Integer,CellContent> elems, int from, int to){
        assert(from<=to && from>=0);
        List<String> result=new ArrayList<>();
        for(int i=from;i<to;i++) {
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert(result.size()==CELL_HEIGHT);
        return result;
    }

    /**
     * creates a string that is the original string aligned to the right in a line of a certain size
     * @param line the line to align
     * @param size the size of the space
     * @return the aligned string
     */
    static String alignRight(String line, int size){
        if(line==null||printableLength(line)>size){ throw new IllegalArgumentException();}
        return padUntil(EMPTY_STRING,size-printableLength(line),SPACE)+line;
    }


    /**
     * this builds a generic schema with the possibility of setting the indexes for both columns and rows
     * @param schema the schema itself
     * @param setIndexes true if indexes are needed
     * @return the representation
     */
    static  List<String> buildSchema(LightSchemaCard schema, boolean setIndexes) {
        List<String> result= new ArrayList<>();

        if(setIndexes){result.add(cliFormatter.getElem(SCHEMA_COLUMNS));}

        for(int row = 0; row< SchemaCard.NUM_ROWS; row++){
            if(setIndexes){
                result.addAll(
                        appendRows(
                                buildSchemaRowsIndex(row),
                                buildCellRow(schema.getCellsMap(),row*SchemaCard.NUM_COLS,(row+1)*SchemaCard.NUM_COLS )));
            }else{
                result.addAll(buildCellRow(schema.getCellsMap(),row*SchemaCard.NUM_COLS,(row+1)*SchemaCard.NUM_COLS ));
            }
        }
        return result;
    }


    /**
     * this builds a column in the form of a list of strings that is the line number for the schema of the user
     * @param row the row of the schema
     * @return said column
     */
    private static List<String> buildSchemaRowsIndex(int row){
        List<String> index= new ArrayList<>();

        for(int i=0; i<CELL_HEIGHT;i++){
            index.add(EMPTY_STRING+SPACE+SPACE);
        }
        index.set(CELL_HEIGHT/2,row+EMPTY_STRING+SPACE);
        return index;
    }


    /**
     * this builds a string that is the syple concatenation of the string to be replicated for n times
     * @param toReplicate the string to be replicated
     * @param times the number of times the string has to be replicated
     * @return the so constructed string
     */
    public static String replicate(String toReplicate,int times){
        return new String (new char[times]).replaceAll(NIL_CHAR,toReplicate);
    }


    /**
     * this method simply renders the passed string bold
     * @param line the line to be rendered bold
     * @return the bold string
     */
    public static String boldify(String line){
        return BOLD +line+DieColor.NONE.getUtf();

    }


    /**
     * this builds the representation of the private objective
     * @param privObj the private objective
     * @param width the width of the line the objective has to be represented in
     * @return the representation in form of a list of strings
     */
    static List<String> buildPrivObj(LightPrivObj privObj, int width) {
        List<String> result=new ArrayList<>();
        List<String> description=buildCard(privObj,width-CELL_WIDTH);
        description.add(0,padUntil(EMPTY_STRING,width-CELL_WIDTH,SPACE));
        result.addAll(
                appendRows(
                        buildCell(new LightConstraint(privObj.getDieColor())),
                        description));
        return result;
    }


    /**
     * this applies random colors from the sagrada game to the lines of a List
     * @param wall a list of strings
     * @return the colored list of strings
     */
    static List<String> colorWall(List<String> wall){
        Random randomGen = new Random();
        for(int row=0 ; row <wall.size();row++){
            wall.set(row,addColorToLine(wall.get(row),DieColor.values()[randomGen.nextInt(DieColor.values().length )]));
        }
        return wall;
    }
    /**
     * this builds a list of strings that represents the cell content
     * @param cellContent the
     * @return the built cell
     */
    static List<String> buildCell(CellContent cellContent) {
        String[] rows=new String [4];

        if(cellContent==null){
            //empty
            rows = splitElem(cliFormatter.getElem(EMPTY));
        }else {
            //not empty

            //die
            if (cellContent.isDie()) {
                rows = splitElem(cliFormatter.getBigDie(cellContent.getShade()));
            }

            //constraint
            if (!cellContent.isDie()) {
                if (cellContent.hasColor()) {

                    //color constraint
                    rows = splitElem(cliFormatter.getElem(FILLED));
                } else {

                    //shade constraint
                    rows = splitElem(cliFormatter.getBigDie(cellContent.getShade()));
                }
            }

            //add color to colored things
            if (cellContent.hasColor()) {
                rows = addColor(rows, cellContent.getDieColor());
            }
        }
        return new ArrayList<>(Arrays.asList(rows));
    }


    /**
     * builds a single generic card(name and description)
     * @param card the card to be represented
     * @return a list that is the card's representation
     */
    static List<String> buildCard( LightCard card,int width) {
        List<String> result=new ArrayList<>();

        result.add(boldify(card.getName()+":"));
        result.addAll(fitInWidth(card.getDescription(), width));
        return result;
    }


    /**
     * this method builds the string representation of a toolcard
     * @param tool the tool to be represented
     * @return the list of strings that represents the tool
     */
    static List<String> buildTool(LightTool tool,int index){
        List<String> result;
        result= buildCard(tool,OBJ_LENGTH);
        if(tool.isUsed()) {
            result.set(0, result.get(0)+SPACE+ FAVOR);
        }
        result.set(0,boldify(String.format(cliFormatter.getElem(LIST_ELEMENT),index,result.get(0))));
        return result;
    }


    /**
     * this changes the color of a line to grey
     * @param line the line
     * @return the greyed out line
     */
    static String greyLine(String line){
        return GREY+removeColor(line)+DieColor.NONE.getUtf();
    }

    /**
     * this removes the colors from a line
     * @param line the line
     * @return the line without colors
     */
    private static String removeColor(String line) {
        return line.replaceAll(ESCAPE,EMPTY_STRING);
    }

    /**
     * this greyes out a list of strings
     * @param lines the strings
     * @return the greyed out strings
     */
    static List<String> greyLines(List<String> lines){
        List<String> result= new ArrayList<>();
        for(String line : lines){
            result.add(greyLine(line));
        }
        return result;
    }
    /**
     * Creates a list containing characters needed to visually separate schemas on the screen
     * @return said list
     */
    static  List<String> buildSeparator(int height){
        if(height<0){
            throw new IllegalArgumentException();
        }
        List<String> separator= new ArrayList<>();
        for(int i=0;i< height;i++){
            separator.add(SPACE+SEPARATOR+SPACE);
        }

        return separator;
    }

    /**
     * appends spaces to a string until a defined length is reached
     * @param toPad the string to be padded
     * @param finalLength the total final length of the padded string
     * @return the padded string
     */
    static String padUntil(String toPad, int finalLength, char filler){
        if(finalLength<0){throw new IllegalArgumentException(INVALID_NEGATIVE_PARAM);}
        if(toPad==null){throw new IllegalArgumentException("toPad"+ IS_NULL);}
        if(printableLength(toPad)>finalLength){throw new IllegalArgumentException(LONGER_THAN_EXPECTED+toPad);}

        return toPad+ replicate(filler+EMPTY_STRING,finalLength - printableLength(toPad));

    }

    /**
     * This method creates a list of string with a fixed maximum length that put together represent the original string
     * @param line the string to be split
     * @param width the desired maximum width
     * @return a list of strings that are parts of the original line
     */
    static List<String> fitInWidth(String line, int width ){
        if(line == null || width <= 0){throw new IllegalArgumentException();}
        List<String> result=new ArrayList<>();

        String lineToFit= line;
        while(printableLength(lineToFit)>width){
            int i = width;

            while(printableLength(lineToFit.substring(0,i))<width){
                i++;
            }

            while(lineToFit.charAt(i) != SPACE){
                i--;
            }
            result.add(lineToFit.substring(0,i));
            lineToFit=lineToFit.substring(i).trim();
        }
        if(printableLength(lineToFit.trim())>=0){
            result.add(padUntil(lineToFit,width,SPACE));
        }
        return result;
    }


    /**
     * this fits the lines in certain width
     * @param lines the lines
     * @param width the width
     * @return the fitted lines
     */
    static List<String> fitInWidth(List<String> lines, int width){
        List<String> result= new ArrayList<>();
        for (int line=0; line<lines.size();line++){
            result.addAll(fitInWidth(lines.get(line),width));
        }
        return result;
    }

    /**
     * Adds the dieColor to the cell being added to the "bigRow"
     * @param rows the rows to be added to the "bigRow"
     * @param dieColor the dieColor to be added
     * @return the new array of string modified as said
     */
    static String [] addColor(String[] rows, DieColor dieColor) {
        String [] result= new String[rows.length];
        if(rows.length==0){throw new IllegalArgumentException();}
        for(int i=0;i<rows.length;i++){
            result[i] = addColorToLine(rows[i], dieColor);
        }
        return result;
    }

    /**
     * returns a copy of the string with added colors
     * @param line the string to apply dieColor to
     * @param dieColor the dieColor to apply
     * @return the colored string
     */
    static String addColorToLine(String line, DieColor dieColor){
        return dieColor.getUtf()+line+DieColor.NONE.getUtf();
    }

    /**
     * appends two lists of strings one  by one one to the other to create a new list (number of strings) if b is longer than a
     * a will be padded to be as long as b with a wall of whitespaces of the same width as the last line of the original a
     * @param a the list of strings that will come first in the resulting list
     * @param b the list of strings being appended to a
     * @return the result of appending, string by string, a to b
     */
    static List<String> appendRows(List<String> a, List<String> b){
        if(a==null ){throw new IllegalArgumentException("a "+IS_NULL);}
        if( b==null){ throw new IllegalArgumentException("b "+IS_NULL);}
        List<String> tempA= new ArrayList<>(a);
        List<String> tempB= new ArrayList<>(b);
        List<String> result= new ArrayList<>();
        if(tempA.isEmpty()){
            result.addAll(tempB);
        }else{
            if(tempA.size()<tempB.size()){
                tempA.addAll(buildWall(tempB.size()-tempA.size(), tempA.get(tempA.size()-1).length(), SPACE));
            }

            for(int row=0; row<tempA.size();row++){
                if(row<tempB.size()) {
                    result.add(row,tempA.get(row)+tempB.get(row));
                }else{
                    result.add(row,tempA.get(row));
                }
            }
        }
        return result;
    }

    /**
     * this method splits the element read from an xml file by on "::"
     * @param elem the string to be split
     * @return the array of strings containing the parts of the elem divided by "::"
     */
    private static String[] splitElem(String elem){
        return elem.split(DOUBLE_COLON);
    }



}
