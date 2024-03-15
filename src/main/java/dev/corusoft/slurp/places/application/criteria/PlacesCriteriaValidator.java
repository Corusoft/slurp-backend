package dev.corusoft.slurp.places.application.criteria;

import jakarta.validation.*;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@NoArgsConstructor
public class PlacesCriteriaValidator {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validate(PlacesCriteria criteria) throws ConstraintViolationException {
        Set<ConstraintViolation<PlacesCriteria>> violations = validator.validate(criteria);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
