package it.polimi.ingsw.client.view.clientui.uielements.enums;


public enum UILanguage {
    ITA,
    ENG;

    /**
     * returns a UILanguage
     * @param value the string containing the name of the language
     * @return
     */
    public static UILanguage getLang(String value){
        return UILanguage.valueOf(value.toUpperCase());
    }
}
