package Tanu;
public class StudentManagementSystem {

    public static void main(String[] args) {
        InputHelper input = new InputHelper();
        StudentFileManager manager = new StudentFileManager("student.txt", "report.txt");

        System.out.println("==================================================");
        System.out.println(" SMART STUDENT FILE MANAGEMENT SYSTEM ");
        System.out.println("==================================================");
        manager.showFilePath();

        int choice;

        do {
            showMenu();
            choice = input.readMenuChoice("Enter your choice: ", 0, 12);

            switch (choice) {
                case 1:
                    addStudentFlow(input, manager);
                    break;
                case 2:
                    manager.viewStudents();
                    break;
                case 3:
                    deleteStudentFlow(input, manager);
                    break;
                case 4:
                    updateStudentFlow(input, manager);
                    break;
                case 5:
                    manager.sortStudentsByMarks(true);
                    break;
                case 6:
                    manager.sortStudentsByMarks(false);
                    break;
                case 7:
                    manager.displayTopper();
                    break;
                case 8:
                    System.out.println("Total students: " + manager.countStudents());
                    break;
                case 9:
                    searchStudentFlow(input, manager);
                    break;
                case 10:
                    manager.exportReport();
                    break;
                case 11:
                    manager.debugRawFileContent();
                    break;
                case 12:
                    manager.showFilePath();
                    break;
                case 0:
                    System.out.println("Exiting system... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);

        input.close();
    }

    private static void showMenu() {
        System.out.println("\n================== MENU ==================");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Delete Student");
        System.out.println("4. Update Student");
        System.out.println("5. Sort by Marks (Ascending)");
        System.out.println("6. Sort by Marks (Descending)");
        System.out.println("7. Display Topper");
        System.out.println("8. Count Total Students");
        System.out.println("9. Smart Search (ID or Name)");
        System.out.println("10. Export Report to report.txt");
        System.out.println("11. Debug Mode (Raw File Content)");
        System.out.println("12. Show File Path");
        System.out.println("0. Exit");
        System.out.println("==========================================");
    }

    private static void addStudentFlow(InputHelper input, StudentFileManager manager) {
        System.out.println("\n--- Add Student ---");

        String id;
        while (true) {
            id = input.readStudentId("Enter student ID: ");
            if (manager.idExists(id)) {
                System.out.println("This ID already exists. Enter a unique ID.");
            } else {
                break;
            }
        }

        String name = input.readText("Enter student name: ");
        String department = input.readText("Enter department/course: ");
        int marks = input.readMarks("Enter marks (0-100): ");

        Student student = new Student(id, name, department, marks);
        manager.addStudent(student);
    }

    private static void deleteStudentFlow(InputHelper input, StudentFileManager manager) {
        System.out.println("\n--- Delete Student ---");
        String id = input.readStudentId("Enter student ID to delete: ");
        manager.deleteStudent(id);
    }

    private static void updateStudentFlow(InputHelper input, StudentFileManager manager) {
        System.out.println("\n--- Update Student ---");
        String currentId = input.readStudentId("Enter existing student ID to update: ");

        if (!manager.idExists(currentId)) {
            System.out.println("Student ID not found.");
            return;
        }

        String newId;
        while (true) {
            newId = input.readStudentId("Enter new ID (or same ID): ");
            if (manager.idExistsExcludingCurrent(newId, currentId)) {
                System.out.println("This new ID already belongs to another student. Try again.");
            } else {
                break;
            }
        }

        String newName = input.readText("Enter new name: ");
        String newDepartment = input.readText("Enter new department/course: ");
        int newMarks = input.readMarks("Enter new marks (0-100): ");

        Student updatedStudent = new Student(newId, newName, newDepartment, newMarks);
        manager.updateStudent(currentId, updatedStudent);
    }

    private static void searchStudentFlow(InputHelper input, StudentFileManager manager) {
        System.out.println("\n--- Smart Search ---");
        String query = input.readText("Enter ID or Name to search: ");
        manager.smartSearch(query);
    }
}

