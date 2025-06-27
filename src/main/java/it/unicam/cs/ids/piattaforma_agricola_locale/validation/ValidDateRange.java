/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure that end date is after start date.
 * Used to validate date ranges where the end date must be after the start date.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface ValidDateRange {
    
    /**
     * The name of the start date field.
     */
    String start();
    
    /**
     * The name of the end date field.
     */
    String end();
    
    /**
     * Default validation error message.
     */
    String message() default "Data di fine deve essere successiva alla data di inizio";
    
    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for clients.
     */
    Class<? extends Payload>[] payload() default {};
}