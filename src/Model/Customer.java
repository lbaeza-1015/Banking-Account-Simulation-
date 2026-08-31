package Model;

/*
 * Customer.java
 * Represents a bank customer. Holds their ID, name, email, and a list of their accounts.
 * No financial logic lives here — it is purely a data holder.
 * Person B's service layer creates and manages Customer objects.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private final String customerId;
    private String name;
    private String email;
    private final List<Account> accounts;

    /** Creates a customer with the given ID, display name, and email address. */
    public Customer(String customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    /** Links an account to this customer so it appears in their account list. */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    public String getCustomerId() { return customerId; }
    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public List<Account> getAccounts() { return accounts; }

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Customer[" + customerId + "] " + name + " <" + email + ">";
    }
}
