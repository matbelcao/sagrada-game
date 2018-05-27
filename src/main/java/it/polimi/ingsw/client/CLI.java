package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.CLIElems;
import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.immutables.CellContent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CLI implements ClientUI{
    private QueuedInReader inKeyboard;
    //private BufferedReader inKeyboard;
    private PrintWriter outCli;
    private Client client;
    private UIMessages uimsg;
    private CLIElems cliElems;

    public CLI(Client client,UILanguage lang) {

        this.cliElems= new CLIElems();
        this.uimsg=new UIMessages(lang);
        this.client = client;
        inKeyboard = new QueuedInReader(new BufferedReader(new InputStreamReader(System.in)));
        //inKeyboard=new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    public void loginProcedure() {
        String username;
        String password;
        try {

            outCli.printf("%n%nUSERNAME: ");
            inKeyboard.add();
            username = inKeyboard.getln();

            //username=inKeyboard.readLine();

            outCli.printf("%nPASSWORD: ");
            inKeyboard.add();
            password = inKeyboard.getln();
            //password=inKeyboard.readLine();

            client.setPassword(password);
            client.setUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            client.disconnect();
        }
    }


    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.printf(String.format("%n%s", uimsg.getMessage("login-ok")), client.getUsername());
        } else {
            outCli.printf(String.format("%n%s", uimsg.getMessage("login-ko")));
        }
    }

    public void updateConnectionOk() { outCli.printf(String.format("%n%s", uimsg.getMessage("connection-ok"))); }

    public void updateLobby(int numUsers){
        outCli.printf(String.format("%n%s", uimsg.getMessage("lobby-update")),numUsers);
    }

    public void updateGameStart(int numUsers, int playerId){
        outCli.printf(String.format("%n%s", uimsg.getMessage("game-start")),numUsers,playerId);
    }

    @Override
    public void updateStatusMessage(String statusChange, int playerid) {

    }


    public void updateConnectionClosed()
    {
        outCli.println("Connection closed!");
    }

    public void updateConnectionBroken() { outCli.println("Connection broken!");
    }

    public void printmsg(String msg){
        outCli.println(msg);
    }

    public List<String> buildBigRow(Map<Integer,CellContent> elems, int from, int to){
        assert(from<=to && from>=0);
        ArrayList<String> result=new ArrayList<>();
        result.add(new String());
        result.add(new String());
        result.add(new String());
        result.add(new String());
        String [] rows;
        rows = new String[4];
        for(int i=from;i<=to;i++) {
            if (elems.containsKey(i)) {
                if (elems.get(i).isDie()) {
                    rows = splitRows(cliElems.getBigDie(elems.get(i).getShade().toString()));
                    for (int row = 0; row < rows.length; row++) {
                        rows[row] = new String(elems.get(i).getColor().getUtf() + rows[row] + Color.RESET);
                    }
                } else {
                    if (!elems.get(i).isDie() && elems.get(i).hasColor()) {
                        rows = splitRows(cliElems.getBigDie("FILLED"));
                        for (int row = 0; row < rows.length; row++) {
                            rows[row] = new String(elems.get(i).getColor().getUtf() + rows[row] + Color.RESET);
                        }
                    } else if(!elems.get(i).hasColor()){
                        rows = splitRows(cliElems.getBigDie(elems.get(i).getShade().toString()));
                    }

                }

            }else{
                rows = splitRows(cliElems.getBigDie("EMPTI"));
            }
            for(int row=0;row<rows.length;row++){
                result.set(row,result.get(row)+rows[row]);
            }
        }
        return result;
    }

    public List<String> appendBigRows(List<String> a,List<String> b){
        ArrayList<String> result= new ArrayList<>();
        for(int row=0; row<a.size();row++){
            result.add(row,a.get(row)+b.get(row));
        }
        return result;
    }

    private String[] splitRows(String elem){
        return elem.split("::");
    }
    @Override
    public String getCommand() {
        //String s="";
        if(inKeyboard.isEmpty()) {
            inKeyboard.add();
        }

        return inKeyboard.getln();
        /*try {
            s=inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //return s;
    }
}


