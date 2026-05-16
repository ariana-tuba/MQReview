/** Represents a staff member (lecturer or tutor) associated with a review. */
public class Person {
    public enum Role { LECTURER, TUTOR }

    private String name;
    private Role role;
    private String department;

    /**
     * Creates a Person with the given details.
     * @param name       full name of the staff member
     * @param role       LECTURER or TUTOR
     * @param department department the staff member belongs to
     */
    public Person(String name, Role role, String department) {
        this.name = name;
        this.role = role;
        this.department = department;
    }

    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return name + " (" + role + ", " + department + ")";
    }
}
