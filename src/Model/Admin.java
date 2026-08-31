package Model;

/*
 * Admin.java
 * Represents a bank administrator. Simpler than Customer — just an ID and name.
 * Admin accounts are used to gate privileged actions such as applying interest
 * and viewing all customer data in the AdminDashboard.
 */
public class Admin {
    private final String adminId;
    private String name;

    /** Creates an admin with a unique ID and display name. */
    public Admin(String adminId, String name) {
        this.adminId = adminId;
        this.name = name;
    }

    public String getAdminId() { return adminId; }
    public String getName()    { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Admin[" + adminId + "] " + name;
    }
}
