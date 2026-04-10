package Tanu;
public class Student {
    private String id;
    private String name;
    private String department;
    private int marks;

    public Student(String id, String name, String department, int marks) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getMarks() {
        return marks;
    }

    // Converts student object to one line for file storage
    public String toFileString() {
        return id + "|" + name + "|" + department + "|" + marks;
    }

    // Builds Student object from one line of file
    public static Student fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|");
        if (parts.length != 4) {
            return null;
        }

        try {
            String id = parts[0].trim();
            String name = parts[1].trim();
            String department = parts[2].trim();
            int marks = Integer.parseInt(parts[3].trim());

            return new Student(id, name, department, marks);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

