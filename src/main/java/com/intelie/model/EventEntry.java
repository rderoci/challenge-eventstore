package com.intelie.model;


import java.util.HashMap;
import java.util.Map;

/*
    Objects of this Class is DTO to the Server web socket
 */
public class EventEntry {


    private String type;
    private Long timestamp;
    private String key;
    Map<String, Object> dataMap = new HashMap<>();

    public EventEntry(String type, Long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public String type() {
        return type;
    }

    public Long timestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getKey() {
        return key == null ? "" : key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
