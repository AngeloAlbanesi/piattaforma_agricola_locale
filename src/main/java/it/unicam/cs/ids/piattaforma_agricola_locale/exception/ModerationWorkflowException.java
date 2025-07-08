/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Exception thrown when there's an error in the moderation workflow.
 */
public class ModerationWorkflowException extends RuntimeException {
    
    public ModerationWorkflowException(String message) {
        super(message);
    }
    
    public ModerationWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}