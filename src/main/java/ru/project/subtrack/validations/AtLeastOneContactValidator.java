package ru.project.subtrack.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.project.subtrack.models.User;

public class AtLeastOneContactValidator implements ConstraintValidator<AtLeastOneContact, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return (user.getEmail() != null && !user.getEmail().isEmpty()) ||
                (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty());
    }
}
