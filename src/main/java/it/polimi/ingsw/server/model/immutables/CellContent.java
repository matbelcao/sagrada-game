package it.polimi.ingsw.server.model.immutables;

import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Face;

public interface CellContent{
    boolean isDie();
    boolean hasColor();
    Face getShade();
    Color getColor();

}
