package com.betimes.betext.service;

import com.betimes.betext.exception.MyHttpException;
import com.betimes.betext.json.FbSearchJson;
import com.betimes.betext.json.ResponseJson;
import com.betimes.betext.util.FormatUtil;
import com.betimes.betext.util.HttpUtil;
import com.betimes.betext.util.JsonUtil;
import com.betimes.betext.util.UrlUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FacebookService {
    private static final Logger log = LoggerFactory.getLogger(FacebookService.class);

    @Value("${app.token}")
    private String appToken;
    @Value("${config.fb.page.search}")
    private String pageSearchUrl;
    @Value("${config.fb.page.info}")
    private String pageUrl;

    public ResponseJson fbPageSearch(String q) throws Exception {
        log.info("Facebook page search '" + q + "'");
        ResponseJson responseJson = new ResponseJson();
        try {
            List<FbSearchJson> pageList = new ArrayList<>();
            String url = this.pageSearchUrl.replaceAll("@query", this.regex(q))
                    .replaceAll("@token", UrlUtil.encodeValue(appToken));
            String jsonResponse = HttpUtil.doGet(url);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                FbSearchJson p = new FbSearchJson();
                JSONObject pageObj = jsonArray.getJSONObject(i);
                String urlPage = this.pageUrl.replace("@id", JsonUtil.getString(pageObj, "id"))
                        .replaceAll("@token", UrlUtil.encodeValue(appToken));

                String pageJsonResponse = HttpUtil.doGet(urlPage);
                JSONObject pageJsonObject = new JSONObject(pageJsonResponse);

                String fanCount = FormatUtil.format(JsonUtil.getLong(pageJsonObject, "fan_count"));
                p.setId(JsonUtil.getString(pageJsonObject, "id"));
                p.setName(JsonUtil.getString(pageJsonObject, "name"));
                p.setUsername(JsonUtil.getString(pageJsonObject, "username"));
                p.setPicture(JsonUtil.getString(pageJsonObject.getJSONObject("picture").getJSONObject("data"), "url"));
                p.setFan_count(fanCount);

                pageList.add(p);
            }

            responseJson.setData(pageList);
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }
        return responseJson;
    }

    private String regex(String url) {
        Pattern p = Pattern.compile("(?:(?:http|https)://)?(?:www.)?facebook.com/(?:(?:w)*#!/)?(?:pages/)?(?:[?w-]*/)?(?:profile.php?id=(?=d.*))?([w-]*)?");
        Matcher m = p.matcher(url);

        if (m.find()) {
            String s = m.replaceAll("$1");
            return s;
        } else {
            return url;
        }
    }

    private String getErrorMessage(String message) {
        try{
            return new JSONObject(message).getJSONObject("error").getString("message");
        }catch(Exception e) {
            return message;
        }
    }
}
