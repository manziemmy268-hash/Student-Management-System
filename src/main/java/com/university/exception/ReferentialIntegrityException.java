package com.university.exception;

public class ReferentialIntegrityException extends RuntimeException {

    public ReferentialIntegrityException(String message) {
        super(message);
    }

    public ReferentialIntegrityException(String entity, int id, String dependency) {
        super(String.format("Cannot delete %s (ID: %d) because it is referenced by %s", entity, id, dependency));
    }
}
