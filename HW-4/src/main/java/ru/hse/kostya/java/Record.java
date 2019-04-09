package ru.hse.kostya.java;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;

import java.util.Objects;

/**
 * Value class with name and phoneNumber fields
 *      prepared to be used in mongo database.
 */
@Entity
public class Record {
    @Id private ObjectId id;

    private String name;
    private String phoneNumber;


    public Record(@NotNull String name, @NotNull String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Record() {}

    @NotNull public String getName() {
        return name;
    }

    @NotNull public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setPhoneNumber(@NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return name.equals(record.name) &&
                phoneNumber.equals(record.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }


    @Override
    public String toString() {
        return "Record{ name='" + name + '\'' + ", phoneNumber='" + phoneNumber + '\'' + '}';
    }
}
