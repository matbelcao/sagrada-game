# Socket Protocol
This document aims to describe the rules and the structure of the messages that the application uses to allow users to play this game through a connection via socket. The messages are divided in __Client-side__ and __Server-side__ ones and they are grouped according to the phase of a game they belong to. We tried to follow a chronological order, when possible, while sorting the following paragraphs.

## Index
+   [Welcome](#welcome)
+   [Login](#login)
+   [Lobby](#lobby)
+   [Round Evolution](#round-evolution)
+   [Board Objects Retrieval](#board-objects-retrieval)
+   [Game Management](#game-management)
+   [Player Status](#player-status)
+   [Connection Status Detection](#connection-status-detection)
+   [Dice and Constraints Syntax](#dice-and-constraints-syntax)

## Welcome
### Server-side
##### `<welcome_message>`
+   `<welcome_message>`: a message to greet the newly connected client

This is the first message the server sends to the client, as soon as the connection is established.

## Debug
### Server-side
##### `INVALID message`

This is for debug purposes only and it simply signals that the message the server received wasn't properly constructed.

## Login
### Client-side
##### `LOGIN <username> <password>`
+   `<username>`: the user's username
+   `<password>`: the user's password (possibly hashed)

The client sends this message then waits for a response from the server. This is tipically the first message exchanged between client and server.

### Server-side
##### `LOGIN ok|ko`
+   `ok`: the client was successfully logged in
+   `ko`: username and password did not match

The server responds with this message to the login request from the client, if ko the client will be able to try with another username + password combination, if ok the user enters the `LOBBY` phase.


## Lobby
### Server-side
##### `LOBBY <number_of_lobby_players>`
+   `<number_of_lobby_players>`: number of the players waiting in the lobby to begin a new match

The server sends this message to all players in the lobby after a successful login of a player that isn't reconnecting to a match he was previously playing. The message is sent again to all said players whenever there is a change in the number of the users in the lobby.

### Example Session

... CLIENT CONNECTS TO THE SERVER ...

+   __`SERVER`__ to __`PLAYER0`__  

    `Connection established!`

+   __`PLAYER0`__ to __`SERVER`__  

	`LOGIN PLAYER0 p455w0rd`

+   __`SERVER`__ to __`PLAYER0`__  

	`LOGIN ok`  
	`LOBBY 1`

+   __`PLAYER1`__ to __`SERVER`__  

	`LOGIN PLAYER0 p455w0rd`

+   __`SERVER`__ to __`PLAYER0`__  

	`LOGIN ko`

+   __`PLAYER1`__ to __`SERVER`__  

	`LOGIN PLAYER1 bubu7tete`

+   __`SERVER`__ to __`PLAYER1`__  

	`LOGIN ok`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__  

	`LOBBY 2`

+   `...`

## Round Evolution
### Server-side
###### Notice: all of the server-side messages in this section are sent in broadcast to all players. The players reply with a simple `ACK game` to each message they receive.


##### `GAME start <number_of_connected_players> <player_id>`

+   `<number_of_connected_players>`: this represents the number of players that are participating to the new match
+   `<player_id>`: the assigned number of the user receiving this message


This message is sent when the new match is ready to begin, after the `LOBBY` phase, it triggers the sequence of initial `GET` (`players`, `schema draft`, `pub`, `priv`, `tool` ), from the user towards the server.

##### `GAME end {<player_id>,<final_score>,<position>} ...`

+   `<player_id>`: the player's identifier (0 to 3)
+   `<final_score>`: the player's score
+   `<position>`: the player's position

With this message the server communicates the end of a match and sends to each client a list of  fields that represent the ranking of the match's players.

##### `GAME round_start|round_end <round_number>`

+   `round_start`: a new round is about to begin
+   `round_end`: the current round has ended
+   `<round_number>`: the number of the round (0 to 9)

This message is sent whenever a round is about to begin or has just ended. `GAME round_start 0` signals the start of the match and that all the players have chosen their respective schema cards.

##### `GAME turn_start|turn_end <player_id> <turn_number>`

+   `turn_start`: a new turn is about to begin
+   `turn_end`: the current turn has ended
+   `<player_id>`: the player's identifier (0 to 3)
+   `<turn_number>`: the number of the turn within the single round (0 to 1)

This message is sent at every beginning/end of a turn and can trigger (end) clients to `GET` the updates derived from the turn, tipically the schema of the <player_id> and the draftpool.

### Client-side

##### `GAME end_turn`

This message is sent to the server in case the client wants to end his turn before the timer goes off.

##### `GAME new_match`

This message is sent to the server in case the client wants to start a new match when the previously is just ended.

### Example Session

+  `...`

+   __`SERVER`__ to __`PLAYER0`__

	`GAME start 2 0`

+   __`SERVER`__ to __`PLAYER1`__

	`GAME start 2 1`

+   `...`



## Board Objects Retrieval

### Client-side
##### `GET schema (draft|<player_id>)`

+   `draft`: optional parameter to request four random schema cards at the match beginning (only one time)
+   `<player_id>`: the player whose schema card the user wants to obtain

The client sends this message to request the complete schema card (. The draft option makes the server send the four schema cards the user has to choose from.

##### `GET favor_tokens <player_id>`
+   `<player_id>`: the player whose favor tokens the user wants to obtain

This message is used to get the number of tokens remaining to the specified player.

##### `GET priv|pub|tool|draftpool|roundtrack|players|game_status`

+   `priv`: the user requests the private objective card
+   `pub`: the user requests the three public objective cards
+   `tool`: the user requests the three toolcards
+   `draftpool`: the user requests the dice in the draftpool
+   `roundtrack`: the user requests the dice in the roundtrack
+   `players`: the user requests the list of players of the match
+   `game_status`: the user requests the status of the the match when reconnected


The client sends this message to request the card parameters, some dice in the board or info about other players.

### Server-side
###### Notice: the following messages of this section all require an `ACK send` each. if a client doesn't reply with an ack within a reasonable time is to be considered offline.
##### `SEND schema <name> <favor_tokens> [{D,<position>,<dieColor>,<shade>|  {C,<index>,<dieColor>|<shade>}] ...`

+   `schema`: signals that the schema is being sent in its entirety
+   `<favor_tokens>`: the number of favor tokens associated with the schema
+   `<name>`: the name of the schema
+   `D`: the cell is occupied by a die
+   `C`: there is a constraint in the cell
+   `<index>`: the cell's index in the schema (0 to 19)
+   `<dieColor>`: the dieColor property
+   `<shade>`: the shade property

The server responds with this message to give information about the dice/constraints that are in the requested schema.

##### `SEND priv|pub|tool <id> <name> <description> [<dieColor>|<used>]`

+   `priv`: the requested element is a private objective card
+   `pub`: the requested element is a public objective card
+   `pub`: the requested element is a toolcard
+   `<id>`: the card's id
+   `<name>`: the card's name
+   `<description>`: the card's description
+   `<used>`: boolean value that tells if the tool has already been used
+   `<dieColor>`: the private objective dieColor

The server responds with this message to give information about the requested card(s).

##### `SEND draftpool|roundtrack [(<index>|<round>),<dieColor>,<shade>] ... `

+   `draftpool`: the requested elements concern the draftpool
+   `roundtrack`: the requested elements concern the roundtrack
+   `pub`: the requested element is a public objective card
+   `<index>`: the die position in the draftpool
+   `<round>`: the number of the round the die was left over
+   `<dieColor>`: the die dieColor property
+   `<shade>`: the die shade property

The server responds with this message to give information about the dice placed in the board's area that is shared by all players.

##### `SEND favor_tokens <player_id> <num_tokens>`
+   `<player_id>`: the id of the player the tokens belong to
+   `<num_tokens>`: the number of remaining tokens


##### `SEND players {<player_id>,<username>,<status>} ...`

+   `<player_id>`: the player's identifier (0 to 3)
+   `<username>`: the username that the player used to login
+   `<status>`: the player's connection status (PLAYING or DISCONNECTED or QUITTED)

This message is used to send a list of the players of the match, their usernames and current status.

##### `SEND game_status <isInit> <numPlayers> <numRound> <isFirstTurn> <nowPlaying>`

+   `<isInit>`: if the game has already started or someone has yet to choose a schema card
+   `<numPlayers>`: the number of players in the match
+   `<numRound>`: the number of the match round
+   `<isFirstTurn>`: if it is the player's first or second turn in the current round
+   `<numPlaying>`: the ID of who is playing

This message is used to send the necessary information to the user to guarantee correct reconnection during the game.

### Example Session

+   `...`

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`GET players`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__  

	`SEND players 0,PLAYER0 1,PLAYER1`  

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`GET schema draft`

+   __`SERVER`__ to __`PLAYER0`__

	`SEND schema Kaleidoscopic_Dreams 4 C,0,0,THREE C,1,2,GREEN ...`

+   `...`

## Game Management
### Client-side
##### `GET_DICE_LIST `

This message is used to gather a list of dice from the server. Said list will be determined by the game controller on the server based on the status of the turn (e.g. if a tool is being used...)




### Server-side

##### `LIST_DICE <from> [<position>,<shade>,<dieColor>] ...`

+   `<from>`: this field tells where the dice that are being listed come from (draft-pool, schema, roundtrack ...)
+   `<position>`: an integer value that carries the information about the "phisical" position of the die in the element of the board . Based on the field `<from>`it can be:
	+   __0-19__ for the schema
	+   __0-8__ for the draftpool
	+   __0-9__ (with multiple occurrencies if needed) for the roundtrack
	+   __0__ for others (toolcards #1,11)
+   `<shade>,<dieColor>`: represent the characteristics of the die


This message is used in order to send and ordered list of valid options the client can later select a die from with the `SELECT` command.


### Client-side

##### `SELECT <die_index>`

+   `<die_index>`: the index (starting from 0) of the die in a given list (from  a `LIST_DICE`) (could be a list of dice from the draft-pool, schema, roundtrack ...)

This message is a request to the server to specify the possible actions the user can perform on the die. The server will reply with a `LIST_OPTIONS`.


### Server-side
##### `LIST_OPTIONS (<command>) ...`

+   `<command>`: this is a command that the player can perform on the die:
	+   __PLACE_DIE__: allows the user to place the die in the schema. The client will have to send a `GET_PLACEMENTS_LIST` request to the server to retrieve a list of the possible placements (given the state of the turn);
	+   __INCREASE_DECREASE__: this is specific to the toolcard #1 and will require the client to select a die from a list later on;
	+   __SWAP__: this is specific to tool #5 and informs the client the die he selected will be swapped with another die he will be able to chose later;
	+   __REROLL__: this allows the user to reroll the selected die;
	+   __FLIP__: this allows the user to flip the die;
	+   __SET_SHADE__: this is specific to tool #11 and signals that the user will need to choose a new shade for a die;
	+   __SET_COLOR__: the player will need to choose a dieColor;
	+   __NONE__: no further choices will be required from the player in the logical flow of the current tool.

__NOTICE:__ whenever a single option is sent to the client the choice will be made automatically and will be transparent to the user.

##### `LIST_PLACEMENTS [<position>] ...`

+   `<position>`: this is the actual position inside the schema where the die can be placed (0-19)

This message is used only to list possible placements in a schema after the client has chosen __PLACE_DIE__ and has sent a `GET_PLACEMENTS_LIST` towards the server.


### Client-side

##### `DISCARD`

This message is sent to the server when the client that received a list of possible placement for a die chooses not to place that die and wants to see the placements of another die. This can only be used after a `GET_PLACEMENTS_LIST` and the corresponding `LIST_PLACEMENTS` is received.
the user will then need to restart the "loop" (explained below) by doing a `GET_DICE_LIST`. The `DISCARD` message is sent automatically in case of an empty list of possible placements.

##### `CHOOSE  <index>`

+   `<index>`: the index of the element in a list (starting from 0). this could be a list of (`LIST_OPTIONS`) options or placements (`LIST_PLACEMENTS`).

__NOTICE__: for the die placement selection this is the index that points to one of the valid positions in a list presented to the user and not the value of the position itself

This message is sent to the server in order to make a possibly definitive choice. the server is still going to do his checks and will reply with the next message, eventually followed by some `SEND` containing updates.


### Server-side

##### `CHOICE ok|ko`
+   `ok`: this signals a valid choice
+   `ko`: this signals an invalid choice sent to the server with `CHOOSE`

This is the reply of the server to a `CHOOSE` message previously received from the user.

### Client-side

##### `BACK`

This message signals that the player wants to go back to where he can choose to either use a toolcard or place a die.
__NOTICE__: any progress in the usage of a tool card that is not a legal state to be "committed" to the board will be lost, and the favor tokens will be gone.


## Toolcards-Specific Messages
### Client-side
##### `TOOL enable <index>`
+   `<index>`: the number of the selected tool (0-2) among the three exctracted for the match.

This is the message that activates, or tries to, a tool card.

### Server-side
##### `TOOL ok|ko`

This message tells the user whether the activation of a toolcard had a positive result or not.

### Client-side
##### `TOOL can_continue`

This message is sent during the execution of the procedures of a toolcard, after a the placement of a die in the schema if __PLACE_DIE__  was chosen or after having just chosen any other option. If the server replies with a `TOOL ok` the client will ask for a new dice list (`GET_DICE_LIST`) and then proceed with the loop that will be described in the following paragraph.



### Example Session

+   `...`
+   __`SERVER`__ to __`PLAYER1`__

	`SEND schema BellesGuard ...`
	`SEND schema Kaleidoscopic_Dream ...`
	`...`

+   __`PLAYER0`__ to __`SERVER`__

	`CHOOSE 1`

+   __`SERVER`__ to __`PLAYER0`__

	`CHOICE ok`

+   `...`

+   __`PLAYER1`__ to __`SERVER`__

	`CHOOSE 3`

+   __`SERVER`__ to __`PLAYER1`__

	`CHOICE ok`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__  

	`GAME round_start 0`

+   __`PLAYER0`__ to __`SERVER`__

	`GET schema 1`

+   `...`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__  

	`GAME turn_start 0 0`

+   __`PLAYER0`__ to __`SERVER`__

	`GET draftpool`

+   `...`

+   __`PLAYER0`__ to __`SERVER`__

	`GET_DICE_LIST `

+   __`SERVER`__ to __`PLAYER0`__

	`LIST draftpool 0,RED,FOUR 1,BLUE,THREE ...`

+   __`PLAYER0`__ to __`SERVER`__

	`SELECT 1 `

+   __`SERVER`__ to __`PLAYER0`__

	`LIST_OPTIONS 0,PLACE_DIE`

+   __`PLAYER0`__ to __`SERVER`__

	`CHOOSE 0`

+   __`SERVER`__ to __`PLAYER0`__

	`CHOICE ok`

+   __`PLAYER0`__ to __`SERVER`__

	`GET_PLACEMENTS_LIST`

+   __`SERVER`__ to __`PLAYER0`__

	`LIST_PLACEMENTS 0 2 4 5 9 17 18 19`

+   __`PLAYER0`__ to __`SERVER`__

	`CHOOSE 1`

+   __`SERVER`__ to __`PLAYER0`__

	`CHOICE ok`

(this confirms that the placement of the die (`BLUE,THREE`) in the third cell of the schema of player0 has been made correctly)


+   `...`

## The Loop

The execution of a turn and it's parts, as well as the usage of a tool, is based on a simple yet effective loop:

1. the player begins his turn: he can either choose to use a tool or place a die (`GAME end_turn` is also a valid option, at any time).

#### __Placement of a die:__
1. if he simply wants to place a die from the draftpool he can send a `GET_DICE_LIST`
2. the server will reply with a `LIST_DICE draftpool ...`
3. user does a `SELECT <die_index>`
4. server replies with a `LIST_OPTIONS PLACE_DIE`
5. user:`CHOOSE 0`
6. server: `CHOICE ok`
7. user: `GET_PLACEMENTS_LIST`
8. server: `LIST_PLACEMENTS [<position>]...`
9. the client can now `DISCARD` and go back to #1 or `CHOOSE <index>`
10. server: `CHOICE ok`
11. client gets the updates made to the board then returns to the initial choice


#### __Usage of a tool:__
1. the user asks for a `TOOL enable <index>`
2. server checks if the user can activate said tool and replies `TOOL ok|ko`, if ko go back to initial choice
3. user sends a `GET_DICE_LIST` doesn't matter which tool is active
4. the server replies with a `LIST_DICE draftpool|schema|roundtrack ...` according to the tool card and which choice it requires first
3. user does a `SELECT <die_index>`
4. server replies with a `LIST_OPTIONS PLACE_DIE|INCREASE_DECREASE|SWAP ...` most of the times it's just one so the client will not have to make any choice so
5. user:`CHOOSE 0`
6. server: `CHOICE ok`if it is a `PLACE_DIE` repeat the steps (7-11) as in the __Placement of a die__ (the server knows the constraint and will apply them according to the selected tool) once finished come back to step #7
7. user: `TOOL can_continue`
8. server: `TOOL ok|ko` if ko then the changes to the board will be applied and the user will return to the decision of placing a die or using a tool if ok will go back to step #2

At the end of each iteration of the loop the temporary board is updated for the user to be able to continue in the game/execution of a tool effect. The changes are made to the actual board only if the state is valid according to the rules of the game.


## Player Status

### Client-side
##### `QUIT`
The client that wants to definitively leave the match can send this message to the server, which will then notify all the players, including the one that quitted with a `STATUS quit <player_id>`.
This message can also be sent during the lobby phase, in this case the server only updates the number of players waiting for the match and sends the corresponding `LOBBY` message.

### Server-side

##### `STATUS reconnect|disconnect|quit [<player_id>]`
+   `reconnect`: indicates that the player has successfully reconnected to the match he was playing
+   `disconnect`: indicates that a player has been found to be disconnected
+   `quit`: the player has definitively quitted the game and won't be able to reconnect in future rounds
+   `<player_id>`: the assigned number of the user involved (not used after `check`)

Theese messages are sent to all connected users and also may serve the purpose of notifying the reconnecting user that the procedure went fine (`STATUS reconnect ...`).

### Example Session

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__

	`GAME round_start 6`
+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__

	`PING`

+   __`PLAYER1`__ to __`SERVER`__

	`PONG`


+   __`PLAYER0`__ to __`SERVER`__

	`...`

+   __`SERVER`__ to __`PLAYER1`__

	`STATUS disconnect 0`

+   __`PLAYER0`__ to __`SERVER`__

	`LOGIN PLAYER0 p455w0rd`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__

	`STATUS reconnect 0`

+   __`PLAYER0`__ to __`SERVER`__

	`GET ...`

	`...`


## Connection Status Detection
This messages are user by the clinet and server to detect if the connection is still available.
### Server-side

##### `PING`


### Client-side

##### `PONG`

### Example session
+   `...`

+   __`SERVER`__ to __`PLAYER1`__

	`PING`

+   __`PLAYER1`__ to __`SERVER`__

	`PONG`

+   __`SERVER`__ to __`PLAYER1`__

	`PING`

+   `...`

## Dice and Constraints Syntax
###### In this section is reported the dice coding used in the client-server communication.
|   dieColor   |    shade    |
|:-----------:|:-----------:|
|   RED     |     ONE     |
|   GREEN   |     TWO     |    
|   BLUE    |     THREE   |
|   YELLOW  |     FOUR    |
|   PURPLE  |     FIVE    |
|           |     SIX     |
