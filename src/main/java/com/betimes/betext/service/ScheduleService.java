package com.betimes.betext.service;

import com.betimes.betext.exception.ResourceNotFoundException;
import com.betimes.betext.json.ScheduleJson;
import com.betimes.betext.model.Schedule;
import com.betimes.betext.repository.ScheduleRepo;
import com.betimes.betext.security.TokenAuthenticationService;
import com.betimes.betext.util.GlobalUtil;
import com.betimes.betext.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ScheduleService {
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    public ResponseEntity<Object> getSchedule(HttpServletRequest request) {
        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        Schedule schedule = this.scheduleRepo.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", username));
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    public ResponseEntity<Object> update(ScheduleJson scheduleJson, HttpServletRequest request) {
        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        Schedule schedule = this.scheduleRepo.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", username));
        schedule.setMinutes(scheduleJson.getMinute());
        schedule.setUpdated_by(username);
        schedule.setUpdated_time(GlobalUtil.getCurrentDateTime());

        return new ResponseEntity<>(this.scheduleRepo.save(schedule), HttpStatus.OK);
    }
}
