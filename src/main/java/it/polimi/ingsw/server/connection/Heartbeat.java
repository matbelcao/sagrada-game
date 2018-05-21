package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.ConnectionMode;

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
                if(user != null){
                    System.out.println("user "+user.getUsername()+" status "+ user.getStatus());
                    //if user is active and uses RMI and doesn't respond to ping
                    if(!user.getStatus().equals(UserStatus.DISCONNECTED) && user.getConnectionMode().equals(ConnectionMode.RMI) && !user.getServerConn().ping())
                        user.disconnect();
                }
            }
        }
    }
}
