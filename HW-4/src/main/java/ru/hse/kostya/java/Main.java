package ru.hse.kostya.java;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Method that interacts with user by treating query to database.
 * Uses System.in System.out for interaction
 */
public class Main {

    private static PhoneBook phoneBook = new PhoneBook("GreatPhoneBook");
    private static Scanner inputScanner = new Scanner(System.in);
    static {
        inputScanner.useDelimiter(System.getProperty("line.separator"));
    }

    /**
     * All available commands.
     */
    private static final String COMMANDS = "0 - exit\n" +
            "1 - add record(name and phone number)\n" +
            "2 - find phoneNumbers by name\n" +
            "3 - find names by phoneNumber\n" +
            "4 - delete record\n" +
            "5 - update name for record\n" +
            "6 - update phoneNumber for record\n" +
            "7 - print all records\n";


    /**
     * Using System.out asks user to write a name and
     *      get it from System.in with scanner.
     */
    private static String readName() {
        System.out.print("Write name: ");
        String name = inputScanner.next();
        System.out.println("Name accepted");
        return name;
    }

    /**
     * Using System.out asks user to write a phoneNumber and
     *      get it from System.in with scanner.
     */
    private static String readPhoneNumber() {
        System.out.print("Write phone number: ");
        String phoneNumber = inputScanner.next();
        System.out.println("Phone number accepted");
        return phoneNumber;
    }

    public static void main(String[] args) {

        boolean isInteracting = true;

        System.out.println(COMMANDS);

        while (isInteracting) {
            int query = 0;
            System.out.print("Your query:\n");
            try {
                query = inputScanner.nextInt();
            } catch (InputMismatchException e) {
                inputScanner.next();
                System.out.println("Token does not match " +
                        "the Integer regular expression, or is out of range");
                continue;
            }

            switch (query) {
                case 0: {
                    isInteracting = false;
                    break;
                }
                case 1: {
                    add();
                    break;
                }
                case 2: {
                    getPhonesByName();
                    break;
                }
                case 3: {
                    getNamesByPhone();
                    break;
                }
                case 4: {
                    remove();
                    break;
                }
                case 5: {
                    updatingRecordName();
                    break;
                }
                case 6: {
                    updatingRecordPhoneNumber();
                    break;
                }
                case 7: {
                    printAllRecords();
                    break;
                }
                default: {
                    System.out.println("Wrong command");
                    break;
                }
            }

        }
    }

    /**
     * Asks user to write a name and phoneNumber and adds
     *  that record to database.
     * Uses System.in System.out for interaction
     */
    private static void add() {
        System.out.println("Adding new record");
        String name = readName();
        String phoneNumber = readPhoneNumber();
        boolean wasAdded = phoneBook.add(name, phoneNumber);
        if (wasAdded) {
            System.out.println("Record added successfully");
        } else {
            System.out.println("Record already in phonebook");
        }
    }

    /**
     * Asks user to write a name and prints
     *  all phones with equal name from database.
     * Uses System.in System.out for interaction
     */
    private static void getPhonesByName() {
        System.out.println("Get phones by name:");
        String name = readName();
        for (Record record : phoneBook.findByName(name)) {
            System.out.println(record.getPhoneNumber());
        }
        System.out.println("All phone numbers for given name printed");
    }

    /**
     * Asks user to write a phone number and prints
     *  all names with equal phone from database.
     * Uses System.in System.out for interaction
     */
    private static void getNamesByPhone() {
        System.out.println("Get names by phone:");
        String phoneNumber = readPhoneNumber();
        for (Record record : phoneBook.findByPhoneNumber(phoneNumber)) {
            System.out.println(record.getName());
        }
        System.out.println("All names for given phone number printed");
    }

    /**
     * Asks user to write a name and phoneNumber and removes
     *  that record to database.
     * Uses System.in System.out for interaction
     */
    private static void remove() {
        System.out.println("Removing record");
        String name = readName();
        String phoneNumber = readPhoneNumber();
        boolean wasRemoved = phoneBook.remove(name, phoneNumber);
        if (wasRemoved) {
            System.out.println("Record removed successfully");
        } else {
            System.out.println("No such record in database");
        }
    }

    /**
     * Asks user to write a name and phoneNumber and updates
     *  that record to database with one more name asked from user.
     * Uses System.in System.out for interaction
     */
    private static void updatingRecordName() {
        System.out.println("Updating records name");
        System.out.println("Record you like to update:");
        String oldName = readName();
        String phoneNumber = readPhoneNumber();
        System.out.println("New name for record");
        String name = readName();
        boolean wasUpdated = phoneBook.updateName(oldName, phoneNumber, name);
        if (wasUpdated) {
            System.out.println("Record updated successfully");
        } else {
            System.out.println("No such record in database");
        }
    }

    /**
     * Asks user to write a name and phoneNumber and updates
     *  that record to database with one more phone number asked from user.
     * Uses System.in System.out for interaction
     */
    private static void updatingRecordPhoneNumber() {
        System.out.println("Updating records phone number");
        System.out.println("Record you like to update:");
        String name = readName();
        String oldPhoneNumber = readPhoneNumber();
        System.out.println("New phone number for record");
        String phoneNumber = readName();
        boolean wasUpdated = phoneBook.updatePhoneNumber(name, oldPhoneNumber, phoneNumber);
        if (wasUpdated) {
            System.out.println("Record updated successfully");
        } else {
            System.out.println("No such record in database");
        }
    }

    /**
     * Prints all records from database.
     */
    private static void printAllRecords() {
        System.out.println("Printing all records in database");
        for (Record record : phoneBook.allRecords()) {
            System.out.println(record);
        }
        System.out.println("All records printed");
    }

}
