package com.betimes.betext.controller;

import com.betimes.betext.json.UserJson;
import com.betimes.betext.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*" )
@RequestMapping("/v1.0/users")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> login(@Valid @RequestBody UserJson userJson) {
        return this.userService.login(userJson);
    }

    @RequestMapping(value = "/authentication", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<HttpStatus> authentication(HttpServletRequest request) {
        return this.userService.authentication(request);
    }

}
