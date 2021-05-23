package com.betimes.betext.controller;

import com.betimes.betext.json.ResponseJson;
import com.betimes.betext.json.TopicJson;
import com.betimes.betext.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1.0")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @RequestMapping(value = "/topic", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResponseJson>  findAll(HttpServletRequest request) {
        return this.topicService.findAll(request);
    }

    @RequestMapping(value = "/topic/{id}", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<TopicJson>  findById(@PathVariable("id") Long topicId) {
        return this.topicService.findById(topicId);
    }

    @RequestMapping(value = "/topic/overview", method = RequestMethod.GET, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object>  overview(@RequestParam(required = true) Long topicId,
                                            @RequestParam(value = "channel", defaultValue = "facebook,twitter,youtube") String channel,
                                            HttpServletRequest request) throws Exception {
        return this.topicService.getTopicOverview(topicId, channel, request);
    }

    @RequestMapping(value = "/topic/feed", method = RequestMethod.GET, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> feed(@RequestParam(required = true) Long topicId,
                                       @RequestParam(value = "channel", defaultValue = "facebook,twitter,youtube") String channel,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "sort", defaultValue = "engagement") String sortBy,
                                       HttpServletRequest request) throws Exception {
        return this.topicService.getTopicFeed(topicId, channel, page, sortBy, request);
    }

    @RequestMapping(value = "/topic/comment", method = RequestMethod.GET, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> comment(@RequestParam(required = true) String postId,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "sort", defaultValue = "engagement") String sortBy) throws Exception {
        return this.topicService.getComment(postId, page, sortBy);
    }

    @RequestMapping(value = "/topic", method = RequestMethod.POST, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> create(@Valid @RequestBody TopicJson topicJson, HttpServletRequest request) {
        return this.topicService.create(topicJson, request);
    }

    @RequestMapping(value = "/topic/{id}", method = RequestMethod.PUT, produces = { MimeTypeUtils.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> update(@PathVariable("id") Long topicId, @Valid @RequestBody TopicJson topicJson,
                                         HttpServletRequest request) {
        return this.topicService.update(topicId, topicJson, request);
    }

    @RequestMapping(value = "/topic/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long topicId, HttpServletRequest request) {
        return this.topicService.delete(topicId, request);
    }
}
