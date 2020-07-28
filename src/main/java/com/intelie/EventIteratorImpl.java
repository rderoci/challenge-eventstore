package com.intelie;

import com.intelie.model.EventData;
import com.intelie.store.EventStoreList;

public class EventIteratorImpl implements EventIterator {

    private EventStoreList eventBase;
    private int cursor = -1;

    public EventIteratorImpl() {
        this.eventBase = EventStoreList.getInstance();
    }

    public boolean hasNext() {
        if(eventBase == null || ++cursor >= eventBase.getEvents().size()) {
            --cursor;
            return false;
        }
        --cursor;
        return true;
    }

    @Override
    public boolean moveNext() {
        if(eventBase == null || ++cursor >= eventBase.getEvents().size()) {
            cursor = -1;
            return false;
        }
        return true;
    }

    @Override
    public EventData current() {
        if(cursor == -1 || cursor >= eventBase.getEvents().size()) throw new IllegalStateException();
        return eventBase.getEvents().get(cursor);
    }

    @Override
    public void remove() {
        eventBase.getEvents().remove(current());
    }

    @Override
    public void close() throws Exception {
        //TODO
    }

    public EventStoreList getEventBase() {
        return eventBase;
    }
}
