package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
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

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
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
        .with("image", image));
        return outcome;
    }
}
