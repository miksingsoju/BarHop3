package app.components;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

/**
 * Salted password hashing with PBKDF2-HMAC-SHA256 (built into the JDK, so no extra jar is needed
 * in SpringBootDependencies). Stored as "pbkdf2$iterations$salt$hash".
 */
@Component
public class PasswordHasher {

	private static final int ITERATIONS = 65536;
	private static final int KEY_BITS = 256;
	private final SecureRandom random = new SecureRandom();

	public String hash(String rawPassword) {
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		byte[] hash = pbkdf2(rawPassword, salt, ITERATIONS);
		Base64.Encoder b64 = Base64.getEncoder();
		return "pbkdf2$" + ITERATIONS + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
	}

	public boolean matches(String rawPassword, String stored) {
		if (rawPassword == null || stored == null) return false;
		String[] parts = stored.split("\\$");
		if (parts.length != 4 || !parts[0].equals("pbkdf2")) return false;
		int iterations = Integer.parseInt(parts[1]);
		byte[] salt = Base64.getDecoder().decode(parts[2]);
		byte[] expected = Base64.getDecoder().decode(parts[3]);
		return MessageDigest.isEqual(expected, pbkdf2(rawPassword, salt, iterations));
	}

	private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("PBKDF2 not available", e);
		}
	}
}
