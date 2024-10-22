package ru.practicum.shareit.annotation;

import jakarta.validation.Constraint;
import ru.practicum.shareit.validator.IdValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = IdValidator.class)
public @interface MinimalId {
    String message() default "ID не может быть меньше {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "1";
}
