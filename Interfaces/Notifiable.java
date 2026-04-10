package Interfaces;

/*
 * Notifiable.java
 * A contract for accounts that can send alerts (low balance, overdraft, payment due).
 * Any account implementing this must define sendAlert(String message).
 */
public interface Notifiable {
    void sendAlert(String message);
}
