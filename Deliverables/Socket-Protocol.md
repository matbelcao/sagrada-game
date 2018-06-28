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
+   [Acknowledgement Messages](#acknowledgement-messages)
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
+   `<password>`: the user's password

The client sends this message then waits for a response from the server. This is tipically the first message exchanged between client and server.
### Server-side
##### `LOGIN ok|ko`
+   `ok`: the client logged in successfully
+   `ko`: username and password did not match
+   `invalid_username`: the username contains prohibited characters

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

this message is sent to the server in case the client wants to end his turn before the timer goes off.



### Example Session

+  `...`

+   __`SERVER`__ to __`PLAYER0`__

	`GAME start 2 0`

+   __`SERVER`__ to __`PLAYER1`__

	`GAME start 2 1`

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`ACK game`

+   `...`



## Board Objects Retrieval

### Client-side
##### `GET schema draft|<player_id>`

+   `draft`: optional parameter to request four random schema cards at the match beginning (only one time)
+   `<player_id>`: the player whose schema card the user wants to obtain

The client sends this message to request the updated schema card or the complete schema card (in case of reconnection or if it's the beginning of the first round). The draft option makes the server send the four schema cards the user has to choose from.

##### `GET favor_tokens <player_id>`
+   `<player_id>`: the player whose favor tokens the user wants to obtain

This message is used to get the number of tokens remaining to the specified player.

##### `GET priv|pub|tool|draftpool|roundtrack|players`

+   `priv`: the user requests the private objective card
+   `pub`: the user requests the three public objective cards
+   `tool`: the user requests the three toolcards
+   `draftpool`: the user requests the dice in the draftpool
+   `roundtrack`: the user requests the dice in the roundtrack
+   `players`: the user requests the list of players of the match


The client sends this message to request the card parameters, some dice in the board or info about other players.

### Server-side
###### Notice: the following messages of this section all require an `ACK send` each. if a client doesn't reply with an ack within a reasonable time is to be considered offline.
##### `SEND schema <name> [<favor_tokens>] [{D,<index>,<dieColor>,<shade>|  {C,<index>,<dieColor>|<shade>}] ...`

+   `schema`: signals that the schema is being sent in its entirety
+   `<favor_tokens>`: the number of favor tokens associated with the schema (this is used only when sending the drafted schemas at the beginning of a game)
+   `<name>`: the name of the schema
+   `D`: the cell is occupied by a die
+   `C`: there is a constraint in the cell
+   `<index>`: the cell's index in the schema (0 to 19)
+   `<dieColor>`: the dieColor property
+   `<shade>`: the shade property

The server responds with this message to give the information about the dice/constraints that are in the requested schema. The server can choose to just send an update (`schema_update`), instead of the whole schema, to the client when changes are made to the content of a schema after each turn.

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


##### `SEND players {<player_id>,<username>} ...`

+   `<player_id>`: the player's identifier (0 to 3)
+   `<username>`: the username that the player used to login

This message is used to send a list of the players of the match and their usernames.

### Example Session

+   `...`

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`GET players`

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__  

	`SEND players 0,PLAYER0 1,PLAYER1`  

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`ACK send`

+   __`PLAYER0`__ and __`PLAYER1`__ to __`SERVER`__

	`GET schema draft`
+   __`SERVER`__ to __`PLAYER0`__

	`SEND schema C,0,0,THREE C,1,2,GREEN ...`

+   __`PLAYER0`__ to __`SERVER`__

	`ACK send`

+   `...`

## Game Management
### Client-side
##### `GET_DICE_LIST schema|roundtrack|draftpool`
+   `schema`: if the client wants a list of all the dice contained in his schema
+   `roundtrack`: if the client wants a list containing the dice in the roundtrack
+   `draftpool`: if the client wants a list of the dice that are in the draftpool



##### `SELECT die <index>`

+   `<index>`: the index (starting from 0) of the die in a given list (could be a list of dice from the draft-pool,schema, roundtrack ...)

This message is a request to the server to specify the possible placements in the user's schema of a die that is temporarily selected by the user.

##### `SELECT modified_die`

This asks the server a list of possible placements for the die that has been rerolled, swapped, flipped... following a `CHOOSE`. It will trigger a `LIST placements` from the server towards the client. If the client receives an empty list or simply chooses to `DISCARD`, the toolcard will be marked as used (if it wasn't already) and the used favor tokens will be gone.


### Server-side
###### Notice: the following messages of this section starting with `LIST` require an `ACK list` each. if a client doesn't reply with an ack within a reasonable time is to be considered offline.
##### `LIST schema|roundtrack|draftpool [<index>,<position>,<dieColor>,<shade>] ...`

+   `schema`: provides an ordered list of the positions of the player's schema that have a die in place. The client can then `SELECT` a die from this list using the command above to obtain a list of possible placements (for example while using tool cards)
+   `roundtrack`: provides an ordered list of the dice that are in the roundtrack
+   `draftpool`: provides an ordered list of the dice of the draftpool
+   `<index>`: this is the index (starting from 0) in the list of placements, this will be used to `CHOOSE` or `SELECT` the placement later on
+   `<position>`: an integer value that carries the information about the position of the die in the element of the board
+   `<dieColor>,<shade>`: represent the characteristics of the die


This message is used in order to create a list of valid options the client can later select a die from with the `SELECT` command. The die will be definitively chosen and actually placed only after a valid sequence of `GET_DICE_LIST ...`, `SELECT die <index>`, `CHOOSE die_placement <index>` [see the example below].

##### `LIST placements [<index>,<position>] ...`
+   `placements`: signals that a list of possible placements for a  selected die is being sent
+   `<index>`: this is the index (starting from 0) in the list of placements, this will be used to `CHOOSE die_placement <index>` later on
+   `<position>`: this is the actual position inside the schema where the die can be placed (from 0 to 19)


__NOTICE__: `<index>` and `<position>` might be the same in some cases but are not 	meant 	to always be, in general `<position>`≥ `<index>`.


### Client-side

##### `DISCARD`
This message is sent to the server when the client that received a list of possible placement for a die chooses not to place that die.

##### `CHOOSE die_placement|schema|tool <index>`

+   `<index>`: the index of the element in a list (starting from 0). __NOTICE__: for the die placement selection this is the index that points to an one of the valid positions in a list presented to the user and not the value of the position itself
+   `die_placement`: the die is chosen from a list that the server sent to the client with a previous `LIST placements`
+   `schema`: the schema is chosen amongst the four cards received from the server with four `SEND`
+   `tool`: the tool is chosen between the three tools the user received at the beginning of the match, again, with three `SEND`

This message is sent to the server in order to make a possibly definitive choice. the server is still going to do his checks and will reply with the next message, eventually followed by some `SEND` containing updates.


### Server-side
##### `DISCARD ack`

This message is a simple acknowledgement to a previous `DISCARD` sent by the player. The server notifies that it received that and that it is waiting now for a new move.

##### `CHOICE ok|ko`
+   `ok`: this signals a valid choice
+   `ko`: this signals an invalid choice sent to the server with `CHOOSE`

This is the reply of the server to a `CHOOSE` message previously received from the user.

### Toolcards Specific

###Client-side
##### `CHOOSE die <index> [increase|decrease|reroll|flip|put_in_bag] `
This message can only be sent within the usage of tool cards that require to choose a die from the draftpool, the schema or the roundtrack. A `GET_DICE_LIST ...` is required before using it. This has to be used by toolcards with id in {1,5,6,10,11}
In toolcard #5 the chosen die is the one in the roundtrack, while the one in the draftpool is selected.

##### `CHOOSE shade <shade>`
This is used in toolcard #11 to set the new shade of the die that he has drafted from the dice bag. The die can then be selected with a `SELECT modified_die` after receiving a `CHOICE ok`.

### Server-side
##### `CHOICE ko|ok`
+   `ok`: this signals a valid choice
+   `ko`: this signals an invalid choice sent to the server with `CHOOSE`

This message if ok triggers the user to get an update of the draftpool


### Example Session

+   `...`

+   __`PLAYER0`__ to __`SERVER`__

	`CHOOSE schema 1`

+   __`SERVER`__ to __`PLAYER0`__

	`CHOICE ok`

+   __`PLAYER1`__ to __`SERVER`__

	`CHOOSE schema 3`

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

	`GET_DICE_LIST draftpool`

+   __`SERVER`__ to __`PLAYER0`__

	`LIST draftpool 0,RED,FOUR 1,BLUE,THREE ...`

+   __`PLAYER0`__ to __`SERVER`__

	`SELECT die 1 `
+   __`SERVER`__ to __`PLAYER0`__

	`LIST placements 0,0 1,2 2,9 ...`

+   __`PLAYER0`__ to __`SERVER`__

	`CHOOSE die_placement 1`
+   __`SERVER`__ to __`PLAYER0`__

	`CHOICE ok`
+   `...`


## Player Status
### Client-side
##### `QUIT`
The client that wants to definitively leave the match can send this message to the server, which will then notify all the players, including the one that quitted with a `STATUS quit <player_id>`.
This message can also be sent during the lobby phase, in this case the server only updates the number of players waiting for the match and sends the corresponding `LOBBY` message.
### Server-side
###### Notice: all of the server-side messages in this section are sent in broadcast to all players. The players reply with the relative `ACK status` to each message they receive.
##### `STATUS check|reconnect|disconnect|quit [<player_id>]`
+   `check`: indicates tha the server is doing a connection check on all clients
+   `reconnect`: indicates that the player has successfully reconnected to the match he was playing
+   `disconnect`: indicates that a player has been found to be disconnected
+   `quit`: the player has definitively quitted the game and won't be able to reconnect in future rounds
+   `<player_id>`: the assigned number of the user involved (not used after `check`)

This message is sent to all connected users and also serves the purpose of notifying the reconnecting user that the procedure went fine (`STATUS reconnect ...`).

### Example Session

+   __`SERVER`__ to __`PLAYER0`__ and __`PLAYER1`__

	`GAME round_start 6`

+   __`PLAYER1`__ to __`SERVER`__

	`ACK game`

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


## Acknowledgement Messages
### Client-side
###### Notice: this type of messages is only sent from the user to the server. A client that doesn't reply with an `ACK` to every server-side message that requires that is to be considered `disconnected`.
##### `ACK game|send|list|status`

+   `game`: in reply to a message regarding __Round Evolution__
+   `send`: if replying to a `SEND`
+   `list`: if replying to a `LIST` message from __Game management__
+   `status`: the information received concerned the change of a player's status

The receiver reports to the sender that he has received the information correctly. An `ACK` is always sent to the server in reply to every `SEND`,`GAME` or `STATUS` message.

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
