package ru.hse.kostya.java;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final String COMMANDS = "0 - exit\n" +
            "1 - add record(name and phone number)\n" +
            "2 - find phoneNumbers by name\n" +
            "3 - find names by phoneNumber\n" +
            "4 - delete record\n" +
            "5 - update name for record\n" +
            "6 - update phoneNumber for record\n" +
            "7 - print all records\n";

    private static String readName(Scanner inputScanner) {
        System.out.print("Write name: ");
        String name = inputScanner.nextLine();
        System.out.println("Name accepted");
        return name;
    }

    private static String readPhoneNumber(Scanner inputScanner) {
        System.out.print("Write phone number: ");
        String phoneNumber = inputScanner.nextLine();
        System.out.println("Phone number accepted");
        return phoneNumber;
    }

    public static void main(String[] args) {

        var phoneBook = new PhoneBook("GreatPhoneBook");
        var inputScanner = new Scanner(System.in);
        boolean isInteracting = true;

        System.out.println(COMMANDS);

        while (isInteracting) {
            int query = 0;
            try {
                query = inputScanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Token does not match the Integer regular expression, or is out of range");
                continue;
            }

            switch (query) {
                case 0: {
                    isInteracting = false;
                    break;
                }
                case 1: {
                    add(inputScanner, phoneBook);
                    break;
                }
                case 2: {
                    getPhonesByName(inputScanner, phoneBook);
                    break;
                }
                case 3: {
                    getNamesByPhone(inputScanner, phoneBook);
                    break;
                }
                case 4: {
                    remove(inputScanner, phoneBook);
                    break;
                }
                case 5:{

                    break;
                }
                case 6: {
                    updatingRecordName(inputScanner, phoneBook);
                    break;
                }
                case 7: {
                    printAllRecords(inputScanner, phoneBook);
                    break;
                }
                default: {
                    System.out.println("Wrong command");
                    break;
                }
            }

        }
    }

    private static void add(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Adding new record");
        String name = readName(inputScanner);
        String phoneNumber = readPhoneNumber(inputScanner);
        boolean wasAdded = phoneBook.add(name, phoneNumber);
        if (wasAdded) {
            System.out.println("Record added successfully");
        } else {
            System.out.println("Record already in phonebook");
        }
    }
    private static void getPhonesByName(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Get phones by name:");
        String name = readName(inputScanner);
        for (Record record : phoneBook.findByName(name)) {
            System.out.println(record.getPhoneNumber());
        }
        System.out.println("All phone numbers for given name added");
    }
    private static void getNamesByPhone(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Get names by phone:");
        String phoneNumber = readPhoneNumber(inputScanner);
        for (Record record : phoneBook.findByName(phoneNumber)) {
            System.out.println(record.getName());
        }
        System.out.println("All names for given phone number added");
    }
    private static void remove(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Removing record");
        String name = readName(inputScanner);
        String phoneNumber = readPhoneNumber(inputScanner);
        boolean wasRemoved = phoneBook.remove(name, phoneNumber);
        if (wasRemoved) {
            System.out.println("Record removed successfully");
        } else {
            System.out.println("No such record in database");
        }
    }
    private static void updatingRecordName(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Updating records name");
        System.out.println("Record you like to update:");
        String oldName = readName(inputScanner);
        String phoneNumber = readPhoneNumber(inputScanner);
        System.out.println("New name for record");
        String name = readName(inputScanner);
        boolean wasUpdated = phoneBook.updateName(oldName, phoneNumber, name);
        if (wasUpdated) {
            System.out.println("Record updated successfully");
        } else {
            System.out.println("No such record in database");
        }
    }
    private static void updatingRecordPhoneNumber(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Updating records phone number");
        System.out.println("Record you like to update:");
        String name = readName(inputScanner);
        String oldPhoneNumber = readPhoneNumber(inputScanner);
        System.out.println("New phone number for record");
        String phoneNumber = readName(inputScanner);
        boolean wasUpdated = phoneBook.updatePhoneNumber(name, oldPhoneNumber, phoneNumber);
        if (wasUpdated) {
            System.out.println("Record updated successfully");
        } else {
            System.out.println("No such record in database");
        }
    }
    private static void printAllRecords(Scanner inputScanner, PhoneBook phoneBook) {
        System.out.println("Printing all records in database");
        for (Record record : phoneBook.allRecords()) {
            System.out.println(record);
        }
        System.out.println("All records printed");
    }

}
