package com.betimes.betext.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilterCheck extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = getAuthentication((HttpServletRequest)request);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            String user  = checkToken(token);
            JSONObject obj = new JSONObject(user);

            String role_user = obj.getString("status");

            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new SimpleGrantedAuthority(role_user));
            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, grantedAuths) :

                    null;
        }
        return null;
    }

    public String checkToken(String token){
        token = token.replace("Bearer", "");
        String user  = "";
        try{
            user = Jwts.parser()
                    .setSigningKey("betext@Betimes.biz")
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }catch(ExpiredJwtException e){
            DecodedJWT jwt = JWT.decode(token);
            user = jwt.getSubject();
        }
        return user;
    }
}
