# 🏦 Bank Account Simulation

> A fully object-oriented Java banking system — where account types form a deep **inheritance hierarchy**, transactions enforce **encapsulation**, and every error case triggers a **custom exception**.

**OOP Course Project — Java & JavaFX**

---

## 📋 Table of Contents

- [Overview](#overview)
- [OOP Concepts Used](#oop-concepts-used)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Team](#team)
- [Setup & Installation](#setup--installation)
- [How to Run](#how-to-run)
- [Key OOP Patterns](#key-oop-patterns)
- [Development Timeline](#development-timeline)
- [References](#references)

---

## Overview

The Bank Account Simulation models a real-world banking system built entirely in Java. Customers can open multiple account types, perform deposits, withdrawals, and transfers, and view their full transaction history. Admins manage customers and accounts system-wide. The UI is built with JavaFX — no console, no ugliness.

The project demonstrates all four OOP pillars required by the rubric:

- **Abstraction** — an abstract `Account` class and three interfaces define contracts without exposing internal logic
- **Encapsulation** — all account data (balance, ID, owner) is private and accessed only through validated methods
- **Inheritance** — four concrete account types extend `Account`, each overriding behavior for their domain
- **Exception Handling** — five custom exceptions cover every domain error: insufficient funds, invalid input, unknown accounts, overdrafts, and credit limit violations

---

## OOP Concepts Used

| Concept | Implementation | Where |
|---|---|---|
| Abstraction | `abstract class Account` + 3 interfaces | `model/`, `interfaces/` |
| Encapsulation | `private` fields, public getters only | All model classes |
| Inheritance | `SavingsAccount`, `CheckingAccount`, `LoanAccount`, `CreditAccount` extend `Account` | `model/` |
| Polymorphism | `Transactable` used as common type across all account operations | `service/TransactionService.java` |
| Exception Handling | 5 custom exceptions thrown in service layer, caught in UI | `exceptions/`, `service/` |
| Design Pattern | Observer — `Notifiable` interface triggers alerts on low balance / overdraft | `interfaces/Notifiable.java` |

---

## Tech Stack

| Component | Tool | Purpose |
|---|---|---|
| Language | Java 17+ | Core OOP implementation |
| UI Framework | JavaFX 21 | Login screen, dashboards, forms, transaction tables |
| IDE | IntelliJ IDEA | Built-in JavaFX support, easy project setup |
| Build Tool | Maven or plain IntelliJ project | Manage JavaFX SDK dependency |
| Data Storage | Java Collections (`HashMap`, `ArrayList`) | In-memory store — no database needed |
| Version Control | Git + GitHub | Collaboration across 3 members |

---

## Project Structure

```
bank_simulation/
├── src/
│   ├── model/
│   │   ├── Account.java                # Abstract base class — Luis
│   │   ├── SavingsAccount.java         # Extends Account — Luis
│   │   ├── CheckingAccount.java        # Extends Account — Luis
│   │   ├── LoanAccount.java            # Extends Account — Luis
│   │   ├── CreditAccount.java          # Extends Account — Luis
│   │   ├── Customer.java               # Customer entity — Luis
│   │   ├── Admin.java                  # Admin entity — Luis
│   │   └── Transaction.java            # Immutable transaction record — Luis
│   │
│   ├── interfaces/
│   │   ├── Transactable.java           # deposit(), withdraw() — Luis
│   │   ├── InterestBearing.java        # calculateInterest(), applyInterest() — Luis
│   │   └── Notifiable.java             # sendAlert(String msg) — Luis
│   │
│   ├── exceptions/
│   │   ├── InsufficientFundsException.java      # Luis
│   │   ├── InvalidAmountException.java          # Luis
│   │   ├── AccountNotFoundException.java        # Luis
│   │   ├── OverdraftException.java              # Luis
│   │   └── CreditLimitExceededException.java    # Luis
│   │
│   ├── service/
│   │   ├── BankService.java            # Account/customer registry — Person B
│   │   ├── TransactionService.java     # deposit, withdraw, transfer logic — Person B
│   │   └── InterestService.java        # Monthly interest application — Person B
│   │
│   ├── ui/
│   │   ├── LoginScreen.java            # JavaFX login (customer vs admin) — Person C
│   │   ├── CustomerDashboard.java      # Account cards, balance view — Person C
│   │   ├── AdminDashboard.java         # All customers, account management — Person C
│   │   ├── TransactionForm.java        # Deposit / withdraw / transfer form — Person C
│   │   └── TransactionHistoryView.java # Sortable transaction table — Person C
│   │
│   └── Main.java                       # Entry point — launches JavaFX app
```

---

## Team

| Member | Role | Owns |
|---|---|---|
| Luis | Core OOP Model | `model/`, `interfaces/`, `exceptions/` |
| Abenezer | Business Logic | `service/BankService.java`, `service/TransactionService.java`, `service/InterestService.java`, `Main.java` |
| Person C | JavaFX UI | `ui/LoginScreen.java`, `ui/CustomerDashboard.java`, `ui/AdminDashboard.java`, `ui/TransactionForm.java`, `ui/TransactionHistoryView.java` |

### Shared Data Contract

> **Agree on this before writing any code.** All three workstreams depend on these definitions. Do not start module code until all three members have signed off on the enums and service API below.

```java
// Shared enums — Luis writes these first
public enum AccountType {
    SAVINGS, CHECKING, LOAN, CREDIT
}

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST_APPLIED
}

// Account ID format:  "ACC-XXXX"  (e.g. "ACC-1001")
// Customer ID format: "CUS-XXXX"  (e.g. "CUS-0042")

// Service API that Person C calls (Person B implements)
void deposit(String accountId, double amount)
    throws AccountNotFoundException, InvalidAmountException;

void withdraw(String accountId, double amount)
    throws AccountNotFoundException, InvalidAmountException,
           InsufficientFundsException, OverdraftException;

void transfer(String fromId, String toId, double amount)
    throws AccountNotFoundException, InsufficientFundsException,
           InvalidAmountException;

List<Transaction> getHistory(String accountId)
    throws AccountNotFoundException;
```

---

## Setup & Installation

### Prerequisites

- Java 17+
- JavaFX SDK 21 — download from [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx)
- IntelliJ IDEA (recommended)

### JavaFX Configuration (IntelliJ)

1. Go to **File → Project Structure → Libraries → + → Java**
2. Select the `lib/` folder inside your JavaFX SDK download
3. In your **Run Configuration**, add the following VM options:

```
--module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
```

---

## How to Run

### From IntelliJ

Press the green **Run** button on `Main.java`.

### From terminal

```bash
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls -d out src/**/*.java
java  --module-path $JAVAFX_HOME/lib --add-modules javafx.controls -cp out Main
```

### Test business logic without the UI (Person B)

```java
public static void main(String[] args) {
    BankService bank = new BankService();
    Customer c = bank.createCustomer("Alice", "pass123");
    Account acc = bank.openAccount(c.getId(), AccountType.SAVINGS);

    bank.deposit(acc.getId(), 1000.0);
    bank.withdraw(acc.getId(), 200.0);

    // Should throw InsufficientFundsException:
    try {
        bank.withdraw(acc.getId(), 9999.0);
    } catch (InsufficientFundsException e) {
        System.out.println("Caught expected: " + e.getMessage());
    }
}
```

---

## Key OOP Patterns

### Inheritance Hierarchy

```
<<abstract>> Account
├── SavingsAccount      implements InterestBearing
├── CheckingAccount     (overdraft limit)
├── LoanAccount         (remaining debt balance)
└── CreditAccount       implements InterestBearing
```

### Interface Contracts

```java
// Every account that can receive/send money
public interface Transactable {
    void deposit(double amount) throws InvalidAmountException;
    void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException;
}

// SavingsAccount and CreditAccount implement this
public interface InterestBearing {
    double calculateInterest();
    void applyInterest();
}

// Triggered when thresholds are crossed (low balance, overdraft warning)
public interface Notifiable {
    void sendAlert(String message);
}
```

### Exception Hierarchy

```
RuntimeException
└── BankException                       (base — optional)
    ├── InsufficientFundsException      withdraw > balance
    ├── InvalidAmountException          amount <= 0
    ├── AccountNotFoundException        account ID does not exist
    ├── OverdraftException              exceed overdraft limit on CheckingAccount
    └── CreditLimitExceededException    charge exceeds credit limit
```

### Observer Pattern — Notifiable Alerts

```java
public class BankService {
    public void processWithdraw(String accountId, double amount) {
        Account acc = findAccount(accountId);
        acc.withdraw(amount);

        // Notify if balance drops below threshold
        if (acc.getBalance() < LOW_BALANCE_THRESHOLD && acc instanceof Notifiable n) {
            n.sendAlert("Warning: balance below $100 on account " + accountId);
        }
    }
}
```

> **Demo talking point:** Show a `CheckingAccount` withdrawal that exceeds the balance — the UI catches the `OverdraftException` and displays a clean error dialog, not a stack trace. This demonstrates exception handling and encapsulation in one live action.

---

## Development Timeline

| Dates | Milestone | Who |
|---|---|---|
| **Apr 6–7** | Agree on shared data contract. Luis starts `Account` and interface skeletons. | All three |
| **Apr 8–9** | Luis completes all model classes and exceptions. Person B + C draft UML for interim report. | A (code) · B+C (UML) |
| **Apr 10** | **Interim report due.** Submit UML class diagram + role assignments. | All three |
| **Apr 11–14** | Person B: `BankService` + `TransactionService`. Person C: static JavaFX screens with mock data. | B · C (parallel) |
| **Apr 15–18** | Integration sprint — Person C swaps mock data for real service calls. All exceptions wired to UI. | B + C integrate |
| **Apr 19–22** | Full feature pass: all account types, all forms, admin dashboard, transaction history. Begin report. | All three |
| **Apr 23–25** | Bug fixing, edge case testing, complete written report. | All three |
| **Apr 26** | **Project files + report due.** | All three |
| **Apr 27** | **Presentation.** Show UML, explain inheritance + abstraction, run program live. | All three |

---

## References

- [JavaFX Documentation](https://openjfx.io/openjfx-docs/)
- [JavaFX SDK Downloads](https://gluonhq.com/products/javafx/)
- [Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)
- Gamma et al. — *Design Patterns: Elements of Reusable Object-Oriented Software*
- Course rubric and project list — provided by instructor

---

<div align="center">
  <sub>OOP Course Project · Bank Account Simulation · Java & JavaFX · 3-Person Team</sub>
</div>
