package com.intelie.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.intelie.EventIteratorImpl;
import com.intelie.EventStoreImpl;
import com.intelie.model.EventDataAxes;
import com.intelie.model.EventData;
import com.intelie.model.EventEntry;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;

import java.lang.reflect.Type;
import java.util.*;

public class EventUtil {

    public static EventEntry deserializeEvent(String message) {

         /*
            Convert input format (JSON similar) using GSonFire, mapping all attributes non 'type' and 'timestamp' in a Map structure
         */
        GsonFireBuilder builder = new GsonFireBuilder()
                .registerPostProcessor(EventEntry.class, new PostProcessor<EventEntry>() {
                    @Override
                    public void postDeserialize(EventEntry result, JsonElement src, Gson gson) {
                        Map<String, Object> dataMap = new HashMap<>();
                        Iterator<Map.Entry<String, JsonElement>> it = ((JsonObject) src).entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, JsonElement> entry = it.next();
                            if (!entry.getKey().equalsIgnoreCase("type") &&
                                    !entry.getKey().equalsIgnoreCase("timestamp")) {

                                if(entry.getValue() instanceof JsonArray) {
                                    Type type = new TypeToken<List<String>>(){}.getType();
                                    List<String> list = gson.fromJson(entry.getValue(), type);
                                    dataMap.put(entry.getKey(), list);
                                }

                                else if(entry.getValue() instanceof JsonPrimitive) {
                                    Type type = new TypeToken<String>(){}.getType();
                                    String str = gson.fromJson(entry.getValue(), type);
                                    dataMap.put(entry.getKey(), str);
                                }

                            }
                        }
                        result.setDataMap(dataMap);
                    }

                    @Override
                    public void postSerialize(JsonElement jsonElement, EventEntry event, Gson gson) {
                        return;
                    }
                });
        Gson gson = builder.createGson();
        EventEntry event = gson.fromJson(message, EventEntry.class);
        return event;
    }

    /*
        Build response JSON (String) to be sent through web socket to all clients. This JSON is used to plot line chart.
     */
    public static String getJSONResponse() {
        EventIteratorImpl eventIterator = (EventIteratorImpl) EventStoreImpl.getInstance().query("data", Long.MIN_VALUE, Long.MAX_VALUE);
        List<EventData> eventDataList = new ArrayList<>();
        List<EventDataAxes> eventDataAxesList = new ArrayList<>();
        EventData eventDataResponse = null;
        String field = null;
        List<EventDataAxes> eventResponseAxes = new ArrayList<>();


        String priorField = null;
        //O(n)
        while (eventIterator.moveNext()) {
            EventData currentEventData = eventIterator.current();

            if(priorField != null && !priorField.equalsIgnoreCase(currentEventData.getField())) {
                eventDataList.add(new EventData(priorField, eventDataAxesList));
                eventDataAxesList = new ArrayList<>();
            }

            eventDataAxesList.addAll(currentEventData.getEventDataAxes());

            if(!eventIterator.hasNext()) {
                eventDataList.add(new EventData(currentEventData.getField(), eventDataAxesList.size() == 0 ? currentEventData.getEventDataAxes() : eventDataAxesList));
            }

            priorField = currentEventData.getField();
        }

        return new Gson().toJson(eventDataList);
    }
}
