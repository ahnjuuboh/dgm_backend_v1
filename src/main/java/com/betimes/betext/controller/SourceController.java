package com.betimes.betext.controller;

import com.betimes.betext.json.ResponseJson;
import com.betimes.betext.json.ScheduleJson;
import com.betimes.betext.json.SourceJson;
import com.betimes.betext.model.Source;
import com.betimes.betext.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1.0")
public class SourceController {
    @Autowired
    private SourceService sourceService;

    @RequestMapping(value = "/source", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseJson>  findAll(HttpServletRequest request) {
        return this.sourceService.findAll(request);
    }

    @RequestMapping(value = "/source/{id}", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Source>  findById(@PathVariable("id") String sourceId, HttpServletRequest request) {
        return this.sourceService.findById(sourceId);
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> create(@Valid @RequestBody SourceJson sourceJson, HttpServletRequest request) throws Exception {
        return this.sourceService.create(sourceJson, request);
    }

    @RequestMapping(value = "/source/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") String sourceId, HttpServletRequest request) {
        return this.sourceService.delete(sourceId, request);
    }

    @RequestMapping(value = "/fetch/{id}", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<HttpStatus> fetchInfo(@PathVariable("id") String sourceId) {
        return this.sourceService.fetchProfile(sourceId);
    }


}
