package codes.recursive.cnms.ords;

import codes.recursive.cnms.ords.model.User;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;

import java.util.Map;

@Client(
        value = "${codes.recursive.cnms.ords.base-url}"
)
public abstract class OrdsClient {

    @Get("/ords/usersvc/users/")
    public abstract Map listUsers();

    @Get("/ords/usersvc/users/{id}")
    public abstract User getUser(@QueryValue String id);

    @Get("/ords/usersvc/users/user/{username}")
    public abstract Map getByUsername(@QueryValue String username);

    @Get("/ords/usersvc/users/?offset={offset}&limit={limit}")
    public abstract Map listUsers(@QueryValue int offset, @QueryValue int limit);

    @Post("/ords/usersvc/users/")
    public abstract User saveUser(@Body User user);

    @Put("/ords/usersvc/users/{id}")
    public abstract User updateUser(@Body User user, @QueryValue String id);

    @Delete("/ords/usersvc/users/{id}")
    public abstract Map deleteUser(@QueryValue String id);

    @Post("/ords/usersvc/oauth/token")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public abstract Map getToken(@Body String body, @Header("Authorization") String auth);
}
