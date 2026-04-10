package Tanu;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class StudentFileManager {
    private File studentFile;
    private File reportFile;

    public StudentFileManager(String studentFileName, String reportFileName) {
        studentFile = new File(studentFileName);
        reportFile = new File(reportFileName);
        ensureStudentFileExists();
    }

    // Create student file if it does not exist
    private void ensureStudentFileExists() {
        try {
            if (!studentFile.exists()) {
                if (studentFile.createNewFile()) {
                    System.out.println("student.txt created successfully.");
                } else {
                    System.out.println("Could not create student.txt.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public void showFilePath() {
        System.out.println("Student file path : " + studentFile.getAbsolutePath());
        System.out.println("Report file path  : " + reportFile.getAbsolutePath());
    }

    // Check if ID already exists
    public boolean idExists(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);
                if (student != null && student.getId().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file while checking ID: " + e.getMessage());
        }
        return false;
    }

    // Check duplicate ID excluding currently edited student
    public boolean idExistsExcludingCurrent(String newId, String currentId) {
        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);
                if (student != null &&
                        student.getId().equalsIgnoreCase(newId) &&
                        !student.getId().equalsIgnoreCase(currentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file while checking duplicate ID: " + e.getMessage());
        }
        return false;
    }

    // Add new student directly to file
    public boolean addStudent(Student student) {
        if (idExists(student.getId())) {
            System.out.println("Duplicate ID not allowed.");
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(studentFile, true))) {
            if (studentFile.length() > 0) {
                bw.newLine();
            }
            bw.write(student.toFileString());
            System.out.println("Student added successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }

    // View all students by reading directly from file
    public void viewStudents() {
        int count = 0;
        int invalidLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            boolean headerPrinted = false;

            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);

                if (student != null) {
                    if (!headerPrinted) {
                        printTableHeader();
                        headerPrinted = true;
                    }
                    printStudent(student);
                    count++;
                } else if (!line.trim().isEmpty()) {
                    invalidLines++;
                }
            }

            if (count > 0) {
                printTableFooter();
            } else {
                System.out.println("No student records found. File is empty.");
            }

            if (invalidLines > 0) {
                System.out.println("Warning: " + invalidLines + " invalid line(s) ignored.");
            }

        } catch (IOException e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }
    }

    // Delete student using temp file logic
    public boolean deleteStudent(String id) {
        File tempFile = createSiblingTempFile("student_delete_temp.txt");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean firstWritten = false;

            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);

                if (student != null && student.getId().equalsIgnoreCase(id)) {
                    found = true;
                    continue;
                }

                if (firstWritten) {
                    bw.newLine();
                }
                bw.write(line);
                firstWritten = true;
            }

        } catch (IOException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            return false;
        }

        if (!found) {
            System.out.println("Student with ID '" + id + "' not found.");
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return false;
        }

        if (replaceOriginalFromTemp(tempFile)) {
            System.out.println("Student deleted successfully.");
            return true;
        } else {
            System.out.println("Failed to update original file after delete.");
            return false;
        }
    }

    // Update student using temp file logic
    public boolean updateStudent(String currentId, Student updatedStudent) {
        File tempFile = createSiblingTempFile("student_update_temp.txt");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean firstWritten = false;

            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);

                if (student != null && student.getId().equalsIgnoreCase(currentId)) {
                    line = updatedStudent.toFileString();
                    found = true;
                }

                if (firstWritten) {
                    bw.newLine();
                }
                bw.write(line);
                firstWritten = true;
            }

        } catch (IOException e) {
            System.out.println("Error updating student: " + e.getMessage());
            return false;
        }

        if (!found) {
            System.out.println("Student with ID '" + currentId + "' not found.");
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return false;
        }

        if (replaceOriginalFromTemp(tempFile)) {
            System.out.println("Student updated successfully.");
            return true;
        } else {
            System.out.println("Failed to update original file after modification.");
            return false;
        }
    }

    // Search by partial ID or partial name
    public void smartSearch(String query) {
        int foundCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            boolean headerPrinted = false;

            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);

                if (student != null) {
                    boolean matchId = student.getId().toLowerCase().contains(query.toLowerCase());
                    boolean matchName = student.getName().toLowerCase().contains(query.toLowerCase());

                    if (matchId || matchName) {
                        if (!headerPrinted) {
                            printTableHeader();
                            headerPrinted = true;
                        }
                        printStudent(student);
                        foundCount++;
                    }
                }
            }

            if (foundCount > 0) {
                printTableFooter();
                System.out.println("Search result count: " + foundCount);
            } else {
                System.out.println("No matching student found for: " + query);
            }

        } catch (IOException e) {
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    // Count students from file
    public int countStudents() {
        return countStudentsInFile(studentFile);
    }

    // Display highest marks student
    public void displayTopper() {
        Student topper = null;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);

                if (student != null) {
                    if (topper == null || student.getMarks() > topper.getMarks()) {
                        topper = student;
                    }
                }
            }

            if (topper == null) {
                System.out.println("No student data available to find topper.");
            } else {
                System.out.println("\nTopper Details:");
                printTableHeader();
                printStudent(topper);
                printTableFooter();
            }

        } catch (IOException e) {
            System.out.println("Error finding topper: " + e.getMessage());
        }
    }

    // Sort students by marks using only file operations
    public void sortStudentsByMarks(boolean ascending) {
        if (countStudents() == 0) {
            System.out.println("No records available to sort.");
            return;
        }

        File workingFile = createSiblingTempFile("working_students_temp.txt");
        File sortedFile = createSiblingTempFile("sorted_students_temp.txt");

        try {
            copyFile(studentFile, workingFile);
            clearFile(sortedFile);

            String selectedLine;

            while ((selectedLine = findExtremeLine(workingFile, ascending)) != null) {
                appendLine(sortedFile, selectedLine);
                removeFirstOccurrence(workingFile, selectedLine);
            }

            copyFile(sortedFile, studentFile);

            System.out.println("Students sorted by marks in " +
                    (ascending ? "ascending" : "descending") + " order.");
            System.out.println("student.txt updated successfully.");

        } catch (IOException e) {
            System.out.println("Error sorting file: " + e.getMessage());
        } finally {
            if (workingFile.exists()) {
                workingFile.delete();
            }
            if (sortedFile.exists()) {
                sortedFile.delete();
            }
        }
    }

    // Export report to another file
    public void exportReport() {
        int totalStudents = 0;
        Student topper = null;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile))) {

            bw.write("SMART STUDENT FILE MANAGEMENT SYSTEM REPORT");
            bw.newLine();
            bw.write("Generated on: " + LocalDateTime.now());
            bw.newLine();
            bw.write("Student file path: " + studentFile.getAbsolutePath());
            bw.newLine();
            bw.write("------------------------------------------------------------");
            bw.newLine();

            String line;
            while ((line = br.readLine()) != null) {
                Student student = Student.fromFileString(line);
                if (student != null) {
                    totalStudents++;
                    if (topper == null || student.getMarks() > topper.getMarks()) {
                        topper = student;
                    }
                }
            }

            bw.write("Total Students: " + totalStudents);
            bw.newLine();

            if (topper != null) {
                bw.write("Topper: " + topper.getName() + " (ID: " + topper.getId() + ", Marks: " + topper.getMarks() + ")");
            } else {
                bw.write("Topper: No data available");
            }
            bw.newLine();
            bw.write("-----------------------------------------------------------------------------");
            bw.newLine();
            bw.write(String.format("%-15s %-25s %-20s %-10s", "ID", "Name", "Department", "Marks"));
            bw.newLine();
            bw.write("-----------------------------------------------------------------------------");
            bw.newLine();

            // Read again to export details
            try (BufferedReader br2 = new BufferedReader(new FileReader(studentFile))) {
                while ((line = br2.readLine()) != null) {
                    Student student = Student.fromFileString(line);
                    if (student != null) {
                        bw.write(String.format("%-15s %-25s %-20s %-10d",
                                student.getId(),
                                student.getName(),
                                student.getDepartment(),
                                student.getMarks()));
                        bw.newLine();
                    }
                }
            }

            bw.write("-----------------------------------------------------------------------------");
            bw.newLine();

            System.out.println("Report exported successfully to: " + reportFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }

    // Debug mode: show raw file content
    public void debugRawFileContent() {
        System.out.println("\n===== DEBUG MODE: RAW FILE CONTENT =====");
        System.out.println("File path: " + studentFile.getAbsolutePath());

        if (!studentFile.exists()) {
            System.out.println("File not found.");
            return;
        }

        if (studentFile.length() == 0) {
            System.out.println("File is empty.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                System.out.println(lineNumber + " -> " + line);
                lineNumber++;
            }

        } catch (IOException e) {
            System.out.println("Error reading raw file content: " + e.getMessage());
        }
    }

    // ---------- Helper methods ----------

    private void printTableHeader() {
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-15s %-25s %-20s %-10s%n", "ID", "Name", "Department", "Marks");
        System.out.println("----------------------------------------------------------------------------");
    }

    private void printStudent(Student student) {
        System.out.printf("%-15s %-25s %-20s %-10d%n",
                student.getId(),
                student.getName(),
                student.getDepartment(),
                student.getMarks());
    }

    private void printTableFooter() {
        System.out.println("----------------------------------------------------------------------------");
    }

    private int countStudentsInFile(File file) {
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (Student.fromFileString(line) != null) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error counting students: " + e.getMessage());
        }

        return count;
    }

    private File createSiblingTempFile(String name) {
        File parent = studentFile.getAbsoluteFile().getParentFile();
        return new File(parent, name);
    }

    private boolean replaceOriginalFromTemp(File tempFile) {
        try {
            copyFile(tempFile, studentFile);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error replacing original file: " + e.getMessage());
            return false;
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(source));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destination))) {

            String line;
            boolean firstWritten = false;

            while ((line = br.readLine()) != null) {
                if (firstWritten) {
                    bw.newLine();
                }
                bw.write(line);
                firstWritten = true;
            }
        }
    }

    private void clearFile(File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // opening in overwrite mode automatically clears file
        }
    }

    private void appendLine(File file, String line) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            if (file.length() > 0) {
                bw.newLine();
            }
            bw.write(line);
        }
    }

    // Find minimum or maximum marks line by scanning file
    private String findExtremeLine(File file, boolean ascending) throws IOException {
        String selectedLine = null;
        Student selectedStudent = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                Student current = Student.fromFileString(line);

                if (current == null) {
                    continue;
                }

                if (selectedStudent == null) {
                    selectedStudent = current;
                    selectedLine = line;
                } else {
                    boolean condition;
                    if (ascending) {
                        condition = current.getMarks() < selectedStudent.getMarks();
                    } else {
                        condition = current.getMarks() > selectedStudent.getMarks();
                    }

                    if (condition) {
                        selectedStudent = current;
                        selectedLine = line;
                    }
                }
            }
        }

        return selectedLine;
    }

    // Remove only the first occurrence of selected line from working file
    private void removeFirstOccurrence(File file, String targetLine) throws IOException {
        File tempFile = createSiblingTempFile("remove_one_temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean removed = false;
            boolean firstWritten = false;

            while ((line = br.readLine()) != null) {
                if (!removed && line.equals(targetLine)) {
                    removed = true;
                    continue;
                }

                if (firstWritten) {
                    bw.newLine();
                }
                bw.write(line);
                firstWritten = true;
            }
        }

        copyFile(tempFile, file);

        if (tempFile.exists()) {
            tempFile.delete();
        }
    }
}

