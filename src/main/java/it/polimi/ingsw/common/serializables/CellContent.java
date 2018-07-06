package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;

import java.io.Serializable;

public interface CellContent extends Serializable {
    boolean isDie();
    boolean hasColor();
    Shade getShade();
    DieColor getDieColor();

}
