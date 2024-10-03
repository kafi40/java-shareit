package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.exception.IdNotCurrentException;

public class IdValidator implements ConstraintValidator<MinimalId, Long> {
    private Long minimalId;

    @Override
    public void initialize(MinimalId constraintAnnotation) {
        minimalId = Long.valueOf(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id != null && id < minimalId) {
            throw new IdNotCurrentException("ID не может быть меньше 1");
        } else {
            return true;
        }
    }
}
