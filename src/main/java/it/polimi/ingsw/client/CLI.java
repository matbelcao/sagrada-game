package it.polimi.ingsw.client;

import java.io.*;

public class CLI {
    private BufferedReader inKeyboard;
    private PrintWriter outCli;
    private Client client;

    public CLI(Client client) {
        this.client = client;
        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }


    public void greeting() {
        outCli.println(client.getClientConn().getGreetings());

    }

    private void clearCLI() {
        for (int i = 0; i < 50; i++) {
            outCli.printf("\n");
        }
    }

    public void loginProcedure() {
        String username;
        String password;
        clearCLI();
        try {
            outCli.printf("\nUSERNAME: ");
            username = inKeyboard.readLine();
            outCli.printf("\nPASSWORD: ");
            password = inKeyboard.readLine();

            client.setPassword(password);
            client.setUsername(username);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.println("Successfully logged in as " + client.getUsername());
        } else {
            outCli.println("Couldn't login correctly, please retry ...");
        }
    }

    public void updateConnection() {
        outCli.println("Connection established!");
        outCli.flush();
    }
}


