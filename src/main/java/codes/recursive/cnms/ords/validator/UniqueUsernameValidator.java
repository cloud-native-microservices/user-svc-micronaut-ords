package codes.recursive.cnms.ords.validator;

import codes.recursive.cnms.ords.OrdsClient;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Inject
    private OrdsClient ordsClient;

    @Override
    public boolean isValid(@Nullable String value, @Nonnull AnnotationValue<UniqueUsername> annotationMetadata, @Nonnull ConstraintValidatorContext context) {
        Map userResult = ordsClient.getByUsername(value);
        return !userResult.containsKey("count") || Integer.parseInt( userResult.get("count").toString() ) == 0;
    }
}
