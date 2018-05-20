package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;

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
                //if user is active and uses RMI
                if(!user.getStatus().equals(UserStatus.DISCONNECTED) && user.getConnectionMode().equals(ConnectionMode.RMI)){
                    //if user doesn't respond to ping
                    if(!user.getServerConn().ping()){
                        user.disconnect();
                    }

                }
            }
        }
    }
}
