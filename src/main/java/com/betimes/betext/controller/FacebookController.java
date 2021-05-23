package com.betimes.betext.controller;

import com.betimes.betext.json.ResponseJson;
import com.betimes.betext.service.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1.0/fb")
public class FacebookController {
    @Autowired
    private FacebookService facebookService;

    @RequestMapping(value = "/page/search", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseJson pageSearch(@RequestParam("q") String keyword) throws Exception {
        return this.facebookService.fbPageSearch(keyword);
    }
}
