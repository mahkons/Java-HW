package ru.hse.kostya.java;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {

    private final PhoneBook phoneBook = new PhoneBook("TheGreatPhoneBook");
    private final PhoneBook emptyPhoneBook = new PhoneBook("SuperNotThatGreatPhoneBook");

    @BeforeEach
    void setUp() {
        phoneBook.add("Senya", "890");
        phoneBook.add("Max", "123");
        phoneBook.add("Masha", "sublime");
    }

    @Test
    void contains_existing() {
        assertTrue(phoneBook.contains(new Record("Masha", "sublime")));
    }

    @Test
    void contains_notExisting() {
        assertFalse(phoneBook.contains(new Record("Tosha", "bald")));
        assertFalse(emptyPhoneBook.contains(new Record("some", "some")));
    }

    @Test
    void add_notExisting() {
        assertTrue(phoneBook.add("Kostya", "hidden"));
        assertEquals(List.of(new Record("Kostya", "hidden")), phoneBook.findByName("Kostya"));
    }

    @Test
    void add_existing(){
        assertFalse(phoneBook.add("Senya", "890"));
    }

    @Test
    void findByName_existing() {
        phoneBook.add("Masha", "charming");
        assertEquals(List.of(new Record("Masha", "sublime"),
                new Record("Masha", "charming")), phoneBook.findByName("Masha"));
    }

    void findByName_notExisting() {
        assertTrue(phoneBook.findByName("Tosha").isEmpty());
        phoneBook.remove("Senya", "890");
        assertTrue(phoneBook.findByName("Senya").isEmpty());

        assertTrue(emptyPhoneBook.findByName("some").isEmpty());
    }

    @Test
    void findByPhoneNumber_existing() {
        phoneBook.add("TextEditor", "sublime");
        assertEquals(List.of(new Record("Masha", "sublime"),
                new Record("TextEditor", "sublime")), phoneBook.findByPhoneNumber("sublime"));
    }

    @Test
    void findByPhoneNumber_notExisting() {
        assertTrue(phoneBook.findByPhoneNumber("bald").isEmpty());
        phoneBook.remove("Senya", "890");
        assertTrue(phoneBook.findByPhoneNumber("890").isEmpty());

        assertTrue(emptyPhoneBook.findByPhoneNumber("some").isEmpty());
    }

    @Test
    void remove() {
        assertTrue(phoneBook.remove("Senya", "890"));
        assertTrue(phoneBook.findByName("Senya").isEmpty());
        assertFalse(phoneBook.remove("Senya", "890"));

        assertFalse(phoneBook.remove("Tosha", "bald"));
        assertFalse(emptyPhoneBook.remove("Tosha", "bald"));
    }

    @Test
    void updateName() {
        assertTrue(phoneBook.updateName("Senya", "890", "Sonya"));
        assertEquals(List.of(new Record("Sonya", "890")), phoneBook.findByName("Sonya"));
        assertFalse(phoneBook.updateName("Senya", "890", "Sonya"));

        assertFalse(emptyPhoneBook.updateName("some", "better", "some"));
    }

    @Test
    void updatePhoneNumber() {
        assertTrue(phoneBook.updatePhoneNumber("Senya", "890", "+790"));
        assertEquals(List.of(new Record("Senya", "+790")), phoneBook.findByPhoneNumber("+790"));
        assertFalse(phoneBook.updatePhoneNumber("Senya", "890", "+790"));

        assertFalse(emptyPhoneBook.updateName("some", "better", "some"));
    }

    @Test
    void allRecords() {
        assertEquals(List.of(new Record("Senya", "890"),
                new Record("Max", "123"),
                new Record("Masha", "sublime")),
                        phoneBook.allRecords());

        assertTrue(emptyPhoneBook.allRecords().isEmpty());

        phoneBook.add("Senya", "+790");
        phoneBook.remove("Max", "123");

        assertEquals(List.of(new Record("Senya", "890"),
                new Record("Masha", "sublime"),
                new Record("Senya", "+790")),
                phoneBook.allRecords());
    }

    @AfterEach
    void clear() {
        phoneBook.clear();
        emptyPhoneBook.clear();
    }
}