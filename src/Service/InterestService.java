package Service;

import Interfaces.InterestBearing;
import Model.Account;

/*
 * InterestService.java
 * Applies monthly interest to every InterestBearing account in the system.
 * SavingsAccount and CreditAccount implement InterestBearing — all others are skipped.
 * Call applyMonthlyInterest() once per billing cycle.
 */
public class InterestService {
    private final BankService bank;

    /** Creates the service using the shared BankService as the source of all accounts. */
    public InterestService(BankService bank) {
        this.bank = bank;
    }

    /** Loops all accounts and calls applyInterest() on every InterestBearing one. */
    public void applyMonthlyInterest() {
        int count = 0;
        for (Account account : bank.getAllAccounts()) {
            if (account instanceof InterestBearing ib) {
                ib.applyInterest();
                count++;
            }
        }
        System.out.println("[InterestService] Applied interest to " + count + " account(s).");
    }

    /** Loops all accounts and calls removeInterest() on every InterestBearing one. */
    public void removeMonthlyInterest() {
        int count = 0;
        for (Account account : bank.getAllAccounts()) {
            if (account instanceof InterestBearing ib) {
                ib.removeInterest();
                count++;
            }
        }
        System.out.println("[InterestService] Removed interest from " + count + " account(s).");
    }
}
