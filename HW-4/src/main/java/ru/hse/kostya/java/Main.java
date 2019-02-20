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
                case 0:
                    isInteracting = false;
                    break;
                case 1:

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 7:

                    break;
                default:
                    System.out.println("Wrong command");
                    break;
            }

        }
    }
}
