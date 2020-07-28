package com.intelie;

import com.intelie.model.EventData;
import com.intelie.model.EventDataAxes;
import com.intelie.model.EventEntry;
import com.intelie.store.EventStoreList;
import com.intelie.util.EventSerializationUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EventStoreImpl implements EventStore {

    private static EventStoreImpl instance;

    public static synchronized EventStoreImpl getInstance() {
        instance = instance == null ? new EventStoreImpl() : instance;
        return instance;
    }

    EventStoreList eventStoreList;

    public EventStoreImpl() {
        this.eventStoreList = EventStoreList.getInstance();
    }


    @Override
    public synchronized void insert(EventEntry eventDTO) {

        if (!eventDTO.getType().equalsIgnoreCase("stop")) {
            if (eventDTO.getType().equalsIgnoreCase("span")) {
                eventStoreList.setValidFrom(Long.parseLong((String) eventDTO.getDataMap().get("begin")));
                eventStoreList.setValidTo(Long.parseLong((String) eventDTO.getDataMap().get("end")));

            } else if (eventDTO.getType().equalsIgnoreCase("start")) {
                if (eventStoreList.getEvents().size() > 0) return; //ignore
                eventStoreList.setSelectList((List<String>) eventDTO.getDataMap().get("select"));
                eventStoreList.setGroupList((List<String>) eventDTO.getDataMap().get("group"));
                eventStoreList.setStarted(true);

            } else if (eventDTO.getType().equalsIgnoreCase("data")) {
                if (eventDTO.getTimestamp() > eventStoreList.getValidTo() || eventDTO.getTimestamp() < eventStoreList.getValidFrom()) {
                    //TODO: Throw an Exception and log it
                    return;
                }

                List<String> field = new ArrayList<>();
                for (Map.Entry<String, Object> entry : eventDTO.getDataMap().entrySet()) {
                    if (eventStoreList.getGroupList().contains(entry.getKey()))
                        field.add((String) entry.getValue());
                }

                for (Map.Entry<String, Object> entry : eventDTO.getDataMap().entrySet()) {
                    if (eventStoreList.getSelectList().contains(entry.getKey())) {
                        EventData eventData = new EventData(StringUtils.join(field, " ") + " " + entry.getKey(),
                                eventDTO.getTimestamp(),
                                Double.parseDouble((String) entry.getValue()));

                        //Insert in last position - O(1)
                        if (eventStoreList.getEvents().size() == 0) {
                            eventStoreList.getEvents().add(eventData);

                        } else {
                            //Binary search - O(n)
                            int index = Collections.binarySearch(eventStoreList.getEvents(), eventData,
                                    Comparator.comparing(EventData::getField)
                                            .thenComparing(EventData::getTimestamp));
                            if (index < 0) {
                                index = -index - 1;
                            }
                            eventStoreList.getEvents().add(index, eventData);
                        }
                    }
                }
            }

            try {
                synchronized (this) {
                    EventSerializationUtil.getInstance().serializeEventStoreList(eventStoreList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (eventDTO.getType().equalsIgnoreCase("stop")) {
            eventStoreList.clear();
            EventSerializationUtil.getInstance().removeSerializedEventStoreList();

        } else {
            //TODO: throw error for invalid type
            return;
        }
    }

    @Override
    public synchronized void removeAll(String type) {
        eventStoreList.getEvents().clear();
    }

    @Override
    public EventIterator query(String type, long startTime, long endTime) {

        if(startTime != Long.MIN_VALUE && endTime != Long.MAX_VALUE) { //return all if begin/end range undefined
            eventStoreList.getEvents()
                    .stream()
                    .filter(e -> {
                        for(EventDataAxes eventDataAxes : e.getEventDataAxes())
                            return startTime >= eventDataAxes.getTimestamp() && endTime <= eventDataAxes.getTimestamp();
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        return new EventIteratorImpl();
    }

    public synchronized void remove(EventEntry event) {
        eventStoreList.getEvents().remove(event);
    }
}
