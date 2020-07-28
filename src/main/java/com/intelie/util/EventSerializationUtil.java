package com.intelie.util;

import com.intelie.store.EventStoreList;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
    Serialize EventStoreList in FS
    Could be using Kryo for faster serialization/desertialization, but it does not accept structures with no default constructor (SynchronizedList in this case).
        To make it work, customizations on Kryo are needed.
 */
public class EventSerializationUtil {

    private static EventSerializationUtil instance;

    /*
        synchronize to be Thread safe
     */
    public static synchronized EventSerializationUtil getInstance() {
        instance = instance == null ? new EventSerializationUtil() : instance;
        return instance;
    }

    public synchronized void serializeEventStoreList(EventStoreList eventStoreList) throws IOException {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            URI uri = EventSerializationUtil.class.getResource("/").toURI();
            String mainPath = Paths.get(uri).toString();
            Path path = Paths.get(mainPath + "/backup/event.dat");
            fout = new FileOutputStream(path.toFile());
            oos = new ObjectOutputStream(fout);
            oos.writeObject(eventStoreList);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if(fout != null)
                fout.close();
            if(oos != null)
                oos.close();
        }
    }
    public synchronized EventStoreList deserializeEventStoreList() throws IOException {
        FileInputStream streamIn = null;
        ObjectInputStream objectinputstream = null;
        EventStoreList eventStoreList = null;
        try {
            URI uri = EventSerializationUtil.class.getResource("/").toURI();
            String mainPath = Paths.get(uri).toString();
            Path path = Paths.get(mainPath + "/backup/event.dat");
            if (Files.exists(path)) {
                streamIn = new FileInputStream(path.toFile());
                objectinputstream = new ObjectInputStream(streamIn);
                Object obj;
                while((obj = objectinputstream.readObject()) != null){
                    System.out.println((EventStoreList) obj);
                    return (EventStoreList) obj;
                }
            }
        } catch (FileNotFoundException | URISyntaxException | ClassNotFoundException e) {
            System.out.println("Failed create data file for serialized objects. Error: " + e);
        } finally {
            if(streamIn != null)
                streamIn.close();
            if(objectinputstream != null)
                objectinputstream.close();
        }
        return null;
    }

    public synchronized void removeSerializedEventStoreList() {
        try {
            URI uri = EventSerializationUtil.class.getResource("/").toURI();
            String mainPath = Paths.get(uri).toString();
            Path path = Paths.get(mainPath + "/backup/event.dat");
            if (Files.exists(path))
                Files.delete(path);
        } catch(Exception e) {
            //TODO:
            System.out.println("Erro");
        }
    }
}
