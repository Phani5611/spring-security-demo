package com.security.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    public static final long JWT_EXPIRATION=1000 * 60 *30;

    @Value("${app.jwt.secret}")
    private String secretKey;

    public String generateToken(String username) {
      Map<String,Object> claims = new HashMap<>();

      return Jwts.builder()
              .claims(claims)
              .subject(username)
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
              .signWith(getKey(), SignatureAlgorithm.HS256)
              .compact();
    }

    // Digitally Signed Key Generation
    private Key getKey(){
      byte[] keyBytes = Decoders.BASE64.decode(secretKey);
      return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        // The standard claim for the user identifier is 'Subject' (sub)
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token.
     * @param token The JWT string.
     * @param claimsResolver A function to map the Claims object to the desired value (e.g., Claims::getExpiration).
     * @param <T> The expected type of the claim.
     * @return The resolved claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and verifies the token, returning all claims.
     * This method handles the actual security check using the secret key.
     * @param token The JWT string.
     * @return The Claims (payload) object.
     */
    private Claims extractAllClaims(String token) {
        // Jwts.parser() is now built via Jwts.parserBuilder() in modern versions (0.12.x+)
        return Jwts.parser()
                .verifyWith((SecretKey) getKey()) // Uses the initialized SecretKey
                .build()
                .parseSignedClaims(token) // Parses the claims and VERIFIES the signature
                .getPayload(); // Returns the Claims object
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the token's expiration date (exp claim) has passed.
     * @param token The JWT string.
     * @return true if expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
