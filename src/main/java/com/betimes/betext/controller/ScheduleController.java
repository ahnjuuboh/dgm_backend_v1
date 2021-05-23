package com.betimes.betext.controller;

import com.betimes.betext.json.ScheduleJson;
import com.betimes.betext.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1.0")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @RequestMapping(value = "/schedule", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> findById(HttpServletRequest request) {
        return this.scheduleService.getSchedule(request);
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.PUT, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> update(@Valid @RequestBody ScheduleJson scheduleJson, HttpServletRequest request) {
        return this.scheduleService.update(scheduleJson, request);
    }
}
