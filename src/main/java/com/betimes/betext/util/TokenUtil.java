package com.betimes.betext.util;

import com.betimes.betext.security.TokenAuthenticationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class TokenUtil {
    public static String getUsername(String tokenRequest) {
        String username = "";
        if (!tokenRequest.equals("")) {
            JSONObject requestJson = new JSONObject(tokenRequest);
            username = JsonUtil.getString(requestJson, "username");
        }

        return username;
    }
}
