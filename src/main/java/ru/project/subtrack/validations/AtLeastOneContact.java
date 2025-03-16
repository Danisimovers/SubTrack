package ru.project.subtrack.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneContactValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneContact {
    String message() default "Необходимо указать хотя бы email или номер телефона";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
