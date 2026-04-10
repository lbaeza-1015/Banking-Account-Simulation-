package Model;

/*
 * Admin.java
 * Represents a bank administrator. Simpler than Customer — just an ID and name.
 * Person B and C will use this to gate admin-level actions in the service and UI layers.
 */
public class Admin {
    private final String adminId;
    private String name;

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
