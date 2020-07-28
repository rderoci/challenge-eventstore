package com.intelie.websocket;

import com.intelie.model.EventEntry;
import com.intelie.EventStoreImpl;
import com.intelie.store.EventStoreList;
import com.intelie.util.EventSerializationUtil;
import com.intelie.util.EventUtil;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/*
    Web Socket
 */
@ServerEndpoint("/event")
@ApplicationScoped
public class EventSocket {

    private static final Logger LOG = Logger.getLogger(EventSocket.class);

    List<Session> sessions = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        /*
            Hold sessions that need to be advised in case of new Events arrive
         */
        sessions.add(session);

        /*
            Restore serialized backup in case of any unexpected failures
         */
        if(!EventStoreList.getInstance().isStarted()) {
            EventStoreList eventStoreList = EventSerializationUtil.getInstance().deserializeEventStoreList();
            EventStoreList.setInstance(eventStoreList);
        }

        emitEvents();
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message) {
        sendEvent(message);
    }

    private void sendEvent(String message) {
        EventEntry eventDTO = EventUtil.deserializeEvent(message);

        /*
            Save Event in memory
         */
        EventStoreImpl.getInstance().insert(eventDTO);

        emitEvents();
    }

    private void emitEvents() {
        String jsonResponse = EventUtil.getJSONResponse();
        System.out.println(jsonResponse);
        /*
            Emit async message to clients that are listening
         */
            sessions.forEach(s -> {
                s.getAsyncRemote().sendObject(jsonResponse, result -> {
                    if (!result.isOK()) {
                        LOG.error("Failed to send message: "+result.getException().getMessage());
                    }
                });
            });
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("onError", throwable);
    }

}
