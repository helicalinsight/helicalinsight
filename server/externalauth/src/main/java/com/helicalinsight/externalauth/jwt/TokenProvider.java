package com.helicalinsight.externalauth.jwt;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TokenProvider implements {@link Serializable}
 * This component is responsible for creating and verifying JWT tokens used for user authentication and authorization.
 * @author Rajesh
 * Created by author on 17/7/2019.
 */

@Component
public class TokenProvider implements Serializable {

    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");

    /**
     * getUsernameFromToken(String token)
     * @param token    jwt token
     * @return username extracted from the token or {@code null} if not present
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    /**
     * getExpirationDateFromToken(String token)
     * @param token   jwt token
     * @return expiration date extracted from the token or {@code null} if not present
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    /**
     * getIssueDateFromToken(String token)
     * @param token    jwt token
     * @return date extracted from the token or {@code null} if not present
     */
    public Date getIssueDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }
    /**
     * getClaimFromToken(String token, Function<Claims, T> claimsResolver)
     * Retrieves a specific claim from a given JWT token using the provided claims resolver.
     * @param token                      JWT token
     * @param claimsResolver			 This is ultimately a JSON map and any values can be added to it
     * @return value extracted from token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    /**
     * getAllClaimsFromToken(String token) 
     * Retrieves all claims from a given JWT token.
     * @param token				jwt token
     * @return  All claims extracted from the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        String SIGNING_KEY = mapFromClasspathPropertiesFile.get("signing_key");
        String ISSUER = mapFromClasspathPropertiesFile.get("issuer");
        byte[] encodedSigningKey = null;
        try {
            encodedSigningKey = SIGNING_KEY.getBytes(ControllerUtils.defaultCharSet());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Jwts.parser()
                .setSigningKey(encodedSigningKey)
                .requireIssuer(ISSUER)
                .parseClaimsJws(token)
                .getBody();
    }
    /**
     * isTokenExpired(String token)
     * @param token    jwt token
     * @return  {@code true} if the token is expired, {@code false} otherwise.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    /**
     * generateToken(Authentication authentication)
     * Generates a JWT token based on the provided authentication details
     * @param authentication    user authentication details.
     * @return generated JWT token.
     */
    //Refer this part
    public String generateToken(Authentication authentication) {

        Long ACCESS_TOKEN_VALIDITY_SECONDS = Long.parseLong(mapFromClasspathPropertiesFile.get("access_token_validity_seconds"));
        String signingAlgorithm = mapFromClasspathPropertiesFile.get("signing_algorithm");
        ACCESS_TOKEN_VALIDITY_SECONDS *= 1000;
        String SIGNING_KEY = mapFromClasspathPropertiesFile.get("signing_key");
        String AUTHORITIES_KEY = mapFromClasspathPropertiesFile.get("authorities_key");
        String ISSUER = mapFromClasspathPropertiesFile.get("issuer");
        Map<String,Object> headerMap = new HashMap<>();
        headerMap.put("typ",mapFromClasspathPropertiesFile.get("jwt_type"));
        byte[] encodedSigningKey = null;
        try {
            encodedSigningKey = SIGNING_KEY.getBytes(ControllerUtils.defaultCharSet());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Principal principal = (Principal) authentication.getPrincipal();
        String userName = principal.getUsername();
        if (principal.getOrg_name() != null) {
            userName += ":" + principal.getOrg_name();
        }
        //TODO SignatureAlgorith.HS256 in project proerties
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setHeader(headerMap)
                .setSubject(userName)
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.forName(signingAlgorithm), encodedSigningKey)
                .compact();
    }

    /**
     * validateToken(String token, UserDetails userDetails)
     * Validates a given JWT token against the user details
     * @param token                 jwt token
     * @param userDetails           object which provides user details
     * @return {@code true} if the token is valid, {@code flase} otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        String onlyUserName = userDetails.getUsername();
        if (username.contains(":")) {
            onlyUserName = onlyUserName + ":" + username.split(":")[1];
        }

        return (username.equals(onlyUserName) && !isTokenExpired(token));
    }

    /**
     * getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) 
     * Retrieves authentication details from a JWT token and constructs an authentication token
     * @param token					jwt token
     * @param existingAuth          existing authentication details
     * @param userDetails           user credentials
     * @return newly constructed authentication token.
     */
    UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {
        String AUTHORITIES_KEY = mapFromClasspathPropertiesFile.get("authorities_key");
        String SIGNING_KEY = mapFromClasspathPropertiesFile.get("signing_key");
        String ISSUER=mapFromClasspathPropertiesFile.get("issuer");
        byte[] encodedSigningKey = null;
        try {
            encodedSigningKey = SIGNING_KEY.getBytes(ControllerUtils.defaultCharSet());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final JwtParser jwtParser = Jwts.parser().setSigningKey(encodedSigningKey).requireIssuer(ISSUER);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

}
