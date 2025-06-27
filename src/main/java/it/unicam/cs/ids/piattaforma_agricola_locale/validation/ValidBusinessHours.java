/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure that time is during business hours.
 * Used to validate that date/time fields fall within acceptable business hours (8:00 - 20:00).
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BusinessHoursValidator.class)
@Documented
public @interface ValidBusinessHours {
    
    /**
     * Default validation error message.
     */
    String message() default "L'orario deve essere durante le ore lavorative (8:00 - 20:00)";
    
    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for clients.
     */
    Class<? extends Payload>[] payload() default {};
}