package com.intelie.store;

import com.intelie.model.EventData;
import java.io.Serializable;
import java.util.*;

/*
    Object of this Class is persisted (through EventStoreList.class) to be recovered in failure cases
 */
public class EventStoreList implements Serializable {

    private static EventStoreList instance;

    public static synchronized EventStoreList getInstance() {
        instance = instance == null ? new EventStoreList() : instance;
        return instance;
    }

    public static synchronized void setInstance(EventStoreList eventStoreList) {
        instance = eventStoreList;
    }

    private boolean started;
    /*
        SynchronizeList to be safe on concurrent threads
        ArrayList to be O(1) on 'get' and O(n) on 'add' at certain index position
     */
    private List<EventData> events = Collections.synchronizedList(new ArrayList());
    private Long validFrom;
    private Long validTo;
    private List<String> selectList;
    private List<String> groupList;

    public void clear() {
        started = false;
        events.clear();
        validFrom = null;
        validTo = null;
        selectList = null;
        groupList = null;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<EventData> getEvents() {
        return events == null ? new ArrayList<>() : events;
    }

    public void setEvents(List<EventData> events) {
        this.events = events;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Long getValidTo() {
        return validTo;
    }

    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }

    public List<String> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<String> selectList) {
        this.selectList = selectList;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }


}
