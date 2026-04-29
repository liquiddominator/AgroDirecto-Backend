package com.agrodirecto.security.jwt;

import com.agrodirecto.user.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String TOKEN_TYPE = "access";

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long accessTokenMinutes;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-minutes}") long accessTokenMinutes
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public GeneratedAccessToken generateAccessToken(User user, List<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(accessTokenMinutes * 60);

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("roles", roles);
        payload.put("iat", issuedAt.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());
        payload.put("typ", TOKEN_TYPE);

        String encodedHeader = base64Url(toJson(header));
        String encodedPayload = base64Url(toJson(payload));
        String signingInput = encodedHeader + "." + encodedPayload;
        String signature = sign(signingInput);

        return new GeneratedAccessToken(signingInput + "." + signature, expiresAt);
    }

    public JwtClaims parseAccessToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT mal formado");
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.US_ASCII),
                parts[2].getBytes(StandardCharsets.US_ASCII)
        )) {
            throw new IllegalArgumentException("Firma JWT invalida");
        }

        Map<String, Object> payload = fromJson(new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        ));

        if (!TOKEN_TYPE.equals(payload.get("typ"))) {
            throw new IllegalArgumentException("Tipo de token invalido");
        }

        Instant expiresAt = Instant.ofEpochSecond(asLong(payload.get("exp")));
        if (!expiresAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("JWT expirado");
        }

        Long userId = Long.valueOf((String) payload.get("sub"));
        String email = (String) payload.get("email");
        List<String> roles = ((List<?>) payload.get("roles")).stream()
                .map(Object::toString)
                .toList();
        Instant issuedAt = Instant.ofEpochSecond(asLong(payload.get("iat")));

        return new JwtClaims(userId, email, roles, issuedAt, expiresAt);
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo serializar el token", exception);
        }
    }

    private Map<String, Object> fromJson(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Payload JWT invalido", exception);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo firmar el token", exception);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    public record GeneratedAccessToken(String token, Instant expiresAt) {
    }
}
