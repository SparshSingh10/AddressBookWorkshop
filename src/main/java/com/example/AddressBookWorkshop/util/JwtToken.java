package com.example.AddressBookWorkshop.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtToken {
    private static final String TOKEN_SECRET = "Lock";
    private static final long EXPIRATION_TIME = 10 * 60 * 1000; // 10 minutes in milliseconds

    public String createToken(String email) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            return JWT.create()
                    .withClaim("email", email)
                    .withClaim("uuid", UUID.randomUUID().toString())  // ✅ Ensures uniqueness
                    .withClaim("iat", new Date().getTime()) // ✅ Issued at timestamp
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // ✅ Expiration
                    .sign(algorithm);

        } catch (JWTCreationException | IllegalArgumentException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public String decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // ✅ Ensure token is not expired
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new RuntimeException("Token has expired.");
            }

            return decodedJWT.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    public String getEmailFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("email").asString(); // Extracts the "email" claim from the token
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

}
