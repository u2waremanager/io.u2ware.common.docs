package io.u2ware.common.docs;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class RestSecurityRequestPostProcessorBuilder {


    public static RequestPostProcessor auth(String bearer){
        return (request)->{
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+bearer);
            return request;
        };
    }

    public static RequestPostProcessor auth(Jwt jwt) {
        return (request)->{
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+jwt.getTokenValue());
            return request;
        };
    }

    public static RequestPostProcessor security(String username) {
        return SecurityMockMvcRequestPostProcessors.user(username);
    }


    public static RequestPostProcessor security(Jwt jwt) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt);
    }


    public static Jwt createJwt(String username){

        Map<String,Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("email", username);
        claims.put("name", username);

        Jwt jwt = new Jwt(
                username,
                Instant.now(),
                Instant.now().plusSeconds(30),
                Map.of("alg", "none"),
                claims
        );
        return jwt;
    }

}
