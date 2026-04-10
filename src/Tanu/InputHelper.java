package Tanu;

import java.util.Scanner;

public class InputHelper {
    private Scanner scanner;

    public InputHelper() {
        scanner = new Scanner(System.in);
    }

    // ✅ Menu choice validation
    public int readMenuChoice(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("Invalid choice. Enter between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter number only.");
            }
        }
    }

    // ✅ Student ID validation (NO duplicate check here)
    public String readStudentId(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Student ID cannot be empty.");
            } else if (input.contains("|")) {
                System.out.println("Character '|' not allowed.");
            } else if (!input.matches("[A-Za-z0-9_-]+")) {
                System.out.println("Only letters, numbers, _ and - allowed.");
            } else {
                return input;
            }
        }
    }

    // ✅ General text input
    public String readText(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
            } else if (input.contains("|")) {
                System.out.println("Character '|' not allowed.");
            } else {
                return input;
            }
        }
    }

    // ✅ Marks validation
    public int readMarks(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                int marks = Integer.parseInt(input);
                if (marks >= 0 && marks <= 100) {
                    return marks;
                } else {
                    System.out.println("Marks must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid marks. Enter number only.");
            }
        }
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}