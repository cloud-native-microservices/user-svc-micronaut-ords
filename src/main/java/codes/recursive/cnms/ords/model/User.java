package codes.recursive.cnms.ords.model;

import codes.recursive.cnms.ords.validator.UniqueUsername;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Map;

@Introspected
@UniqueUsername(message = "Username must exist and be unique")
public class User {

    @JsonAlias(value = {"id"})
    @JsonProperty
    private String id;

    @NotNull
    @Size(max = 50)
    @JsonAlias(value = {"first_name", "firstName"})
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(max = 50)
    @JsonAlias(value = {"last_name", "lastName"})
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Size(max = 50)
    @JsonAlias(value = {"username"})
    @JsonProperty
    private String username;

    @JsonAlias(value = {"created_on", "createdOn"})
    @JsonProperty("created_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date createdOn = new Date();

    /* comes back from ORDS, we'll ignore it */
    @JsonIgnore
    private Map links;

    public User() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Map getLinks() {
        return links;
    }

    public void setLinks(Map links) {
        this.links = links;
    }

}