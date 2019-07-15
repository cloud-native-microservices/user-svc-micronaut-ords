package codes.recursive.cnms.ords;

import codes.recursive.cnms.ords.model.PaginatedUserResult;
import codes.recursive.cnms.ords.model.User;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Validated
@Controller("/user")
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient, EmbeddedServer embeddedServer) {
        this.userClient = userClient;
    }

    @Get("/")
    public HttpResponse<Map> get() {
        Map<String, Object> map = new HashMap<>();
        map.put("OK", true);
        return HttpResponse.ok(
                map
        );
    }

    @Get("/{id}")
    public Maybe<User> getUser(String id) {
        return userClient.getUser(id);
    }

    @Get("/users")
    public Single<PaginatedUserResult> listUsers() {
        return userClient.listUsers();
    }

    @Get("/users/{offset}/{max}")
    public Single<PaginatedUserResult> listUsersPaginated(int offset, int max) {
        return userClient.listUsers(offset, max);
    }

    @Get("/username/{username}")
    public Maybe<User> getUserByUsername(String username) {
        return userClient.getByUsername(username);
    }

    @Post("/")
    @Status(HttpStatus.CREATED)
    public Single<User> saveUser(@Body @Valid User user) {
        return userClient.saveUser(user);
    }

    @Put("/")
    public Single<User> updateUser(@Body @Valid User user) {
        return userClient.updateUser(user, user.getId());
    }

    @Delete("/{id}")
    public Single<MutableHttpResponse> deleteUser(String id) {
        return userClient.deleteUser(id).flatMap(map -> {
            if (map.get("rowsDeleted").toString().equals("0")) {
                return Single.just(HttpResponse.notFound());
            } else {
                return Single.just(HttpResponse.noContent());
            }
        });
    }

}