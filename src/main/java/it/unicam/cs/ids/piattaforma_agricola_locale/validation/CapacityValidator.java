/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

/**
 * Validator implementation for ValidCapacity annotation.
 * Validates that booked amount does not exceed capacity using reflection to access fields.
 */
public class CapacityValidator implements ConstraintValidator<ValidCapacity, Object> {

    private String capacityFieldName;
    private String bookedFieldName;

    @Override
    public void initialize(ValidCapacity constraintAnnotation) {
        this.capacityFieldName = constraintAnnotation.capacity();
        this.bookedFieldName = constraintAnnotation.booked();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        try {
            Number capacity = getFieldValue(value, capacityFieldName, Number.class);
            Number booked = getFieldValue(value, bookedFieldName, Number.class);

            // If either value is null, validation passes (let @NotNull handle it)
            if (capacity == null || booked == null) {
                return true;
            }

            // Booked must not exceed capacity
            return booked.intValue() <= capacity.intValue();

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