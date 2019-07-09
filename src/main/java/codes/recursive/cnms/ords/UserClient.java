package codes.recursive.cnms.ords;

import codes.recursive.cnms.ords.model.PaginatedUserResult;
import codes.recursive.cnms.ords.model.User;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.Map;

@Client(
        value = "${codes.recursive.cnms.ords.base-url}"
)
public abstract class UserClient {

    @Get("/ords/usersvc/users/")
    public abstract Single<PaginatedUserResult> listUsers();

    @Get("/ords/usersvc/users/?offset={offset}&limit={limit}")
    public abstract Single<PaginatedUserResult> listUsers(@QueryValue int offset, @QueryValue int limit);

    @Get("/ords/usersvc/users/{id}")
    public abstract Maybe<User> getUser(@QueryValue String id);

    @Get("/ords/usersvc/users/user/{username}")
    public abstract Maybe<User> getByUsername(@QueryValue String username);

    @Post("/ords/usersvc/users/")
    public abstract Single<User> saveUser(@Body User user);

    @Put("/ords/usersvc/users/{id}")
    public abstract Single<User> updateUser(@Body User user, @QueryValue String id);

    @Delete("/ords/usersvc/users/{id}")
    public abstract Single<Map> deleteUser(@QueryValue String id);

    @Post("/ords/usersvc/oauth/token")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public abstract Map getToken(@Body String body, @Header("Authorization") String auth);
}
