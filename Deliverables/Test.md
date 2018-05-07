# Test

#### Schema card
Verifiche su classe SchemaCard:
* Caricamento da file e inizializzazione degli attributi
* Corretto assegnamento dei constraint
* Possibili piazzamenti di un dado valido
* Piazzamento invalido primo dado su cella non di bordo
* Piazzamento valido del primo dado
* Piazzamento invalido dello stesso dado nella stessa cella
* Piazzamento invalido di un dado in una cella non adiacente ad altri dadi
* Piazzamento invalido di un dado in una cella occupata
* Piazzamento invalido di un dado in una cella con restrizione di colore
* Piazzamento invalido di un dado in una cella con restrizione di faccia
* Piazzamento valido in cella con restrizioni di colore
* Piazzamento valido in cella con restrizioni di faccia

#### Die
* Costruttore
* Aumento valore faccia
* Diminuzione valore faccia
* Setter
* flip
* toString
* Lancio eccezioni
 
#### Cell
* Costruttore
* Setter di dadi
* CanAcceptDie
* getConstraint
* Lancio eccezioni

#### Constraint
* toString
* isColorConstraint
* getColor
* getShade
* toUtf
* isActive

#### DiceBag
* Inizializzazione e verifica estrazione di 18 dadi per ogni colore
* Lancio eccezione all'estrazione del 91-esimo dado

####Private Objective Card
* Corretta apertura file e inizializzazione
* Verifica calcolo punteggio su SchemaCard riempita manualmente per ogni carta

####Public Objective Card
* Corretta apertura file e inizializzazione
* Verifica calcolo punteggio su SchemaCard riempita manualmente per ogni carta

#### Color
* Contains

#### Face
* Contains
* valueOf
* Lancio eccezioni