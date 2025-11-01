import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

/**
 * Utility to generate a secure 256-bit symmetric key for HS256.
 * Run this ONCE to get a static key for your production environment.
 */
public class SecretKeyGenerator {
    public static void main(String[] args) {
        // Generates a cryptographically secure, 256-bit symmetric key for HS256
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

        // Output the Base64-encoded string
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("------------------------------------------------------------------------");
        System.out.println("           🔒 PRODUCTION-GRADE JWT SECRET KEY GENERATED 🔒");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Key Algorithm: HS256 (Minimum 256 bits)");
        System.out.println("Base64 Key: " + base64Key);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("ACTION: Copy the Base64 Key and store it in your Environment Variables or Secret Vault.");
    }
}