package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightTool;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.model.iterators.RoundIterator;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This class represents the controller of the game. It manages the rounds and the operations that the players make on the board
 */
public class Game extends Thread implements Iterable  {
    public static final int NUM_ROUND=10;
    private DraftPool draftPool;
    private Board board;
    private boolean additionalSchemas; //to be used for additional schemas FA
    private ArrayList<User> users;
    private SchemaCard [] draftedSchemas;
    private RoundIterator round;
    private Boolean endLock;
    private final Object lockRun;
    private User userPlaying;
    private Timer timer;

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     * @param additionalSchemas true if additional are wanted by the user
     */
    public Game(List<User> users,boolean additionalSchemas){
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
            u.setGame(this);
            u.getServerConn().notifyGameStart(users.size(), users.indexOf(u));
        }
        this.draftPool=new DraftPool();
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        this.draftedSchemas = board.draftSchemas();
    }

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     */
    public Game(List<User> users){
        this.additionalSchemas=false;
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
        }
        this.draftPool=new DraftPool();
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        draftedSchemas = board.draftSchemas();
    }

    /**
     * Assigns to the users who have not chosen any schema card, the first one that was proposed them before
     */
    private class DefaultSchemaAssignment extends TimerTask {
        @Override
        public void run(){
            Player player;
            for (User u:users){
                player=board.getPlayer(u);
                if(player.getSchema()==null){
                    player.setSchema(draftedSchemas[users.indexOf(u)*Board.NUM_PLAYER_SCHEMAS]);
                }
            }
            synchronized (lockRun) {
                endLock = true;
                lockRun.notifyAll();
            }
        }
    }

    /**
     * Allows the enabled user to perform the desired actions during his turn ( until it isn't executed )
     */
    private class PlayerTurn extends TimerTask {
        @Override
        public void run(){
            synchronized (lockRun) {
                endLock = true;
                lockRun.notifyAll();
            }
        }
    }

    /**
     * Stops the execution flow of Run () until the desired action occurs
     */
    private void waitAction(){
        while (!endLock) {
            synchronized (lockRun) {
                try {
                    lockRun.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method provides the execution order of the game flow
     */
    @Override
    public void run(){
        endLock=false;
        timer = new Timer();
        round = (RoundIterator) this.iterator();

        timer.schedule(new DefaultSchemaAssignment(), MasterServer.getMasterServer().getTurnTime() * 1000);
        waitAction();

        while (round.hasNextRound()){
            //estrazione casuale dadi draftpool
            while(round.hasNext()){
                userPlaying = round.next();

                endLock=false;
                timer.schedule(new PlayerTurn(), MasterServer.getMasterServer().getTurnTime() * 1000);

                waitAction();
            }
            round.nextRound();
        }
    }

    /**
     * Responds to the request by sending one private objective card to the user of the match
     * @param user the user who made the request
     */
    public void sendPrivCard(User user){
        PrivObjectiveCard privCard = board.getPlayer(user).getPrivObjective();
        LightCard lightCard =new LightCard(privCard.getName(),privCard.getDescription(),privCard.getImgSrc(),privCard.getId());
        user.getServerConn().notifyPrivateObjective(lightCard);
    }

    /**
     * Responds to the request by sending three public objective cards to the user of the match
     * @param user the user who made the request
     */
    public void sendPubCards(User user){
        for (int i=0 ; i < Board.NUM_OBJECTIVES ; i++ ) {
            PubObjectiveCard pubCard = board.getPublicObjective(i);
            LightCard lightCard =new LightCard(pubCard.getName(),pubCard.getDescription(),pubCard.getImgSrc(),pubCard.getId());
            user.getServerConn().notifyPublicObjective(lightCard);
        }
    }

    /**
     * Responds to the request by sending three tool cards to the user of the match
     * @param user the user who made the request
     */
    public void sendToolCards(User user){
        LightTool lightTool;

        for (int i = 0; i < Board.NUM_TOOLS; i++) {
            ToolCard toolCard=board.getToolCard(i);
            lightTool=new LightTool(toolCard.getName(),toolCard.getDescription(),toolCard.getImgSrc(),toolCard.getId(),toolCard.hasAlreadyUsed());
            user.getServerConn().notifyToolCard(lightTool);
        }
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     */
    public void sendSchemaCards(User user){
        for (int i=0 ; i < Board.NUM_PLAYER_SCHEMAS ; i++ ){
                user.getServerConn().notifySchema(draftedSchemas[(users.indexOf(user)* Board.NUM_PLAYER_SCHEMAS)+i]);
        }
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     * @param playerId the id of the player's desired schema card
     */
    public void sendUserSchemaCard(User user,int playerId){
        if(playerId>=0 && playerId<users.size()){
            user.getServerConn().notifySchema(board.getPlayer(users.get(playerId)).getSchema());
        }
    }

    /**
     * Responds to the request by sending the draftpool to the user of the match
     * @param user the user who made the request
     */
    public void sendDraftPool(User user){
        //user.getServerConn().notifyDraftPool(draftPool);
    }

    public void sendPlayers(User user){
        ArrayList<LightPlayer> players= new ArrayList<>();
        for (User u:users){
            players.add(new LightPlayer(u.getUsername(),board.getPlayer(u).getGameId()));
        }
        user.getServerConn().notifyPlayers(players);
    }

    public void sendFavorTokens(User user){
        Player player=board.getPlayer(user);
        if(player.getSchema()!=null){
            user.getServerConn().notifyFavorTokens(player.getFavorTokens());
        }
    }

    /**
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card
     * the timer will be stopped
     * @param user the user to set the card
     * @param idSchema the id of the schema card
     */
    public void chooseSchemaCard(User user,int idSchema){
        for (SchemaCard s: draftedSchemas){
            if (s.getId()==idSchema){
                board.getPlayer(user).setSchema(s);
                break;
            }
        }
        for (User u: users){
            if(board.getPlayer(u).getSchema()==null){
                return;
            }
        }
        timer.cancel();
    }


    /**
     * Notify to the active users that an user has been reconnected to the game
     * @param user the user to notify
     */
    public void reconnectUser(User user){
        for(User u : users){
            if(u.getStatus()==UserStatus.PLAYING){
                u.getServerConn().notifyStatusUpdate("reconnect",users.indexOf(user));
            }
        }
        user.setStatus(UserStatus.PLAYING);
    }

    /**
     * Notify to the active users that an user has lost the connection to the game
     * @param user the user to notify
     */
    public void disconnectUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        for(User u : users){
            if(u.getStatus()==UserStatus.PLAYING){
                u.getServerConn().notifyStatusUpdate("disconnect",users.indexOf(user));
            }
        }
    }

    /**
     * Notify to the active users that an user has left the game
     * @param user the user to notify
     */
    public void quitUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        // add control for number of players still in the game...
        for(User u : users){
            if(u.getStatus()==UserStatus.PLAYING){
                u.getServerConn().notifyStatusUpdate("quit",users.indexOf(user));
            }
        }
        users.remove(user);
    }

    /**
     * Instantiates a new board for the match
     */
    public void createBoard(){
        this.board= new Board(this.users, additionalSchemas);
    }

    /**
     * Returns the list of the users that are currently playing the game
     * @return the list of users
     */
    public List<User> getUsers(){
        return users;
    }

    /**
     * This method creates an iterator that implements the round's turns management system
     * @return an iterator on the players of this game
     */
    @NotNull
    @Override
    public Iterator iterator() {
        return new RoundIterator(users);
    }

}