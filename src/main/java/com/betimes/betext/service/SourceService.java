package com.betimes.betext.service;

import com.betimes.betext.exception.MyHttpException;
import com.betimes.betext.exception.ResourceNotFoundException;
import com.betimes.betext.json.FbProfileJson;
import com.betimes.betext.json.ResponseJson;
import com.betimes.betext.json.SourceJson;
import com.betimes.betext.model.FbProfile;
import com.betimes.betext.model.Source;
import com.betimes.betext.repository.FbProfileRepo;
import com.betimes.betext.repository.SourceRepo;
import com.betimes.betext.security.TokenAuthenticationService;
import com.betimes.betext.util.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SourceService {
    private static final Logger log = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    private SourceRepo sourceRepo;
    @Autowired
    private FbProfileRepo fbProfileRepo;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Value("${app.token}")
    private String appToken;
    @Value("${config.fb.page.info}")
    private String pageUrl;
    @Value("${config.redis.facebook.page.key}")
    private String redisFacebookKey;

    public ResponseEntity<ResponseJson> findAll(HttpServletRequest request) {
        ResponseJson responseJson = new ResponseJson();

        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        List<Source> sourceList = new ArrayList<Source>();
        List<SourceJson> sourceJsonList = new ArrayList<>();
        this.sourceRepo.findByUsername(username).forEach(sourceList::add);
        if (sourceList != null) {
            for (Source s : sourceList) {
                FbProfile fbProfile = this.fbProfileRepo.findById(s.getSource_id())
                        .orElseThrow(() -> new ResourceNotFoundException("Facebook", "id", s.getSource_id()));
                SourceJson sourceJson = new SourceJson();
                sourceJson.setSource_id(s.getSource_id());
                sourceJson.setSource_type(s.getSource_type());
                sourceJson.setSource_name(fbProfile.getProfile_name());
                sourceJson.setSource_username(fbProfile.getProfile_username());
                sourceJson.setSource_image(fbProfile.getProfile_picture());
                sourceJson.setSource_link(fbProfile.getProfile_link());
                sourceJson.setSource_category(fbProfile.getProfile_category());
                sourceJson.setSource_fans(FormatUtil.format(fbProfile.getFan_count()));

                sourceJsonList.add(sourceJson);
            }

            responseJson.setData(sourceJsonList);
        }

        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    public ResponseEntity<Source> findById(String sourceId) {
        Source source = this.sourceRepo.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Data Source", "id", sourceId));

        return new ResponseEntity<>(source, HttpStatus.OK);
    }

    public ResponseEntity<Object> create(SourceJson sourceJson, HttpServletRequest request) throws Exception {
        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        Source source = new Source();
        source.setSource_id(sourceJson.getSource_id());
        source.setSource_type(sourceJson.getSource_type());
        source.setCreated_time(GlobalUtil.getCurrentDateTime());
        source.setCreated_by(username);
        source.setRecord_status(GlobalUtil.getActiveStatus());

        try {
            String urlPage = this.pageUrl.replace("@id", sourceJson.getSource_id())
                    .replaceAll("@token", UrlUtil.encodeValue(appToken));
            String jsonResponse = HttpUtil.doGet(urlPage);
            JSONObject jsonObject = new JSONObject(jsonResponse);

            FbProfile fbProfile = new FbProfile();
            fbProfile.setProfile_id(JsonUtil.getString(jsonObject,"id"));
            fbProfile.setProfile_name(JsonUtil.getString(jsonObject, "name"));
            fbProfile.setProfile_username(JsonUtil.getString(jsonObject, "username"));
            fbProfile.setProfile_picture(JsonUtil.getString(jsonObject.getJSONObject("picture").getJSONObject("data"), "url"));
            fbProfile.setProfile_link(JsonUtil.getString(jsonObject, "link"));
            fbProfile.setProfile_category(JsonUtil.getString(jsonObject, "category"));
            fbProfile.setProfile_about(JsonUtil.getString(jsonObject, "about"));
            fbProfile.setFan_count(JsonUtil.getLong(jsonObject, "fan_count"));
            fbProfile.setCreated_by(GlobalUtil.getCreateBy());
            fbProfile.setCreated_time(GlobalUtil.getCurrentDateTime());

            this.fbProfileRepo.save(fbProfile);
            this.redisUtil.addQueue(this.redisFacebookKey, fbProfile.getProfile_id(), fbProfile.getProfile_id(), null, "fetch_posts");
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }

        return new ResponseEntity<>(this.sourceRepo.save(source), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> delete(String sourceId, HttpServletRequest request) {
        try {
            String tokenRequest = this.tokenAuthenticationService.readToken(request);
            String username = TokenUtil.getUsername(tokenRequest);

            Source source = this.sourceRepo.findById(sourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Data Source", "id", sourceId));
            source.setUpdated_time(GlobalUtil.getCurrentDateTime());
            source.setUpdated_by(username);
            source.setRecord_status(GlobalUtil.getInActiveStatus());
            this.sourceRepo.save(source);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> fetchProfile(String sourceId) {
        try {
            this.redisUtil.addQueue(this.redisFacebookKey, sourceId, null, null, "fetch_profile");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getErrorMessage(String message) {
        try {
            return new JSONObject(message).getJSONObject("error").getString("message");
        } catch(Exception e) {
            return message;
        }
    }
}
