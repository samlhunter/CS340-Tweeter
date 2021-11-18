package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements IFollowDAO{
    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table table;

    public FollowDAO () {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.table = this.dynamoDB.getTable("follows");
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }

    @Override
    public FollowingResponse getFollowing(String username, String lastFolloweeAlias, int limit) {
        List<User> followees = new ArrayList<>();
        FollowingResponse response = null;

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#handle", "follower_handle");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":username", new AttributeValue().withS(username));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(limit);

        QueryRequest checkForMoreRequest = new QueryRequest()
                .withTableName("follows")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(limit+1);

        if (lastFolloweeAlias != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("follower_handle", new AttributeValue().withS(username));
            startKey.put("followee_handle", new AttributeValue().withS(lastFolloweeAlias));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
            checkForMoreRequest = checkForMoreRequest.withExclusiveStartKey(startKey);
        }

        QueryResult queryResult = client.query(queryRequest);
        QueryResult checkResult = client.query(checkForMoreRequest);
        List<Map<String, AttributeValue>> countItems = checkResult.getItems();
        List<Map<String, AttributeValue>> items = queryResult.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items){
                User user = new User(item.get("followeeFirstName").getS(),
                        item.get("followeeLastName").getS(),
                        item.get("followee_handle").getS(),
                        item.get("followeeURL").getS()
                );
                followees.add(user);
            }
        }

        if (countItems.size() > items.size()) {
            response = new FollowingResponse(followees, true);
        } else {
            response = new FollowingResponse(followees, false);
        }

        return response;
    }

    @Override
    public GetFollowersResponse getFollowers(String username, String lastFollowerAlias, int limit) {
        List<User> followers = new ArrayList<>();
        GetFollowersResponse response = null;

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#handle", "followee_handle");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":username", new AttributeValue().withS(username));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withIndexName("follows_index")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName("follows")
                .withIndexName("follows_index")
                .withKeyConditionExpression("#handle = :username")
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastFollowerAlias != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("followee_handle", new AttributeValue().withS(username));
            lastKey.put("follower_handle", new AttributeValue().withS(lastFollowerAlias));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                User user = new User(item.get("followerFirstName").getS(),
                        item.get("followerLastName").getS(),
                        item.get("follower_handle").getS(),
                        item.get("followerURL").getS()
                );
                followers.add(user);
            }
        }

        if (checkItems.size() > items.size()) {
            return new GetFollowersResponse(followers, true);
        }
        else {
            System.out.println("We have no more followers to get");
            return new GetFollowersResponse(followers, false);
        }
    }

    @Override
    public boolean isFollower(String currUser, String userToUnfollow) {
        return false;
    }

    @Override
    public void putFollows(User currUser, User toFollow) {
        System.out.println("User who is following: " + currUser);
        System.out.println("User being followed: " + toFollow);
        PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("follower_handle", currUser.getAlias(), "followee_handle", toFollow.getAlias())
                     .withString("followerFirstName", currUser.getFirstName())
                     .withString("followerLastName", currUser.getLastName())
                     .withString("foloweeFirstName", toFollow.getFirstName())
                     .withString("followeeLastName", toFollow.getLastName())
                     .withString("followerURL", currUser.getImageUrl())
                     .withString("followeeURL", toFollow.getImageUrl())
                     );
    }

    @Override
    public void deleteFollows(String currUser, String toUnfollow) {
        DeleteItemOutcome outcome = table.deleteItem("follower_handle", currUser, "followee_handle", toUnfollow);
    }
}
