package codes.recursive.cnms.ords.validator;

import codes.recursive.cnms.ords.UserClient;
import codes.recursive.cnms.ords.model.User;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Optional;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, User> {

    private UserClient userClient;

    @Inject
    public UniqueUsernameValidator(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public boolean isValid(@Nullable User user, @Nonnull AnnotationValue<UniqueUsername> annotationMetadata, @Nonnull ConstraintValidatorContext context) {
        if( user.getUsername() == null ) return false;
        Optional<HttpRequest<Object>> request = ServerRequestContext.currentRequest();
        Boolean isUpdate = request.isPresent() && request.get().getMethod().name().equals("PUT");
        User retrievedUser = userClient.getByUsername(user.getUsername()).blockingGet();

        // if it's a new user, check if we have an existing user by this username
        if( !isUpdate ) {
            return retrievedUser == null;
        }

        // if no matches or the retrieved user by this username is the user being validated, it's valid
        return retrievedUser == null || retrievedUser.getId().equals(user.getId());
    }
}
