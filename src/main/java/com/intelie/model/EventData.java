package com.intelie.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
    Objects of this Class are persisted (through EventStoreList.class) to be recovered in failure cases
    &&
    Objects of this Class are sent through web socket (DTO) to the clients (in the client side it's used to plot line chart)
 */
public class EventData implements Serializable {

    private String field;
    private transient Long timestamp;
    List<EventDataAxes> eventDataAxes;

    public EventData() {
    }

    public EventData(String field, Long timestamp, Double value){
        this.field = field;
        this.timestamp = timestamp;
        getEventDataAxes().add(new EventDataAxes(timestamp, value));
    }

    public EventData(String field, List<EventDataAxes> eventDataAxes){
        this.field = field;
        for(EventDataAxes eventDataAxe : eventDataAxes)
            this.getEventDataAxes().add(new EventDataAxes(eventDataAxe.getTimestamp(), eventDataAxe.getValue()));
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public EventData(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<EventDataAxes> getEventDataAxes() {
        if(eventDataAxes == null)
            eventDataAxes = new ArrayList<>();
        return eventDataAxes;
    }

    public void setEventDataAxes(List<EventDataAxes> eventDataAxes) {
        this.eventDataAxes = eventDataAxes;
    }
}
