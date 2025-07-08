/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Exception thrown when a user attempts to access or modify a resource they don't own.
 */
public class ResourceOwnershipException extends RuntimeException {
    
    public ResourceOwnershipException(String message) {
        super(message);
    }
    
    public ResourceOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}