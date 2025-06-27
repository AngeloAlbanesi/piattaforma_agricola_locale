/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Validator implementation for ValidDateRange annotation.
 * Validates that end date is after start date using reflection to access fields.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startFieldName = constraintAnnotation.start();
        this.endFieldName = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        try {
            Date startDate = getFieldValue(value, startFieldName, Date.class);
            Date endDate = getFieldValue(value, endFieldName, Date.class);

            // If either date is null, validation passes (let @NotNull handle it)
            if (startDate == null || endDate == null) {
                return true;
            }

            // End date must be after start date
            return endDate.after(startDate);

        } catch (Exception e) {
            // If reflection fails, validation fails
            return false;
        }
    }

    /**
     * Uses reflection to get field value from object.
     * 
     * @param object the object to get the field from
     * @param fieldName the name of the field
     * @param expectedType the expected type of the field
     * @return the field value
     * @throws Exception if field access fails
     */
    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object object, String fieldName, Class<T> expectedType) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(object);
        
        if (fieldValue == null || expectedType.isInstance(fieldValue)) {
            return (T) fieldValue;
        }
        
        throw new IllegalArgumentException("Field " + fieldName + " is not of expected type " + expectedType.getSimpleName());
    }
}