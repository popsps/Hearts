package edu.gmu.server.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtProvider {
  private final String ROLE_KEY = "role";
  private final long validityInMilliseconds;
  private final KeyPair keyPair;
  final String privateKey;
  final String publicKey;

  public JwtProvider(@Value("${security.jwt.access_token.expiration:800000}") long validityInMilliseconds,
                     @Value("${AUTH_JWT_PRIVATE_KEY}") final String privateKey,
                     @Value("${AUTH_JWT_PUBLIC_KEY}") final String publicKey) {
    this.validityInMilliseconds = validityInMilliseconds;
    this.privateKey = privateKey;
    this.publicKey = publicKey;
    this.keyPair = generateKeyPair(publicKey, privateKey);
  }

  private KeyPair generateKeyPair(String publicKey, String privateKey) {
    try {
      return new KeyPair(generatePublicKey(publicKey), generatePrivateKey(privateKey));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return null;
    }
  }

  private PrivateKey generatePrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
    final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    privateKey = parseKey(privateKey);
    final byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKey);
    final KeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
    return keyFactory.generatePrivate(keySpec);
  }

  private PublicKey generatePublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
    final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    publicKey = parseKey(publicKey);
    final byte[] decodedPublicKey = Base64.getDecoder().decode(publicKey);
    final KeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
    return keyFactory.generatePublic(keySpec);
  }

  private String parseKey(String key) {
    return key
      .replaceAll("-----BEGIN (.*?)-----", "")
      .replaceAll("-----END (.*)----", "")
      .replaceAll("\\s+", "")
      .trim();
  }

  public String createToken(String username, String role) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put(this.ROLE_KEY, role);
    Date now = new Date();
    Date expireAt = new Date(now.getTime() + validityInMilliseconds);
    return Jwts.builder()
      .setClaims(claims)
      .setId(UUID.randomUUID().toString())
      .setIssuedAt(now)
      .setExpiration(expireAt)
      .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
      .compact();
  }

  public boolean isValidToken(final String token) {
    try {
      final JwsHeader jwsHeader =
        Jwts.parser()
          .setSigningKey(keyPair.getPublic())
          .parseClaimsJws(token)
          .getHeader();
      if (jwsHeader.getAlgorithm().equals("RS256")) {
        final Claims claims =
          Jwts.parser()
          .setSigningKey(keyPair.getPublic())
          .parseClaimsJws(token)
          .getBody();
        if (claims.getExpiration().before(new Date()))
          return false;
        return true;
      } else
        return false;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public String getUsername(String token) {
    return Jwts.parser().setSigningKey(keyPair.getPublic())
      .parseClaimsJws(token).getBody().getSubject();
  }

  public String getRole(String token) {
    return Jwts.parser().setSigningKey(this.keyPair.getPublic())
      .parseClaimsJws(token).getBody().get(this.ROLE_KEY, String.class);
  }
}
