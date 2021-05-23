package com.betimes.betext.service;

import com.betimes.betext.exception.ResourceNotFoundException;
import com.betimes.betext.json.UserJson;
import com.betimes.betext.model.User;
import com.betimes.betext.repository.UserRepo;
import com.betimes.betext.security.TokenAuthenticationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;

@Component
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TokenAuthenticationService jwt;

    public ResponseEntity<Object> login(UserJson userJson) {
        User user = this.userRepo.login(userJson.getUsername(), userJson.getPassword())
                .orElseThrow(() -> new ResourceNotFoundException("User", "", userJson.getUsername()));

        JSONObject response = new JSONObject();
        response.put("user_id", user.getUser_id());
        response.put("username", user.getUsername());
        response.put("status", "ROLE_USER");
        response.put("token", jwt.getToken(response.toString()));
        response.remove("status");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> authentication(HttpServletRequest request) {
        String token = jwt.readToken(request);
        if (!token.equals("")){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}