package codes.recursive.cnms.ords.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class PaginatedUserResult {
    private int offset;
    private int count;
    private Boolean hasMore;
    private int limit;
    @JsonProperty("users")
    private List<User> users;

    public PaginatedUserResult() {}

    @JsonCreator
    public PaginatedUserResult(@JsonProperty("offset") int offset, @JsonProperty("count") int count, @JsonProperty("hasMore") Boolean hasMore, @JsonProperty("limit") int limit, @JsonProperty("items") List<User> users) {
        this.setOffset(offset);
        this.setCount(count);
        this.setHasMore(hasMore);
        this.setLimit(limit);
        this.setUsers(users);
    }
    @JsonProperty("users")
    public List<User> getUsers() {
        return users;
    }

    @JsonProperty("items")
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
