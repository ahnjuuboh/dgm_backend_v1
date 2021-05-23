package com.betimes.betext.service;

import com.betimes.betext.exception.MyHttpException;
import com.betimes.betext.exception.ResourceNotFoundException;
import com.betimes.betext.json.*;
import com.betimes.betext.model.FbProfile;
import com.betimes.betext.model.Topic;
import com.betimes.betext.model.TopicCondition;
import com.betimes.betext.repository.FbProfileRepo;
import com.betimes.betext.repository.TopicConditionRepo;
import com.betimes.betext.repository.TopicRepo;
import com.betimes.betext.security.TokenAuthenticationService;
import com.betimes.betext.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class TopicService {
    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicRepo topicRepo;
    @Autowired
    private TopicConditionRepo topicConditionRepo;
    @Autowired
    private ContentService contentService;
    @Autowired
    private FbProfileRepo fbProfileRepo;
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Value("${config.data.api.path}")
    private String configApiPath;
    @Value("${config.data.feed.size}")
    private Long configPageSize;

    public ResponseEntity<ResponseJson> findAll(HttpServletRequest request) {
        ResponseJson responseJson = new ResponseJson();

        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        List<TopicJson> topicJsonList = new ArrayList<TopicJson>();
        List<Topic> topicList = new ArrayList<Topic>();
        this.topicRepo.findByUsername(username).forEach(topicList::add);
        if (!topicList.isEmpty()) {
            for (Topic topic : topicList) {
                TopicJson topicJson = new TopicJson();
                topicJson.setTopic_id(topic.getTopic_id());
                topicJson.setTopic_name(topic.getTopic_name());
                topicJson.setKeyword(topic.getKeyword());
                topicJson.setStart_date(topic.getStart_date());
                topicJson.setEnd_date(topic.getEnd_date());
                topicJson.setDate_range(FormatUtil.formatDateRange(topic.getStart_date(), topic.getEnd_date()));

                topicJson = this.getTopicCondition(topicJson, topic.getTopic_id());

                topicJsonList.add(topicJson);
            }

            responseJson.setData(topicJsonList);
        }

        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    public ResponseEntity<TopicJson> findById(Long topicId) {
        TopicJson topicJson = new TopicJson();
        Topic topic = this.topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        topicJson.setTopic_id(topicId);
        topicJson.setTopic_name(topic.getTopic_name());
        topicJson.setKeyword(topic.getKeyword());
        topicJson.setStart_date(topic.getStart_date());
        topicJson.setEnd_date(topic.getEnd_date());
        topicJson.setDate_range(FormatUtil.formatDateRange(topic.getStart_date(), topic.getEnd_date()));

        topicJson = this.getTopicCondition(topicJson, topicId);

        return new ResponseEntity<>(topicJson, HttpStatus.OK);
    }

    public ResponseEntity<Object> getTopicOverview(Long topicId, String channelStrRequest, HttpServletRequest request) throws Exception {
        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        TopicJson topicJson = new TopicJson();
        Topic topic = this.topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        topicJson.setTopic_id(topicId);
        topicJson.setTopic_name(topic.getTopic_name());
        topicJson.setKeyword(topic.getKeyword());
        topicJson.setStart_date(topic.getStart_date());
        topicJson.setEnd_date(topic.getEnd_date());

        topicJson = this.getTopicCondition(topicJson, topicId);
        JSONObject response = new JSONObject(topicJson);
        response.put("date_range", FormatUtil.formatDateRange(topic.getStart_date(), topic.getEnd_date()));

        String[] channelList = channelStrRequest.split(",");

        JSONObject overviewData = this.contentService.getEsContent(username, channelList, topicJson, "latest", 0L, 0L);
//        Long allMessage = JsonUtil.getLong(overviewData.getJSONObject("hits"), "total");
        Long allMessage = JsonUtil.getLong(overviewData.getJSONObject("hits").getJSONObject("total"), "value");


        FeedSummaryJson feedSummaryJson = new FeedSummaryJson();
        FeedSummaryItemJson allMessageObj = new FeedSummaryItemJson();
        allMessageObj.setCount(allMessage);
        allMessageObj.setFormatted(FormatUtil.formatWithComma(allMessage));
        feedSummaryJson.setAll(allMessageObj);
        Long facebookMessage = 0L;

        JSONArray sourceType = overviewData.getJSONObject("aggregations").getJSONObject("source_type").getJSONArray("buckets");
        JSONArray pieChartSeriesData = new JSONArray();
        JSONArray resultOvertimeData = new JSONArray();

        List<String> pieChartLegendData = new ArrayList<>();
        if (sourceType.length() > 0) {
            for (int i = 0; i < sourceType.length(); i++) {
                FeedSummaryItemJson message = new FeedSummaryItemJson();
                JSONObject bucket = sourceType.getJSONObject(i);
                PieChartJson pieChartJson = new PieChartJson();

                if (JsonUtil.getString(bucket, "key").equals("facebook")) {
                    String key = "Facebook";
                    facebookMessage = JsonUtil.getLong(bucket, "doc_count");
                    message.setCount(facebookMessage);
                    message.setFormatted(FormatUtil.formatWithComma(facebookMessage));
                    feedSummaryJson.setFacebook(message);

                    pieChartLegendData.add(key);

                    pieChartJson.setName(key);
                    pieChartJson.setValue(facebookMessage);
                    pieChartJson.setItemStyle(new JSONObject("{'color': 'rgba(58, 85, 159, 1)'}").toMap());

                    JSONArray dateRangBuckets = bucket.getJSONObject("date_range").getJSONArray("buckets");
                    resultOvertimeData.put(this.getResultOvertimeData(dateRangBuckets, key));
                }
                pieChartSeriesData.put(pieChartJson);
                message = null;
                pieChartJson = null;
            }
        } else {
            FeedSummaryItemJson message = new FeedSummaryItemJson();
            message.setCount(0L);
            message.setFormatted("0");
            feedSummaryJson.setFacebook(message);
        }
        JSONObject pieChartData = new JSONObject();
        pieChartData.put("data_legend", pieChartLegendData);
        pieChartData.put("data_series", pieChartSeriesData);

        response.put("network_referral", pieChartData);
        response.put("result_overtime", resultOvertimeData);
        response.put("message", new JSONObject(feedSummaryJson));

        ResponseJson responseJson = new ResponseJson();
        responseJson.setData(response.toMap());

        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    public ResponseEntity<Object> getTopicFeed(Long topicId, String channelStrRequest, int page, String sortByStr, HttpServletRequest request) throws Exception {
        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);

        TopicJson topicJson = new TopicJson();
        Topic topic = this.topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        topicJson.setTopic_id(topicId);
        topicJson.setTopic_name(topic.getTopic_name());
        topicJson.setKeyword(topic.getKeyword());
        topicJson.setStart_date(topic.getStart_date());
        topicJson.setEnd_date(topic.getEnd_date());

        topicJson = this.getTopicCondition(topicJson, topicId);

        Long from = (long) this.configPageSize * (page - 1);

        String[] channelList = channelStrRequest.split(",");

        JSONObject overviewData = this.contentService.getEsContent(username, channelList, topicJson, sortByStr, from, this.configPageSize);
        JSONArray jsonArray = overviewData.getJSONObject("hits").getJSONArray("hits");
//        Long allMessage = JsonUtil.getLong(overviewData.getJSONObject("hits"), "total");
        Long allMessage = JsonUtil.getLong(overviewData.getJSONObject("hits").getJSONObject("total"), "value");
        List<FbProfile> fbProfileList = this.fbProfileRepo.findAll();
        int size = jsonArray.length();

        List<FeedJson> feedList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            FeedJson feedJson = new FeedJson();
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");

            feedJson.setId(JsonUtil.getString(jsonObject, "id"));
            String type = JsonUtil.getString(jsonObject, "type");
            if (!jsonObject.isNull("parent")) type = "reply";
            feedJson.setType(FormatUtil.capitalize(type));
            feedJson.setSource(JsonUtil.getString(jsonObject, "source_type"));
            feedJson.setMessage(JsonUtil.getString(jsonObject, "message"));
            feedJson.setStatus_type(JsonUtil.getString(jsonObject, "status_type"));
            feedJson.setMedia_type(JsonUtil.getString(jsonObject, "media_type"));
            feedJson.setLink(JsonUtil.getString(jsonObject, "link"));
            feedJson.setPicture(JsonUtil.getString(jsonObject, "picture"));

            if (!jsonObject.isNull("permalink_url")) {
                feedJson.setPermalink_url(JsonUtil.getString(jsonObject, "permalink_url"));
            } else {
                feedJson.setPermalink_url("https://www.facebook.com/" + JsonUtil.getString(jsonObject, "id"));
            }

            Long reaction = JsonUtil.getLong(jsonObject, "likes") + JsonUtil.getLong(jsonObject, "love") +
                    JsonUtil.getLong(jsonObject, "wow") + JsonUtil.getLong(jsonObject, "haha") +
                    JsonUtil.getLong(jsonObject, "sorry") + JsonUtil.getLong(jsonObject, "anger");
            Long shares = JsonUtil.getLong(jsonObject, "shares");
            Long comments = JsonUtil.getLong(jsonObject, "comments");
            feedJson.setReactions(reaction > 0 ? FormatUtil.format(reaction) : null);

            feedJson.setShares(shares > 0 ? FormatUtil.format(shares) : null);
            feedJson.setComments(comments > 0 ? FormatUtil.format(comments) : null);


            feedJson.setCreated_time(FormatUtil.formatDateTime(JsonUtil.getString(jsonObject, "created_time")));

            if (!jsonObject.isNull("from") && JsonUtil.getString(jsonObject, "type").equals("post")) {
                FbProfile profile = fbProfileList.stream().filter(x -> x.getProfile_id()
                        .equals(JsonUtil.getString(jsonObject.getJSONObject("from"), "id"))).findAny().orElse(null);
                if (profile != null) {
                    FeedAuthorJson authorJson = new FeedAuthorJson();
                    authorJson.setId(profile.getProfile_id());
                    authorJson.setName(profile.getProfile_name());
                    authorJson.setPicture(profile.getProfile_picture());
                    authorJson.setUrl(profile.getProfile_link());
                    feedJson.setAuthor(authorJson);
                }
            }

            feedList.add(feedJson);
        }
        ResponseFeedJson responseFeedJson = new ResponseFeedJson();
        responseFeedJson.setData(feedList);

        if (allMessage > (page * this.configPageSize)) {
            page++;
            PagingJson pagingJson = new PagingJson();
            String nextPage = this.configApiPath + "/topic/feed?topicId=" + topicId + "&page=" + page + "&channel=" + channelStrRequest + "&sort=" + sortByStr;
            pagingJson.setNext(nextPage);
            responseFeedJson.setPaging(pagingJson);
        }

        return new ResponseEntity<>(new JSONObject(responseFeedJson).toString(), HttpStatus.OK);
    }

    public ResponseEntity<Object> getComment(String postId, int page, String sortByStr) throws Exception {
        Long from = (long) this.configPageSize * (page - 1);

        JSONObject overviewData = this.contentService.getComment(postId, sortByStr, from, this.configPageSize);
        JSONArray jsonArray = overviewData.getJSONObject("hits").getJSONArray("hits");
        Long allMessage = JsonUtil.getLong(overviewData.getJSONObject("hits").getJSONObject("total"), "value");
        int size = jsonArray.length();

        List<FeedJson> feedList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            FeedJson feedJson = new FeedJson();
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("_source");

            feedJson.setId(JsonUtil.getString(jsonObject, "id"));
            String type = JsonUtil.getString(jsonObject, "type");
            if (!jsonObject.isNull("parent")) type = "reply";
            feedJson.setType(FormatUtil.capitalize(type));
            feedJson.setSource(JsonUtil.getString(jsonObject, "source_type"));
            feedJson.setMessage(JsonUtil.getString(jsonObject, "message"));
            feedJson.setStatus_type(JsonUtil.getString(jsonObject, "status_type"));
            feedJson.setMedia_type(JsonUtil.getString(jsonObject, "media_type"));
            feedJson.setLink(JsonUtil.getString(jsonObject, "link"));
            feedJson.setPicture(JsonUtil.getString(jsonObject, "picture"));

            if (!jsonObject.isNull("permalink_url")) {
                feedJson.setPermalink_url(JsonUtil.getString(jsonObject, "permalink_url"));
            } else {
                feedJson.setPermalink_url("https://www.facebook.com/" + JsonUtil.getString(jsonObject, "id"));
            }

            Long reaction = JsonUtil.getLong(jsonObject, "likes") + JsonUtil.getLong(jsonObject, "love") +
                    JsonUtil.getLong(jsonObject, "wow") + JsonUtil.getLong(jsonObject, "haha") +
                    JsonUtil.getLong(jsonObject, "sorry") + JsonUtil.getLong(jsonObject, "anger");
            Long shares = JsonUtil.getLong(jsonObject, "shares");
            Long comments = JsonUtil.getLong(jsonObject, "comments");
            feedJson.setReactions(reaction > 0 ? FormatUtil.format(reaction) : null);

            feedJson.setShares(shares > 0 ? FormatUtil.format(shares) : null);
            feedJson.setComments(comments > 0 ? FormatUtil.format(comments) : null);


            feedJson.setCreated_time(FormatUtil.formatDateTime(JsonUtil.getString(jsonObject, "created_time")));

            feedList.add(feedJson);
        }
        ResponseFeedJson responseFeedJson = new ResponseFeedJson();
        responseFeedJson.setData(feedList);

        if (allMessage > (page * this.configPageSize)) {
            page++;
            PagingJson pagingJson = new PagingJson();
            String nextPage = this.configApiPath + "/topic/comments?postId=" + postId + "&page=" + page + "&sort=" + sortByStr;
            pagingJson.setNext(nextPage);
            responseFeedJson.setPaging(pagingJson);
        }

        return new ResponseEntity<>(new JSONObject(responseFeedJson).toString(), HttpStatus.OK);
    }

    public ResponseEntity<Object> create(TopicJson topicJson, HttpServletRequest request) {
        Topic topic = new Topic();
        topic.setTopic_name(topicJson.getTopic_name());
        topic.setKeyword(topicJson.getKeyword());
        topic.setStart_date(topicJson.getStart_date());
        topic.setEnd_date(topicJson.getEnd_date());
        topic.setCreated_time(GlobalUtil.getCurrentDateTime());
        topic.setRecord_status(GlobalUtil.getActiveStatus());

        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);
        topic.setCreated_by(username);

        topic = this.topicRepo.save(topic);

        if (topic.getTopic_id() != null) {
            List<String> conditionAnd;
            List<String> conditionOr;
            List<String> conditionNot;

            conditionAnd = topicJson.getCondition_and();
            conditionOr = topicJson.getCondition_or();
            conditionNot = topicJson.getCondition_not();
            if (!conditionAnd.isEmpty()) this.createCondition(conditionAnd, topic.getTopic_id(), "AND");
            if (!conditionOr.isEmpty()) this.createCondition(conditionOr, topic.getTopic_id(), "OR");
            if (!conditionNot.isEmpty()) this.createCondition(conditionNot, topic.getTopic_id(), "NOT");
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    public ResponseEntity<Object> update(Long topicId, TopicJson topicJson, HttpServletRequest request) {
        Topic topic = this.topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        topic.setTopic_name(topicJson.getTopic_name());
        topic.setKeyword(topicJson.getKeyword());
        topic.setStart_date(topicJson.getStart_date());
        topic.setEnd_date(topicJson.getEnd_date());
        topic.setUpdated_time(GlobalUtil.getCurrentDateTime());

        String tokenRequest = this.tokenAuthenticationService.readToken(request);
        String username = TokenUtil.getUsername(tokenRequest);
        topic.setUpdated_by(username);

        this.topicConditionRepo.deleteByTopicId(topicId);

        List<String> conditionAnd;
        List<String> conditionOr;
        List<String> conditionNot;

        conditionAnd = topicJson.getCondition_and();
        conditionOr = topicJson.getCondition_or();
        conditionNot = topicJson.getCondition_not();
        if (!conditionAnd.isEmpty()) this.createCondition(conditionAnd, topic.getTopic_id(), "AND");
        if (!conditionOr.isEmpty()) this.createCondition(conditionOr, topic.getTopic_id(), "OR");
        if (!conditionNot.isEmpty()) this.createCondition(conditionNot, topic.getTopic_id(), "NOT");

        return new ResponseEntity<>(this.topicRepo.save(topic), HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> delete(Long topicId, HttpServletRequest request) {
        try {
            Topic topic = this.topicRepo.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
            topic.setRecord_status(GlobalUtil.getInActiveStatus());

            String tokenRequest = this.tokenAuthenticationService.readToken(request);
            String username = TokenUtil.getUsername(tokenRequest);
            topic.setUpdated_by(username);

            this.topicRepo.save(topic);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private TopicJson getTopicCondition(TopicJson topicJson, Long topicId) {
        List<TopicCondition> topicConditionList = new ArrayList<TopicCondition>();
        this.topicConditionRepo.findByTopicId(topicId).forEach(topicConditionList::add);
        List<String> conditionAnd = new ArrayList<String>();
        List<String> conditionOr = new ArrayList<String>();
        List<String> conditionNot = new ArrayList<String>();

        if (!topicConditionList.isEmpty()) {
            for (TopicCondition c : topicConditionList) {
                if (c.getCondition_symbol().equals("AND")) conditionAnd.add(c.getCondition_text());
                if (c.getCondition_symbol().equals("OR")) conditionOr.add(c.getCondition_text());
                if (c.getCondition_symbol().equals("NOT")) conditionNot.add(c.getCondition_text());
            }
        }
        topicJson.setCondition_and(conditionAnd);
        topicJson.setCondition_or(conditionOr);
        topicJson.setCondition_not(conditionNot);

        return topicJson;
    }


    private void createCondition(List<String> arr, Long topicId, String symbol) {
        TopicCondition condition;
        for (String s : arr) {
            condition = new TopicCondition();
            condition.setTopic_id(topicId);
            condition.setCondition_symbol(symbol);
            condition.setCondition_text(s);

            this.topicConditionRepo.save(condition);
        }
    }

    private JSONObject getResultOvertimeData(JSONArray sourceData, String key) {
        JSONObject response = new JSONObject();
        response.put("key", key);

        List<List<Long>> valueList = new ArrayList<>();
        int size = sourceData.length();
        for (int i = 0; i < size; i++) {
            JSONObject bucket = sourceData.getJSONObject(i);
            List<Long> value = new ArrayList<>();
            value.add(JsonUtil.getLong(bucket, "key"));
            value.add(JsonUtil.getLong(bucket, "doc_count"));

            valueList.add(value);
        }
        response.put("values", valueList);
        return response;
    }
}