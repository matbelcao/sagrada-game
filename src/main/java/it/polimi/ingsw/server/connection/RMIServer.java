package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.common.immutables.CellContent;
import it.polimi.ingsw.common.immutables.LightConstraint;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.server.model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RMIServer extends UnicastRemoteObject implements ServerConn,RMIServerInt {
        private RMIClientInt clientReference;
        private User user;

    public RMIServer(User user) throws RemoteException{
        this.user = user;
    }

    @Override
    public void setClientReference(RMIClientInt remoteRef) {
        this.clientReference = remoteRef;
    }

    /**
     * Quits the user
     */
    @Override
    public void quit() { user.quit(); }

    /**
     * Notifies the client waiting in a lobby that the lobby has updated
     * @param n lobby's current size
     */
    @Override
    public void notifyLobbyUpdate(int n) {
        try {
            clientReference.updateLobby(n);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    /**
     * Notifies the client that the game has started
     * @param n the number of players playing the game
     * @param id the client's identification number in the game
     */
    @Override
    public void notifyGameStart(int n, int id) {
       try {
            clientReference.updateGameStart(n,id);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyStatusUpdate(String event, int id) {

    }

    @Override
    public void notifyGameEnd(List<Player> players) {

    }

    @Override
    public void notifyRoundEvent(String event, int roundNumber) {

    }

    @Override
    public void notifyTurnEvent(String event, int playerId, int turnNumber) {

    }

    @Override
    public void notifySchema(SchemaCard schemaCard){

    }

    /**
     * Returns the light version of the given schema card
     * @param schemaCard the schema card to be used as a template for the new light schema
     * @return the newly created LightSchema
     */
    private LightSchemaCard toLightSchema(SchemaCard schemaCard){
        HashMap<Integer,CellContent> contentMap = new HashMap<>(30);
        for(int i=0;i<20;i++){
           Cell cell = schemaCard.getCell(i);
           if(cell.hasDie()){
               contentMap.put(i,new LightDie(cell.getDie().getShade(),cell.getDie().getColor()));
           }else if(cell.hasConstraint()){
               Constraint constraint = cell.getConstraint();
               if(constraint.isColorConstraint())
                   contentMap.put(i,new LightConstraint(constraint.getColor()));
               else
                   contentMap.put(i,new LightConstraint(constraint.getShade()));
           }
        }
        return new LightSchemaCard(schemaCard.getName(),contentMap,schemaCard.getFavorTokens());
    }

    @Override
    public void notifyToolCard(ToolCard toolCard) {

    }

    @Override
    public void notifyPublicObjective(PubObjectiveCard pubObjectiveCard) {

    }

    @Override
    public void notifyPrivateObjective(PrivObjectiveCard privObjectiveCard) {

    }

    @Override
    public void notifyPlayers(List<Player> players) {

    }

    @Override
    public void notifyDraftPool(List<Die> draftedDice) {
        
    }

    @Override
    public void notifyRoundTrack(ArrayList<ArrayList<Die>> trackList) {

    }


    @Override
    public void notifyFavorTokens(int favorTokens) {

    }

    @Override
    public void notifySchemaDiceList(SchemaCard schema) {

    }

    @Override
    public void notifyRoundTrackDiceList(ArrayList<ArrayList<Die>> trackList) {

    }

    @Override
    public void notifyDraftPoolDiceList(List<Die> draftedDice) {

    }

    /**
     * Pings the client invoking a remote method
     * @ truee iff the remote call doesn't throw an exception, therefore the connession between client and server is still up
     */
    @Override
    public boolean ping() {
        try{
            clientReference.pong();
        } catch (Exception e) {
            return false;
        }
            return true;
    }



}