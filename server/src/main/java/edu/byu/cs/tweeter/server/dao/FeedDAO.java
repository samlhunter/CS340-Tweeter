package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;

public class FeedDAO implements IFeedDAO{

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table table;

    public FeedDAO () {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.table = this.dynamoDB.getTable("feed");
    }

    @Override
    public GetFeedResponse getFeed(String username, String lastStatusDateTime, int limit) {
        List<Status> feed = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#username", "username");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(username));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("feed")
                .withKeyConditionExpression("#username = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("feed")
                .withKeyConditionExpression("#username = :username")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastStatusDateTime != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("username", new AttributeValue().withS(username));
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
                feed.add(status);
            }
        }

        System.out.println("Successfully got feed");
        System.out.println(feed);
        if (checkItems.size() > items.size()) {
            return new GetFeedResponse(feed, true);
        }
        else {
            System.out.println("We have no more statuses to get");
            return new GetFeedResponse(feed, false);
        }
    }

    @Override
    public void putFeed(Status status, List<UserDTO> followers) {
        TableWriteItems items = new TableWriteItems("feed");

        for (UserDTO user: followers) {
            HashSet<String> mentionsSet = new HashSet<>(status.getMentions());
            HashSet<String> urlsSet = new HashSet<>(status.getUrls());
            mentionsSet.add("");
            urlsSet.add("");
            Item item = new Item().withPrimaryKey("username", user.getAlias(), "datetime", status.getDate())
                    .withString("authorUsername", status.getUser().getAlias())
                    .withString("message", status.getPost())
                    .withStringSet("mentions", mentionsSet)
                    .withStringSet("urls", urlsSet)
                    .withString("imageURL", status.getUser().getImageUrl())
                    .withString("userFirstName", status.getUser().getFirstName())
                    .with("userLastName", status.getUser().getLastName());
            items.addItemToPut(item);
            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("feed");
            }

        }
    }

    private void loopBatchWrite(TableWriteItems items) {
        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Users");
        }
    }
}
