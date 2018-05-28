package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.immutables.CellContent;
import it.polimi.ingsw.common.immutables.LightConstraint;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Constraint;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.SchemaCard;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public class RMIServer implements ServerConn {
        private RMIServerInt remoteObj;
        private  User user;


    public RMIServer(RMIServerInt remoteObj,User user){
        this.remoteObj = remoteObj;
        this.user = user;
    }

    /**
     * Notifies the client waiting in a lobby that the lobby has updated
     * @param n lobby's current size
     */
    @Override
    public void notifyLobbyUpdate(int n) {
        try {
            remoteObj.notifyLobbyUpdate(n);
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
            remoteObj.notifyGameStart( n, id);
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


    /**
     * Pings the client invoking a remote method
     * @ truee iff the remote call doesn't throw an exception, therefore the connession between client and server is still up
     */
    @Override
    public boolean ping(){
        boolean result;
        try {
            result = remoteObj.ping();
        } catch (RemoteException e) {
            return false;
        }
        return result;
    }



}