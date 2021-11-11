package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

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

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public AuthTokenDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
    }

    @Override
    public boolean validateAuthToken(AuthToken authToken) {
        // First check if the authToken has not expired
        // if not, then update the time stamp
        return false;
    }

    @Override
    public AuthToken createAuthToken() throws Exception {
        String token = generateNewToken();
        // Generate the datetime
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        // Create the object and add to table
        AuthToken authToken = new AuthToken(token, strDate);
        try {
            Table table = dynamoDB.getTable("authtoken");
            PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("authToken", token)
                                                  .with("datetime",strDate)
            );
        } catch (Exception e) {
            throw new Exception("Exception caught adding authToken to table");
        }
        // Return the object
        return authToken;
    }

    @Override
    public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
