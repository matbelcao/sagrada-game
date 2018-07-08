#  Sagrada board-game

<img src="/src/main/resources/img/sagrada-box.jpg" height="400px" ></img>

### Group number: 7
* __10503336__ - *Belcao Matteo*
* __10493758__ - *Bertolini Giuseppe*
* __10546479__ - *Biondo Livio*



### Brief introduction

This project aims to replicate the experience of playing Sagrada, a board game that obviously gets its name from Gaudi's Sagrada Familia, in which the players become master glassmakers and compete to create the most beautiful window (or the one that just gets them more points!). 

### A little background 

The project is built around a simple client-server infrastructure, so that you can play with others without having to share a computer among four people. What you need to do is just setting up a server (one of your computers, you can still play the game on that too, of course), then connect all of you to the same server (more on that later on) by launching a client for each participants with the correct IP set (this should be the one of the machine used as a server). You will have to login with a username and a password (Notice: those will be deleted once you kill the server), then you'll be able to join a lobby of users that are waiting to begin to play. Once some time has passed or four people get in the lobby a new match can start. From now on you'll be playing Sagrada, with all it's rules written in pure code (and other magical stuff you don't really need to care about) so none of you can try to give new interpretations to the rules to cheat at it.

### technical stuff

Here's a copy of the help messages you wuold get in while launching both client and server with invalid options:
##### Client
```
Welcome to Sagrada!
    Usage:
            -h/--help                               Get this help message
            -g/--gui                                Set the preferred UI mode to GUI
            -c/--cli                                Set the preferred UI mode to CLI
            -a/--server-address ipv4_address        Set the ip address of the server
            -r/--rmi                                Set the preferred connection mode to RMI
            -s/--socket                             Set the preferred connection mode to Socket
            -i/--italian                            Set UI language to italian
            -e/--english                            Set UI language to english

    By choosing no options default options will be set:
            ip:                 127.0.0.1
            ui:                 GUI
            connection mode:    socket
            language:           ita 
            
```

#### Server

```
Welcome to Sagrada!
        Usage:
        -h/--help                               Get this help message
        -a/--server-address ipv4_address        Set the ip address of the server
        -A/--additional-schemas                 Choose to play with additional schemas
        -t/--turn-time time_in_seconds          Set the maximum time for a turn
        -l/--lobby-time time_in_seconds         Set the lobby timer length
        By choosing no options default options will be set:
        ipv4_address:               127.0.0.1
        additional-schemas:         false
        turn-time:                  90s
        lobby-time:                 30s
        
```

They're pretty much self-explanatory but let me give you more details on these.

+   You can play this game in either a CLI or GUI interface (you can set this in the client), the default is CLI because we hate you and just want you to have nightmares about this game.
+   More importantly, let's talk about __IP addresses__: the default is the simple _loopback_ (that will only work if you play on the same machine you start the server from). This setting is crucial, because if you mess with this you won't be able to play (not on different machines, at least). It's simple though: 
	+   supposing you are all on the same local area network (i.e. your wifi at home) you choose a machine that is going to be the server for the game (games, if you're more than four, you could even have homemade tournaments of Sagrada with this, and play multiple matches in parallel), you go to the network settings (you can google this part if you need to) and find out the local IP, write it down somewhere (`ctrl + c` for the hackers);
	+   you launch the server with the option `-a` ( `--server-address`) followed by that IP address (`ctrl + v`)
	+   then you use the same option and the same ip on all clients 
	+   just make sure to write the ip right after the option (separated by a whitespace)
	+   PS: if you want to play with people outside your home connection you can follow the same principles ( you can set the port for both RMI and Socket via configuration file), but good luck with NATs, port mapping and firewalls (you can always google google google)
+   Now, on the __client__ you can also set the language _italian_ or _english_ (guess where we were born!) and, if you really wish to, you can chose between _RMI_ and _Socket_ ((hopefully) nothing that makes a difference you'd actually notice while playing) 
+   On the other hand, on the server you can still choose the maximum time you'll have to wait to begin a match when at least two of you are inside the lobby (`--lobby-time` or `-l`), and the time you are going to give at most to a player to make his move (`--turn-time` or `-t`)
+   To spice things up we decided to add a few more windows schemas to the game, you can enable them (on the server) by adding the option `--additional-schemas` or `-A`, one or more of you will get them, we'll make sure...


We genuinely hope you'll have fun with this.

And now ... 
 

### other technical stuff for windows

If ((you're using Windows) AND (you wish to use the CLI) AND (you really wish to se colored dice and not some strange characters)) then we have this simple solution:

1. unzip the LM_7_Client.zip file
2. run the bat file UTF8-windows.bat
3. set your cmd font to __consolas__ or __curier new__
4. run the LM_7_Client.jar with the option `-Dfile.encoding=UTF8` (ex: `java -jar -Dfile.encoding=UTF8 LM_7_Client.jar` assuming you are in the parent folder)

### other technical stuff for some linux distros (like Ubuntu)

If you're trying to run the client with a GUI on linux with java installed and all and you can't start it, the problem may be that your distribution of jre doesn't contain the needed javafx implementation. Please make sure to install it before running the application
