package codes.recursive.cnms.ords;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import org.reactivestreams.Publisher;

import java.util.Base64;
import java.util.Map;

@Filter("/ords/usersvc/users/**")
@Requires(property = "codes.recursive.cnms.ords.client-id")
@Requires(property = "codes.recursive.cnms.ords.client-secret")
public class OrdsClientFilter implements HttpClientFilter {
    private final OrdsConfiguration ordsConfiguration;
    private final OrdsClient ordsClient;

    private long lastAuthAt = 0;
    private String currentToken = "";
    private final long timeOut = 60 * 60 * 1000;

    OrdsClientFilter(OrdsConfiguration ordsConfiguration, OrdsClient ordsClient) {
        this.ordsConfiguration = ordsConfiguration;
        this.ordsClient = ordsClient;
    }

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {

        if( lastAuthAt == 0 || lastAuthAt > 0 && System.currentTimeMillis() - lastAuthAt > timeOut ) {
            String authString =  ordsConfiguration.clientId + ":" + ordsConfiguration.clientSecret;
            String authEncoded = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            Map tokenBody = ordsClient.getToken("grant_type=client_credentials", authEncoded);
            currentToken = tokenBody.get("access_token").toString();
            System.out.println("Token: " + currentToken);
            lastAuthAt = System.currentTimeMillis();
        }

        return chain.proceed(request.bearerAuth(currentToken));
    }
}
