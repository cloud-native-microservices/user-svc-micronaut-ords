package codes.recursive.cnms.ords;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("codes.recursive.cnms.ords")
public class OrdsConfiguration {
    String clientId;
    String clientSecret;
    String baseUrl;

    public Map<String, Object> toMap() {
        Map<String, Object> props = new HashMap<>();
        props.put("clientId", clientId);
        props.put("clientSecret", clientSecret);
        props.put("baseUrl", baseUrl);
        return props;
    }

}
