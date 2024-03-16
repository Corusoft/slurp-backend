package dev.corusoft.slurp.places.application.criteria;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
public class PlacesCriteriaValidator {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validate(PlacesCriteria criteria) throws ConstraintViolationException {
        Set<ConstraintViolation<PlacesCriteria>> violations = validator.validate(criteria);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
