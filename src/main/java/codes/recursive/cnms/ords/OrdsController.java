package codes.recursive.cnms.ords;

import codes.recursive.cnms.ords.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.micronaut.validation.validator.Validator;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@Controller("/user")
public class OrdsController {
    private final OrdsClient ordsClient;
    private final String baseUri;
    @Inject
    Validator validator;

    public OrdsController(OrdsClient ordsClient, EmbeddedServer embeddedServer) {
        this.ordsClient = ordsClient;
        this.baseUri = embeddedServer.getURI() + "/user";
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
    public HttpResponse<User> getUser(String id) {
        User user = ordsClient.getUser(id);
        if( user != null ) {
            return HttpResponse.ok(user);
        }
        else {
            return HttpResponse.notFound();
        }
    }

    @Get("/users")
    public HttpResponse<Map<String, Object>> listUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map users = ordsClient.listUsers();
        List<User> items = objectMapper.convertValue(users.get("items"), new TypeReference<List<User>>() {});
        Map<String, Object> response = new HashMap<>();
        response.put("users", items);
        return HttpResponse.ok(
                response
        );
    }

    @Get("/users/{offset}/{max}")
    public HttpResponse<Map<String, Object>> listUsersPaginated(int offset, int max) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map users = ordsClient.listUsers(offset, max);
        List<User> items = objectMapper.convertValue(users.get("items"), new TypeReference<List<User>>() {});
        Map<String, Object> response = new HashMap<>();
        response.put("users", items);
        response.put("count", users.get("count"));
        response.put("offset", users.get("offset"));
        response.put("hasMore", users.get("hasMore"));
        response.put("limit", users.get("limit"));
        return HttpResponse.ok(
                response
        );
    }

    @Get("/username/{username}")
    public HttpResponse<User> getUserByUsername(String username) {
        User user = ordsClient.getByUsername(username);
        if( user != null ) {
            return HttpResponse.ok(user);
        }
        else {
            return HttpResponse.notFound();
        }
    }

    @Post("/")
    public HttpResponse saveUser(@Body @Valid User user) throws URISyntaxException {
        User savedUser = ordsClient.saveUser(user);
        return HttpResponse.created(
                new URI(baseUri + "/" + savedUser.getId())
        );
    }

    @Put("/")
    public HttpResponse updateUser(@Body @Valid User user) {
        ordsClient.updateUser(user, user.getId());
        return HttpResponse.ok();
    }

    @Delete("/{id}")
    public HttpResponse deleteUser(String id) {
        Map deleteUser = ordsClient.deleteUser(id);
        return HttpResponse.noContent();
    }
}