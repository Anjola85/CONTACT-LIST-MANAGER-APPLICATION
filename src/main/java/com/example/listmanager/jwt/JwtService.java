//package com.example.listmanager.jwt;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import java.util.function.Function;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.JwtParserBuilder;
//
//
//
//import java.io.Serializable;
//@Component
//public class JwtService implements Serializable {
//    private static final long serialVersionUID = -2550185165626007488L;
//
//    // valid for 5 hours
//    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    public String getUserIdFromToken(String token) {
//        return getClaimFromToken(token, Claims::getSubject);
//    }
//
//
//    //check if the token has expired
//    private Boolean isTokenExpired(String token) {
//        final Date expiration = getExpirationDateFromToken(token);
//        return expiration.before(new Date());
//    }
//
//    // Generate token for user with userId as String
//    public String generateToken(String userId) {
//        return Jwts.builder()
//                .setSubject(userId)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(JWT_TOKEN_VALIDITY))
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//    }
//
//    public Date getExpirationDateFromToken(String token) {
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//
//    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
//    }
//
//    public Boolean validateToken(String token, String userId) {
//        if(userId.isEmpty())
//            return !isTokenExpired(token);
//
//        return (userId.equals(getUserIdFromToken(token)) && !isTokenExpired(token));
//    }
//}
