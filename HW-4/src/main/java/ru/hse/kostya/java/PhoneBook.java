package ru.hse.kostya.java;

import com.mongodb.*;
import xyz.morphia.*;

import java.util.List;

/**
 *
 * Database for storing {@link Record} instances.
 * Uses mongo service as database
 * And morphia library to work with it
 */
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
     * Creates new database with new Morphia object and new Mongo client
     * @param databaseName used as a name for new database
     */
    public PhoneBook(String databaseName) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("ru.hse.kostya.java.Record");
        datastore = morphia.createDatastore(new MongoClient(), databaseName);
        datastore.getDB().dropDatabase(); // cleaning database, to be sure it is empty
        datastore.ensureIndexes();
    }

    /**
     * Adds new record with given name and phoneNumber
     *      if there is no equal record in database.
     * @return {@code true} if element was added
     *     and {@code false} otherwise
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
     * Returns List of records contained in database with name equal to given.
     */
    public List<Record> findByName(String name) {
        return datastore.createQuery(Record.class).field("name").equal(name).asList();
    }

    /**
     * Returns List of records contained in database with phoneNumber equal to given.
     */
    public List<Record> findByPhoneNumber(String phoneNumber) {
        return datastore.createQuery(Record.class).field("phoneNumber").equal(phoneNumber).asList();
    }

    /**
     * Removes record with given name and phoneNumber
     *      if there is equal record in database.
     * @return {@code true} if element was removed
     *     and {@code false} otherwise
     */
    public boolean remove(String name, String phoneNumber) {
        final var record = new Record(name, phoneNumber);
        if (!contains(record)) {
            return false;
        }
        datastore.delete(matchingRecord(record));
        return true;
    }

    /**
     * Updates name of record if record was in database.
     * @param oldName name of record that is going to be updated
     * @param phoneNumber phoneNumber of record that is going to be updated
     * @param renewedName new name for record
     * @return {@code true} if element was updated
     *     and {@code false} otherwise
     */
    public boolean updateName(String oldName, String phoneNumber, String renewedName) {
        final var record = new Record(oldName, phoneNumber);
        if (!contains(record)) {
            return false;
        }
        var updateOperations = datastore.createUpdateOperations(Record.class).set("name", renewedName);
        datastore.update(matchingRecord(record), updateOperations);

        return true;
    }

    /**
     * Updates phoneNumbr of record if record was in database.
     * @param name name of record that is going to be updated
     * @param oldPhoneNumber phoneNumber of record that is going to be updated
     * @param renewedPhoneNumber new phoneNUmber for record
     * @return {@code true} if element was updated
     *     and {@code false} otherwise
     */
    public boolean updatePhoneNumber(String name, String oldPhoneNumber, String renewedPhoneNumber) {
        final var record = new Record(name, oldPhoneNumber);
        if (!contains(record)) {
            return false;
        }
        var updateOperations = datastore.createUpdateOperations(Record.class).set("phoneNumber", renewedPhoneNumber);
        datastore.update(matchingRecord(record), updateOperations);

        return true;
    }

    /**
     * Returns List of all records in database.
     */
    public List<Record> allRecords() {
        return datastore.find(Record.class).asList();
    }

    /**
     * Drop database.
     * Note: This will not delete the database, only all of its contents.
     */
    public void clear() {
        datastore.getDB().dropDatabase();
    }

}

