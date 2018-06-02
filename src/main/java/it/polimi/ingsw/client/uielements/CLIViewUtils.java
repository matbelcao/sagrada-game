package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * This class contains all utility methods for the cliview class. All methods here are static
 * and can be used to execute different tasks for the CLIView.
 */
public class CLIViewUtils {
    private CLIViewUtils(){}


    static final int SCHEMA_WIDTH = 35;
    static final int SCHEMA_HEIGHT = 18;
    static final int CELL_HEIGHT = 4;
    static final int CELL_WIDTH = 7;
    static final int OBJ_LENGTH = 38;
    static final int SCREEN_WIDTH = 160;
    static final int SCREEN_CLEAR =100;
    static final int MENU_WIDTH = 80;
    static final int MENU_HEIGHT = 21;
    static final String FAVOR= "\u001B[31m"+"╳"+"\u001B[0m";

    static CLIElems cliElems;

    static {
        try {
            cliElems = new CLIElems();
        } catch (InstantiationException e) {
            System.exit(2);
        }
    }

    static String printFavorTokens(int tokens){
        return replicate(FAVOR,tokens);
    }

    /**
     * this method is used to clena the screen and to make sure the lines of the page are printed starting from the top of the screen
     */
    public static String resetScreenPosition() {
        StringBuilder builder= new StringBuilder();
        Client.getOsName();
        if(Client.isWindows()){
            builder.append(ansi().eraseScreen());
            builder.append(ansi().restoreCursorPosition());
        }
        else {
            builder.append(replicate("%n",SCREEN_CLEAR));
            builder.append(replicate("\u001b[A",SCREEN_CLEAR));
        }
        return builder.toString();
    }
    /**
     * this method is used to create a string that represents a list of strings by appending them one to another separated by a newline
     * @param toPrint the list to be put in the string
     * @return said string
     */
    static String printList(List<String> toPrint){
        if(toPrint==null){throw new IllegalArgumentException();}
        StringBuilder builder=new StringBuilder();
        for(String line : toPrint) {
            builder.append(line).append("%n");
        }
        return builder.toString();
    }

    /**
     * this method is used to fill up the space following a certain string until a defined total length is reached
     * @param toPad the string to be padded
     * @param finalLenght the final desired length
     * @param filler the character uset to fill the remaining space
     * @return a list of strings that represent that
     */
    static List<String> padUntil(List<String> toPad,int finalLenght, char filler){
        List<String> result= new ArrayList<>();
        for(int i=0; i<toPad.size();i++){
            result.add(padUntil(toPad.get(i),finalLenght,' '));
        }
        return result;
    }



    /**
     * creates a rectangle filled with the filler of the  selected sizes
     * @param filler the character used as a filler
     * @param height the height of the rectangle
     * @param width the width of the rectangle
     * @return the wall
     */
    static  List<String> buildWall(char filler,int height, int width){
        if(height<0||width<0){ throw new IllegalArgumentException();}

        List<String> result=new ArrayList<>();
        for(int row=0;row<height;row++){
            result.add(padUntil("",width,filler));
        }
        return result;
    }
    /**
     * converts a list of light dice in a map of light dice preserving the order
     * @param list the list to convert
     * @return the converted list as a map
     */
    static Map<Integer,LightDie> listToMap(List<LightDie> list) {
        if(list==null){
            throw new IllegalArgumentException();
        }
        HashMap<Integer, LightDie> map = new HashMap<>();
        for (int i = 0; i<list.size();i++){
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
            //empty cell
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert (result.size() == 4);
        //append new cell/die/constraint

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
            //append new cell/die/constraint
            result = appendRows(result, buildCell(elems.get(i)));
        }
        assert(result.size()==4);
        return result;
    }

    /**
     * creates a string that is the original string aligned to the right in a line of a certain size
     * @param line the line to align
     * @param size the size of the space
     * @return the aligned string
     */
    static String alignRight(String line, int size){
        if(line==null||line.length()>size){ throw new IllegalArgumentException();}
        return padUntil("",size-line.length(),' ')+line;
    }


