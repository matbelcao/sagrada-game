package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.enums.UserStatus;

public class Heartbeat extends Thread{
    private MasterServer master = MasterServer.getMasterServer();
    @Override
    public void run(){

        while(true){
            try
            {
                Thread.sleep(5000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            for(int i = 0; i<master.getUsersSize(); i++){
                    User user = master.getUserByIndex(i);
                    try{
                        if(!user.getStatus().equals(UserStatus.DISCONNECTED) && !user.getStatus().equals(UserStatus.CONNECTED) && !user.getServerConn().ping()){
                            user.disconnect();
                        }
                    }catch(NullPointerException e) { // because the thread isn't synchronized to the user constructor
                    }
            }
        }
    }
}
