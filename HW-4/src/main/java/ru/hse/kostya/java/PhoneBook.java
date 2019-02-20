package ru.hse.kostya.java;

import com.mongodb.*;
import xyz.morphia.*;

import java.util.List;

public class PhoneBook {
    private final Datastore datastore;

    /**
     * Finds record in database equal(by name and phoneNumber) to given one.
     * @return found record or null if there is no such record in database
     */
    private Record matchingRecord(Record record){
        return datastore.find(Record.class).field("name").equal(record.getName()).
                field("phoneNumber").equal(record.getPhoneNumber()).get();
    }

    /**
     * Checks whether database contains record equal(by name and phoneNumber) to given.
     */
    public boolean contains(Record record) {
        return matchingRecord(record) != null;
    }

    /**
     * @param databaseName
     */
    public PhoneBook(String databaseName) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("ru.hse.kostya.java.Record");
        datastore = morphia.createDatastore(new MongoClient(), databaseName);
        datastore.ensureIndexes();
    }

    /**
     * @param name
     * @param phoneNumber
     * @return
     */
    public boolean add(String name, String phoneNumber) {
        final var record = new Record(name, phoneNumber);
        if (contains(record)) {
            return false;
        }
        datastore.save(record);
        return true;
    }

    /**
     * @param name
     * @return
     */
    public List<Record> findByName(String name) {
        return datastore.createQuery(Record.class).field("name").equal(name).asList();
    }

    public List<Record> findByPhoneNumber(String phoneNumber) {
        return datastore.createQuery(Record.class).field("phoneNumber").equal(phoneNumber).asList();
    }

    public boolean remove(String name, String phoneNumber) {
        final var record = new Record(name, phoneNumber);
        if (!contains(record)) {
            return false;
        }
        datastore.delete(matchingRecord(record));
        return true;
    }

    public boolean updateName(String oldName, String phoneNumber, String renewedName) {
        final var record = new Record(oldName, phoneNumber);
        if (!contains(record)) {
            return false;
        }
        var updateOperations = datastore.createUpdateOperations(Record.class).set("name", renewedName);
        datastore.update(matchingRecord(record), updateOperations);

        return true;
    }

    public boolean updatePhoneNumber(String name, String oldPhoneNumber, String renewedPhoneNumber) {
        final var record = new Record(name, oldPhoneNumber);
        if (!contains(record)) {
            return false;
        }
        var updateOperations = datastore.createUpdateOperations(Record.class).set("phoneNumber", renewedPhoneNumber);
        datastore.update(matchingRecord(record), updateOperations);

        return true;
    }

    public List<Record> allRecords() {
        return datastore.find(Record.class).asList();
    }

    public void clear() {
        datastore.getDB().dropDatabase();
    }

}

