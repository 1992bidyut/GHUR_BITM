package bdnath.lictproject.info.ghur.FireBasePojoClass;

import java.io.Serializable;

public class EventHandler implements Serializable {
    private String eventID;
    private String eventTitle;
    private String eventPlace;
    private float eventCost;
    private String eventStartDate;
    private String eventEndDate;
    private String eventDetail;
    private String coverUrl;
    private float extraExpense;

    public EventHandler(String eventID, String eventTitle, String eventPlace, float eventCost, String eventStartDate, String eventEndDate, String eventDetail, float extraExpense) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventPlace = eventPlace;
        this.eventCost = eventCost;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventDetail = eventDetail;
        this.extraExpense = extraExpense;
    }

    public float getExtraExpense() {

        return extraExpense;
    }

    public void setExtraExpense(float extraExpense) {
        this.extraExpense = extraExpense;
    }

    public EventHandler() {
    }


    public String getEventID() {
        return eventID;
    }


    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public float getEventCost() {
        return eventCost;
    }

    public void setEventCost(float eventCost) {
        this.eventCost = eventCost;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
