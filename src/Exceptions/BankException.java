package Exceptions;

/*
 * BankException.java
 * Base class for all domain-specific exceptions in this system.
 * Extends RuntimeException so callers are not forced to declare or catch it,
 * while still allowing targeted handling when needed.
 */
public class BankException extends RuntimeException {
    /** Wraps the given error description in a runtime exception. */
    public BankException(String message) {
        super(message);
    }
}
