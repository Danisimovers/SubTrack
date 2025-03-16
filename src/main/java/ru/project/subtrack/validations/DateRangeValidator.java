package ru.project.subtrack.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.project.subtrack.dto.SubscriptionDTO;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, SubscriptionDTO> {

    @Override
    public boolean isValid(SubscriptionDTO dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return true; // Это проверяют другие аннотации @NotNull
        }
        return dto.getEndDate().isAfter(dto.getStartDate());
    }
}
