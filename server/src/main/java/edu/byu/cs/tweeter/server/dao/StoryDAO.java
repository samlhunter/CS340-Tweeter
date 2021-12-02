package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;

public class StoryDAO  implements IStoryDAO{

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table table;

    public StoryDAO () {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.table = this.dynamoDB.getTable("story");
    }

    @Override
    public GetStoryResponse getStory(String senderUsername, String lastStatusDateTime, int limit) {
        List<Status> story = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#username", "username");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(senderUsername));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("story")
                .withKeyConditionExpression("#username = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("story")
                .withKeyConditionExpression("#username = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastStatusDateTime != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("username", new AttributeValue().withS(senderUsername));
            lastKey.put("datetime", new AttributeValue().withS(lastStatusDateTime));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                User user = new User(item.get("userFirstName").getS(),
                        item.get("userLastName").getS(),
                        item.get("username").getS(),
                        item.get("imageURL").getS());
                List<String> urls = item.get("urls").getSS();
                List<String> mentions = item.get("mentions").getSS();
                Status status = new Status(item.get("message").getS(),
                        user, item.get("datetime").getS(),
                        urls, mentions);
                story.add(status);
            }
        }

        System.out.println("Successfully got story");
        System.out.println(story);
        if (checkItems.size() > items.size()) {
            return new GetStoryResponse(story, true);
        }
        else {
            System.out.println("We have no more statuses to get");
            return new GetStoryResponse(story, false);
        }
    }

    @Override
    public void putStory(Status status) {
        // Convert mentions and urls to sets
        HashSet<String> mentionsSet = new HashSet<>(status.getMentions());
        HashSet<String> urlsSet = new HashSet<>(status.getUrls());
        mentionsSet.add("");
        urlsSet.add("");
        table.putItem(new Item().withPrimaryKey("username", status.getUser().getAlias(), "datetime", status.getDate())
                .withString("message", status.getPost())
                .withStringSet("mentions", mentionsSet)
                .withStringSet("urls", urlsSet)
                .withString("imageURL", status.getUser().getImageUrl())
                .withString("userFirstName", status.getUser().getFirstName())
                .with("userLastName", status.getUser().getLastName())
        );
    }
}
