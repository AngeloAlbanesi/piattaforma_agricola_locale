/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure that booked items do not exceed capacity.
 * Used to validate capacity constraints where booked amount must not exceed maximum capacity.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CapacityValidator.class)
@Documented
public @interface ValidCapacity {
    
    /**
     * The name of the capacity field.
     */
    String capacity();
    
    /**
     * The name of the booked field.
     */
    String booked();
    
    /**
     * Default validation error message.
     */
    String message() default "Posti prenotati non possono superare la capienza massima";
    
    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for clients.
     */
    Class<? extends Payload>[] payload() default {};
}