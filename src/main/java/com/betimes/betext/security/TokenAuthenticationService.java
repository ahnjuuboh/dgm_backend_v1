package com.betimes.betext.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class TokenAuthenticationService {
    @Value("${jwt.expirationtime}")
    private Long expireHours;
    @Value("${jwt.secret}")
    private  String SECRET;
    @Value("${jwt.token_prefix}")
    private  String TOKEN_PREFIX;
    @Value("${jwt.header_string}")
    private  String HEADER_STRING;

    public String getToken(String username) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(getExpirationTime())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return TOKEN_PREFIX + " " + JWT;
    }

    public String readToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String data = "";
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();
            data = user;
        }
        return data;
    }

    private Date getExpirationTime()
    {
        Date now = new Date();
        Long expireInMillis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMillis + now.getTime());
    }
}
