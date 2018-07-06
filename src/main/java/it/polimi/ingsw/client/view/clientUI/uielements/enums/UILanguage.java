package it.polimi.ingsw.client.view.clientUI.uielements.enums;

public enum UILanguage {
    ITA,
    ENG;

    public static UILanguage getLang(String value){
        return UILanguage.valueOf(value.toUpperCase());
    }
}
