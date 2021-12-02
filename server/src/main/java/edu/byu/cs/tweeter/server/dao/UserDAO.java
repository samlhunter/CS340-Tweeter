package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class UserDAO implements IUserDAO{

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table table;

    public UserDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.table = this.dynamoDB.getTable("users");
    }

    @Override
    public User getUser(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        Item userItem = this.table.getItem(spec);

        User user = new User(userItem.getString("firstName"),
                userItem.getString("lastName"),
                userItem.getString("username"),
                userItem.getString("image")
        );
        System.out.println("This is our user: " + user);
        return user;
    }

    public  String getUserPassword(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        Item userItem = this.table.getItem(spec);

        String password = userItem.getString("password");

        return password;
    }

    @Override
    public PutItemOutcome putUser(String firstName, String lastName, String username, String password, String image) {
        PutItemOutcome outcome = this.table.putItem(new Item()
        .withPrimaryKey("username", username)
        .with("password", password)
        .with("firstName", firstName)
        .with("lastName", lastName)
        .with("image", image)
        .with("followerCount", 0)
        .with("followingCount",0));
        return outcome;
    }

    @Override
    public int getFollowerCount(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        Item outcome = table.getItem(spec);
        int followerCount = outcome.getInt("followerCount");
        return followerCount;
    }

    @Override
    public int getFollowingCount(String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", username);
        Item outcome = table.getItem(spec);
        int followingCount = outcome.getInt("followingCount");
        return followingCount;
    }

    @Override
    public void incrementFollowerCount(String username) {
        int numFollowers = getFollowerCount(username);
        numFollowers += 1;
        System.out.println("Updating follower count, new count: " + numFollowers);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set followerCount = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("Successfully incremented follower count!");
        }
        catch (Exception e) {
            System.err.println("Unable to increment follower_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void incrementFollowingCount(String username) {
        int numFollowers = getFollowingCount(username);
        numFollowers += 1;

        System.out.println("Updating following count, new count: " + numFollowers);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set followingCount = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("Successfully incremented following count!");
        }
        catch (Exception e) {
            System.err.println("Unable to increment following_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void decrementFollowerCount(String username) {
        int numFollowers = getFollowerCount(username);
        numFollowers -= 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set followerCount = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Decrementing follower_count...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("decrementFollowerCount succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to decrement follower_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void decrementFollowingCount(String username) {
        int numFollowers = getFollowerCount(username);
        numFollowers -= 1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username",
                username)
                .withUpdateExpression("set followingCount = :count")
                .withValueMap(new ValueMap().withNumber(":count", numFollowers))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

        }
        catch (Exception e) {
            System.err.println("Unable to decrement following_count: " + username);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void addUserBatch(List<UserDTO> users) {
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems("users");

        // Add each user into the TableWriteItems object
        for (UserDTO user : users) {
            Item item = new Item()
                    .withPrimaryKey("username", user.getAlias())
                    .withString("name", user.getName());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("users");
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
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
