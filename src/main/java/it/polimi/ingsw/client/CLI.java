package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightPrivObj;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.common.immutables.LightTool;
import org.fusesource.jansi.AnsiConsole;
import java.io.Console;
import java.util.List;
import java.util.Map;

public class CLI implements ClientUI{
    private final CLIView view;
    private Console console;
    private Client client;
    private UIMessages uimsg;

    public CLI(Client client,UILanguage lang) throws InstantiationException {

        this.console=System.console();

        if (console == null) {
            System.err.println("ERR: couldn't retrieve any console!");
            System.exit(1);
        }
        AnsiConsole.systemInstall();

        this.uimsg=new UIMessages(lang);
        this.client = client;
        this.view=new CLIView(lang);
    }

    @Override
    public void showLoginScreen() {
        String username;
        char [] password;

        try {
            console.printf(view.showLoginUsername());
            username=console.readLine().trim();

            console.printf(view.showLoginPassword());
            password= Credentials.hash(username,console.readPassword());

            client.setPassword(password);
            client.setUsername(username);
        } catch (Exception e) {
            client.disconnect();
        }
    }


    @Override
    public void updateLogin(boolean logged) {
        if (logged) {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ok")), client.getUsername());
        } else {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ko")));
        }
    }


    @Override
    public void updateConnectionOk() { console.printf(String.format("%n%s", uimsg.getMessage("connection-ok"))); }

    @Override
    public void updateLobby(int numUsers){
        console.printf(CLIView.resetScreenPosition());
        console.printf(String.format("%s%n", uimsg.getMessage("lobby-update")),numUsers);
    }

    @Override
    public void updateGameStart(int numUsers, int playerId){
        console.printf(String.format("%s%n", uimsg.getMessage("game-start")),numUsers,playerId);
        this.view.setMatchInfo(client.getPlayerId(),client.getBoard().getNumPlayers());
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        view.updateDraftedSchemas(draftedSchemas);
        view.updatePrivObj(privObj);
        console.printf(view.printSchemaChoiceView());
    }

    @Override
    public void updateBoard(LightBoard board) {

    }

    @Override
    public void updateDraftPool(Map<Integer, LightDie> draftpool) {

    }

    @Override
    public void updateSchema(LightSchemaCard schema, int playerId) {

    }

    @Override
    public void updateRoundTrack(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void showRoundTrackWithCoordinates(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void updateRoundStart(int numRound,List<List<LightDie>> roundtrack){
        console.printf(String.format("%s%n", uimsg.getMessage("round")),numRound);
    }

    @Override
    public void updateTurnStart(int playerId, boolean isFirstTurn, Map<Integer,LightDie> draftpool) {

    }

    @Override
    public void updateToolUsage(List<LightTool> tools) {

    }

    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

    }


    @Override
    public void updateConnectionClosed()
    {
        console.printf("Connection closed!%n");
    }

    @Override
    public void updateConnectionBroken() { console.printf("Connection broken!%n");
    }

    @Override
    public void printmsg(String msg){
        console.printf(msg);
    }

    @Override
    public String getCommand() {
        return console.readLine();
    }
}


