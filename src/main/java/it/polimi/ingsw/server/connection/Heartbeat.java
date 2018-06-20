/*package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.User;

public class Heartbeat extends Thread{
    private MasterServer master = MasterServer.getMasterServer();
    @Override
    public void run(){

        while(true){
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            for(int i = 0; i<master.getUsersSize(); i++){
                    User user = master.getUserByIndex(i);
                    try{
                        if((user.getStatus().equals(UserStatus.LOBBY)
                                ||user.getStatus().equals(UserStatus.PLAYING))
                                && !user.getServerConn().ping()){
                            user.disconnect();
                        }
                    }catch(NullPointerException e) { // because the thread isn't synchronized to the user constructor
                    }
            }
        }
    }
}*/
