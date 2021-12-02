package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAO implements IAuthTokenDAO{

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private Table table;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public AuthTokenDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.table = this.dynamoDB.getTable("authtoken");
    }

    @Override
    public AuthToken validateAuthToken (AuthToken authToken) throws Exception{
        //Date date = Calendar.getInstance().getTime();
        System.out.println("Validating authtoken: " +  authToken.getToken());
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", authToken.getToken());
        Item authItem = table.getItem(spec);
        System.out.println("Successfully got token: " + authItem.toString());
        String storedDateStr = authItem.getString("date_time");
        System.out.println("Got stored datetime: " + storedDateStr);
        long storedDateLong = Long.parseLong(storedDateStr);
        System.out.println("Stored date long: ");
        System.out.println(storedDateLong);
        //Get current time
        Date date = Calendar.getInstance().getTime();
        long currTimeLong = date.getTime();
        System.out.println("Curr date long: ");
        System.out.println(currTimeLong);
        long timeDiff = currTimeLong - storedDateLong;
        long minDiff = (timeDiff / 1000) / 60;
        System.out.println("Checking time difference");
        if (minDiff >= 10) {
            System.out.println("Authtoken is expired");
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey("token", authToken.getToken()));
            table.deleteItem(deleteItemSpec);
            throw new Exception();
        }
        else {
            // Update time and return true
            System.out.println("Now updating token");
            String currTimeStr = String.valueOf(currTimeLong);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("token", authToken.getToken())
                    .withUpdateExpression("set date_time = :date_time")
                    .withValueMap(new ValueMap().withString(":date_time", currTimeStr))
                    .withReturnValues(ReturnValue.UPDATED_NEW);
            try {
                UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            }
            catch (Exception e) {
                System.err.println("Unable to update Authtoken: " + authToken.getToken());
                System.err.println(e.getMessage());
            }
            return new AuthToken(authToken.getToken(), currTimeStr);
        }
    }

    @Override
    public AuthToken getAuthToken(AuthToken authToken) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", authToken.getToken());
        Item outcome = table.getItem(spec);

        return new AuthToken(outcome.getString("token"), outcome.getString("date_time"));
    }

    @Override
    public AuthToken createAuthToken() throws Exception {
        String token = generateNewToken();
        // Generate the datetime
        Date date = Calendar.getInstance().getTime();
        long currTimeLong = date.getTime();
        String currTimeStr = String.valueOf(currTimeLong);

        AuthToken authToken = new AuthToken(token, currTimeStr);
        try {
            PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("token", token)
                    .with("date_time", currTimeStr));
        }
        catch (Exception e) {
            throw new Exception("Exception in putAuthToken");
        }
        return authToken;
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("token", authToken.getToken()));

        try {
            System.out.println("Attempting authToken delete...");
            System.out.println("AuthToken token passed in: " + authToken);
            System.out.println("AuthToken token we are deleting: " + deleteItemSpec.toString());
            System.out.println("AuthToken passed in: " + authToken);
            System.out.println("AuthToken we are deleting: " + deleteItemSpec.toString());
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        } catch (Exception e) {
            System.err.println("Unable to delete authToken: " + authToken.getToken() + " " +
                    authToken.getDatetime());
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
