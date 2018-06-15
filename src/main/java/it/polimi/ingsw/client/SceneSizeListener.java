package it.polimi.ingsw.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public interface SceneSizeListener extends ChangeListener<Number> {
    void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue);
}
