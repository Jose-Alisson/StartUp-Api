package br.start.up.jwt;

import br.start.up.detail.UserDetail;
import br.start.up.services.AccountService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.temporal.TemporalAmount;
import java.util.Date;

@Service
public class JwtService {

    @Value("${secret.jwt}")
    private String secret;

    public String generateJwt(UserDetail detail) {
        return JWT.create()
                .withSubject(detail.getUsername())
                .withClaim("id", detail.getAccount().getId())
                .withClaim("authorities", detail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withClaim("role", detail.getAccount().getRole())
                .withIssuer("startup-guide")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date().toInstant().plusMillis(1000 * 60 * 60 * 10))
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verify(String token) {
        var verify = JWT.require(Algorithm
                        .HMAC256(secret))
                .withIssuer("startup-guide")
                .build();
        return verify.verify(token);
    }
}
