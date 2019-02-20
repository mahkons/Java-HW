package ru.hse.kostya.java;

import com.mongodb.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.morphia.*;

import java.util.List;

/**
 *
 * Database for storing {@link Record} instances.
 * Uses mongo service as database
 * And morphia library to work with it
 */
public class PhoneBook {

    @NotNull private final Datastore datastore;

    /**
     * Finds record in database equal(by name and phoneNumber) to given one.
     * @return found record or null if there is no such record in database
     */
    @Nullable
    private Record matchingRecord(@NotNull Record record){
        return datastore.find(Record.class).field("name").equal(record.getName()).
                field("phoneNumber").equal(record.getPhoneNumber()).get();
    }

    /**
     * Checks whether database contains record equal(by name and phoneNumber) to given.
     */
    public boolean contains(@NotNull Record record) {
        return matchingRecord(record) != null;
    }

    /**
     * Creates new database with new Morphia object and new Mongo client
     * @param databaseName used as a name for new database
     */
    public PhoneBook(@NotNull String databaseName) {
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
    public boolean add(@NotNull String name, @NotNull String phoneNumber) {
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
    @NotNull
    public List<Record> findByName(@NotNull String name) {
        return datastore.createQuery(Record.class).field("name").equal(name).asList();
    }

    /**
     * Returns List of records contained in database with phoneNumber equal to given.
     */
    @NotNull
    public List<Record> findByPhoneNumber(@NotNull String phoneNumber) {
        return datastore.createQuery(Record.class).field("phoneNumber").equal(phoneNumber).asList();
    }

    /**
     * Removes record with given name and phoneNumber
     *      if there is equal record in database.
     * @return {@code true} if element was removed
     *     and {@code false} otherwise
     */
    public boolean remove(@NotNull String name, @NotNull String phoneNumber) {
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
    public boolean updateName(@NotNull String oldName, @NotNull String phoneNumber, @NotNull String renewedName) {
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
    public boolean updatePhoneNumber(@NotNull String name, @NotNull String oldPhoneNumber, @NotNull String renewedPhoneNumber) {
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
    @NotNull
    public List<Record> allRecords() {
        return datastore.find(Record.class).asList();
    }

    /**s
     * Drop database.
     * Note: This will not delete the database, only all of its contents.
     */
    public void clear() {
        datastore.getDB().dropDatabase();
    }

}

