package com.betimes.betext.service;

import com.betimes.betext.exception.MyHttpException;
import com.betimes.betext.json.*;
import com.betimes.betext.model.Source;
import com.betimes.betext.repository.SourceRepo;
import com.betimes.betext.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentService {
    @Autowired
    private SourceRepo sourceRepo;

    @Value("${config.elasticsearch.content}")
    private String esContent;

    public JSONObject getEsContent(String username, String[] channelList, TopicJson topicJson, String sortByStr, Long from, Long requestSize) throws Exception {
        JSONObject jsonObject = new JSONObject();
        TopicEsRequestJson esRequestJson = new TopicEsRequestJson();
        esRequestJson.setSize(requestSize);
        esRequestJson.setFrom(from);

        String sortStr = this.getSortStr(sortByStr);
        esRequestJson.setSort(new JSONObject(sortStr));

        List<Object> mainShouldList = new ArrayList<>();

        TopicEsSubBoolJson boolMust = new TopicEsSubBoolJson();
        JSONObject mustObject = new JSONObject();
        List<Object> mustList = new ArrayList<>();
        QueryStringJson primary = new QueryStringJson();
        String primaryMust = "{\"query\": \"*" + topicJson.getKeyword() + "*\", \"fields\": [ \"*\"]}";
        primary.setQuery_string(new JSONObject(primaryMust));
        mustList.add(primary);

        // Set date range between topic start_date and end_date
        String rangeStr = "{\n" +
                "          \"range\": {\n" +
                "            \"created_time\": {\n" +
                "              \"gte\": \"" + topicJson.getStart_date() + "\",\n" +
                "              \"lte\": \"" + topicJson.getEnd_date() + "\",\n" +
                "              \"format\": \"yyyy-MM-dd\"\n" +
                "            }\n" +
                "          }\n" +
                "        }";
        mustList.add(new JSONObject(rangeStr));

        // Set secondary keyword from AND condition to mainShouldList
        if (topicJson.getCondition_and() != null) {
            for (String s : topicJson.getCondition_and()) {
                QueryStringJson queryStringJson = new QueryStringJson();
                String mustStr = "{\"query\": \"*" + s + "*\", \"fields\": [ \"*\"]}";;
                queryStringJson.setQuery_string(new JSONObject(mustStr));
                mustList.add(queryStringJson);
            }
        }
        mustObject.put("must", mustList);
        boolMust.setBool(mustObject);
        mainShouldList.add(boolMust);

        // Set secondary keyword from OR condition to mainShouldList
        TopicEsSubBoolJson boolShould = new TopicEsSubBoolJson();
        JSONObject shouldObject = new JSONObject();
        List<Object> shouldList = new ArrayList<>();
        if (topicJson.getCondition_or().size() > 0) {
            for (String s : topicJson.getCondition_or()) {
                QueryStringJson queryStringJson = new QueryStringJson();
                String shouldStr = "{\"query\": \"*" + s + "*\", \"fields\": [ \"*\"]}";;
                queryStringJson.setQuery_string(new JSONObject(shouldStr));
                shouldList.add(queryStringJson);
            }

            shouldObject.put("should", shouldList);
            boolShould.setBool(shouldObject);
            mainShouldList.add(boolShould);
        }

        // Set secondary keyword from NOT condition to must_not
        List<Object> mustNotList = new ArrayList<>();
        if (topicJson.getCondition_not().size() > 0) {
            for (String s : topicJson.getCondition_not()) {
                QueryStringJson queryStringJson = new QueryStringJson();
                String mustNotStr = "{\"query\": \"*" + s + "*\", \"fields\": [ \"*\"]}";;
                queryStringJson.setQuery_string(new JSONObject(mustNotStr));
                mustNotList.add(queryStringJson);
            }
        }

        TopicEsBoolJson esBoolJson = new TopicEsBoolJson();
        esBoolJson.setShould(mainShouldList);
        esBoolJson.setMust_not(mustNotList);

        TopicEsQueryJson esQueryJson = new TopicEsQueryJson();
        esQueryJson.setBool(esBoolJson);

        esRequestJson.setQuery(esQueryJson);
        esRequestJson.setQuery(esQueryJson);

        // Aggregation String
        String aggsStr = "{\n" +
                "  \"source_type\": {\n" +
                "    \"terms\": {\n" +
                "      \"field\": \"source_type.keyword\"\n" +
                "    },\n" +
                "    \"aggs\": {\n" +
                "      \"date_range\": {\n" +
                "        \"date_histogram\": {\n" +
                "          \"field\": \"created_time\",\n" +
                "          \"interval\": \"day\",\n" +
                "          \"format\": \"yyyy-MM-dd\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"post_count\": {\n" +
                "        \"filter\": {\n" +
                "          \"term\": {\n" +
                "            \"type\": \"post\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"comment_count\": {\n" +
                "        \"filter\": {\n" +
                "          \"term\": {\n" +
                "            \"type\": \"comment\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"share_count\": {\n" +
                "        \"sum\": {\n" +
                "          \"field\": \"shares\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"reaction_count\": {\n" +
                "        \"sum\": {\n" +
                "          \"script\": {\n" +
                "            \"inline\": \"doc['likes'].value + doc['love'].value + doc['wow'].value + doc['haha'].value + doc['sorry'].value + doc['anger'].value\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        esRequestJson.setAggs(new JSONObject(aggsStr));

        // Filter
        JSONArray filterJsonArray = new JSONArray();
        // Find data source from user request
        List<Source> sourceList = this.sourceRepo.findByUsername(username);
        if (sourceList.size() > 0) {
            JSONArray sourceJsonArray = new JSONArray();
            for (Source s : sourceList) {
                sourceJsonArray.put(s.getSource_id());
            }
            String filterStr = "{\"terms\": {\"source_id\": " + sourceJsonArray.toString() + "}}";
            // Add data source id to list
            filterJsonArray.put(new JSONObject(filterStr));
        }

        // must filter by channel
        String channelFilterStr = "{\"terms\": {\"source_type\": " + new JSONArray(channelList).toString() + "}}";
        filterJsonArray.put(new JSONObject(channelFilterStr));
        String postFilterStr = "{\n\"term\": {\n\"type\": \"post\"\n}\n}";
        filterJsonArray.put(new JSONObject(postFilterStr));
        esBoolJson.setFilter(filterJsonArray.toList());

        esBoolJson.setMinimum_should_match(1L);

        try {
            String jsonResponse = HttpUtil.doPost(this.esContent + "/_search",
                    new JSONObject(esRequestJson).toString());
            jsonObject = new JSONObject(jsonResponse);
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }

        return jsonObject;
    }

    public JSONObject getComment(String postId, String sortByStr, Long from, Long requestSize) throws Exception {
        JSONObject jsonObject = new JSONObject();
        TopicEsRequestJson esRequestJson = new TopicEsRequestJson();
        esRequestJson.setSize(requestSize);
        esRequestJson.setFrom(from);

        String sortStr = this.getSortStr(sortByStr);
        esRequestJson.setSort(new JSONObject(sortStr));

        String query = "{\n" +
                "    \"term\": {\n" +
                "      \"from.id\": \"" + postId + "\"\n" +
                "    }\n" +
                "  }";
        esRequestJson.setQuery(new JSONObject(query));

        try {
            String jsonResponse = HttpUtil.doPost(this.esContent + "/_search",
                    new JSONObject(esRequestJson).toString());
            jsonObject = new JSONObject(jsonResponse);
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }

        return jsonObject;
    }

    private String getSortStr(String sortByStr) {
        String sortStr = "";
        if (sortByStr.equals("latest")) {
            sortStr = "{\"created_time\": \"desc\"}";
        } else {
            sortStr = "{\n" +
                    "    \"_script\": {\n" +
                    "      \"type\": \"number\",\n" +
                    "      \"script\": {\n" +
                    "        \"lang\": \"painless\",\n" +
                    "        \"source\": \"doc['likes'].value + doc['love'].value + doc['wow'].value + doc['haha'].value + doc['sorry'].value + doc['anger'].value\",\n" +
                    "        \"params\": {\n" +
                    "          \"factor\": 1.1\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"order\": \"desc\"\n" +
                    "    }\n" +
                    "  }";
        }

        return sortStr;
    }

    private String getErrorMessage(String message) {
        try{
            return new JSONObject(message).getJSONObject("error").getString("message");
        }catch(Exception e) {
            return message;
        }
    }
}