    /**
     * this builds a generic schema with the possibility of setting the indexes for both columns and rows
     * @param schema the schema itself
     * @param setIndexes true if indexes are needed
     * @return the representation
     */
    static  List<String> buildSchema(LightSchemaCard schema, boolean setIndexes) {
        List<String> result= new ArrayList<>();

        if(setIndexes){result.add(cliElems.getElem("schema-cols"));}

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
    static List<String> buildSchemaRowsIndex(int row){
        List<String> index= new ArrayList<>();

        for(int i=0; i<CELL_HEIGHT;i++){
            index.add("  ");
        }
        index.set(CELL_HEIGHT/2,row+" ");
        return index;
    }


    /**
     * this builds a string that is the syple concatenation of the string to be replicated for n times
     * @param toReplicate the string to be replicated
     * @param times the number of times the string has to be replicated
     * @return the so constructed string
     */
    public static String replicate(String toReplicate,int times){
        return new String (new char[times]).replaceAll("\0",toReplicate);
    }


    /**
     * this method simply renders the passed string bold
     * @param line the line to be rendered bold
     * @return the bold string
     */
    public static String bold(String line){
        return "\u001B[1m"+line+Color.RESET;

    }


    /**
     * this builds the representation of the private objective
     * @param privObj the private objective
     * @param width the width of the line the objective has to be represented in
     * @return the representation in form of a list of strings
     */
    static List<String> buildPrivObj(LightPrivObj privObj, int width) {
        List<String> result=new ArrayList<>();
        result.add(" ");
        result.add(bold(privObj.getName()+":"));
        result.addAll(fitInLength(privObj.getDescription(), width));
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
            wall.set(row,addColorToLine(wall.get(row),Color.values()[randomGen.nextInt(Color.values().length )]));
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
            rows = splitElem(cliElems.getBigDie("EMPTI"));
        }else {
            //not empty

            //die
            if (cellContent.isDie()) {
                rows = splitElem(cliElems.getBigDie(cellContent.getShade().toString()));
            }

            //constraint
            if (!cellContent.isDie()) {
                if (cellContent.hasColor()) {

                    //color constraint
                    rows = splitElem(cliElems.getBigDie("FILLED"));
                } else {

                    //shade constraint
                    rows = splitElem(cliElems.getBigDie(cellContent.getShade().toString()));
                }
            }

            //add color to colored things
            if (cellContent.hasColor()) {
                rows = addColor(rows, cellContent.getColor());
            }
        }
        return new ArrayList<>(Arrays.asList(rows));
    }


    /**
     * builds a single generic card(name and description)
     * @param card the card to be represented
     * @return a list that is the card's representation
     */
    static List<String> buildCard( LightCard card) {
        List<String> result=new ArrayList<>();

        result.add(bold(card.getName()+":"));
        result.addAll(fitInLength(card.getDescription(), OBJ_LENGTH));
        return result;
    }


    /**
     * this method builds the string representation of a toolcard
     * @param tool the tool to be represented
     * @return the list of strings that represents the tool
     */
    static List<String> buildTool(LightTool tool){
        List<String> result;
        result= buildCard(tool);
        if(tool.isUsed()) {
            result.set(0, result.get(0)+" "+ FAVOR);
        }
        return result;
    }





    /**
     * Creates a list containing characters needed to visually separate schemas on the screen
     * @return said list
     */
    static  List<String> buildSeparator(int height){
        List<String> separator= new ArrayList<>();
        for(int i=0;i< height;i++){
            separator.add(" | ");
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
        if(finalLength<0|| toPad==null||toPad.length()>finalLength){throw new IllegalArgumentException();}

        return toPad+ replicate(filler+"",finalLength - toPad.length());

    }

    /**
     * This method creates a list of string with a fixed maximum length that put together represent the original string
     * @param line the string to be split
     * @param length the desired maximum length
     * @return a list of strings that are parts of the original line
     */
    static List<String> fitInLength(String line, int length ){
        if(line == null || length <= 0){throw new IllegalArgumentException();}
        List<String> result=new ArrayList<>();

        String lineToFit= line;
        while(lineToFit.length()>length){
            int i = length;
            while(lineToFit.charAt(i) != ' '){
                i--;
            }
            result.add(lineToFit.substring(0,i));
            lineToFit=lineToFit.substring(i).trim();
        }
        if(lineToFit.trim().length()>=0){
            result.add(padUntil(lineToFit,length,' '));
        }
        return result;
    }


    /**
     * Adds the color to the cell being added to the "bigRow"
     * @param rows the rows to be added to the "bigRow"
     * @param color the color to be added
     * @return the new array of string modified as said
     */
    static String [] addColor(String[] rows, Color color) {
        String [] result= new String[rows.length];
        if(rows.length==0){throw new IllegalArgumentException();}
        for(int i=0;i<rows.length;i++){
            result[i] = addColorToLine(rows[i],color);
        }
        return result;
    }

    /**
     * returns a copy of the string with added colors
     * @param line the string to apply color to
     * @param color the color to apply
     * @return the colored string
     */
    static String addColorToLine(String line, Color color){
        return color.getUtf()+line+Color.RESET;
    }

    /**
     * appends two lists of strings one  by one one to the other to create a new list (number of strings)
     * @param a the list of strings that will come first in the resulting list
     * @param b the list of strings being appended to a
     * @return the result of appending, string by string, a to b
     */
    static List<String> appendRows(List<String> a, List<String> b){
        if(a==null || b==null || (!a.isEmpty() && a.size()<b.size())){
            throw new IllegalArgumentException();
        }

        List<String> result= new ArrayList<>();
        if(a.isEmpty()){
            result.addAll(b);
        }else{
            assert(a.size()>=b.size());
            for(int row=0; row<a.size();row++){
                if(row<b.size()) {
                    result.add(row,a.get(row)+b.get(row));
                }else{
                    result.add(row,a.get(row));
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
    static String[] splitElem(String elem){
        return elem.split("::");
    }



}
