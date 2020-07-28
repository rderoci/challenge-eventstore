package com.intelie.model;

import java.io.Serializable;

/*
    Objects of this Class are persisted (through EventStoreList.class) to be recovered in failure cases
    &&
    Objects of this Class are sent through web socket (DTO) to the clients (in the client side it's used to plot line chart)
 */
public class EventDataAxes implements Serializable {

    private Long timestamp;
    private Double value;

    public EventDataAxes(Long timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
