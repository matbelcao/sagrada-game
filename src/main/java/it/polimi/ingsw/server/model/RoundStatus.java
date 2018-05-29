package it.polimi.ingsw.server.model;

public class RoundStatus {
    private Boolean requestedSchemaList;
    private Boolean requestedRoundTrackList;
    private Boolean requestedDraftPoolList;
    private Die selectedDie;
    private boolean placedDie;

    public RoundStatus(){
        discard();
        placedDie=false;
    }

    private void discard(){
        requestedSchemaList=false;
        requestedRoundTrackList=false;
        requestedDraftPoolList=false;
        selectedDie=null;
    }

    public Boolean isRequestedSchemaList() {
        return requestedSchemaList;
    }

    public void setRequestedSchemaList() {
        discard();
        this.requestedSchemaList = true;
    }

    public Boolean isRequestedRoundTrackList() {
        return requestedRoundTrackList;
    }

    public void setRequestedRoundTrackList() {
        discard();
        this.requestedRoundTrackList = true;
    }

    public Boolean isRequestedDraftPoolList() {
        return requestedDraftPoolList;
    }

    public void setRequestedDraftPoolList() {
        discard();
        this.requestedDraftPoolList = true;
    }

    public Die getSelectedDie() {
        return selectedDie;
    }

    public void setSelectedDie(Die die) {
        this.selectedDie = die;
    }

    public Boolean isSelectedDie(){
        return this.selectedDie != null;
    }

    public boolean hasPlacedDie() {
        return placedDie;
    }

    public void setPlacedDie() {
        this.placedDie = true;
    }

}
