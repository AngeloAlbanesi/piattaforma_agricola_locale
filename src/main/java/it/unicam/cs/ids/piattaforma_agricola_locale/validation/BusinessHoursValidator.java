/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;
import java.util.Date;

/**
 * Validator implementation for ValidBusinessHours annotation.
 * Validates that date/time falls within business hours .
 */
public class BusinessHoursValidator implements ConstraintValidator<ValidBusinessHours, Date> {

    private static final int BUSINESS_START_HOUR = 0;
    private static final int BUSINESS_END_HOUR = 24;

    @Override
    public void initialize(ValidBusinessHours constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        // Check if hour is within business hours 
        return hour >= BUSINESS_START_HOUR && hour < BUSINESS_END_HOUR;
    }
}