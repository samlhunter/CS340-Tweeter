package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

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
    private AuthTokenDAO authTokenDAO;
    private Table table;

    public UserDAO() {
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        this.dynamoDB = new DynamoDB(client);
        this.authTokenDAO = new AuthTokenDAO();
        this.table = this.dynamoDB.getTable("users");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Perform a login on using the database
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("username",request.getUsername());
            Item outcome = this.table.getItem(spec);
            String hashedPassword = outcome.getString("password");
            boolean passwordsMatch = validatePassword(request.getPassword(), hashedPassword);
            if (passwordsMatch) {
                AuthToken token = authTokenDAO.createAuthToken();
                User user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("username"), outcome.getString("image"));
                return new LoginResponse(user, token);
            }
            else {
                return new LoginResponse("incorrect password");
            }
        } catch (Exception e) {
            return new LoginResponse(e.getMessage());
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String hashedPassword = getHashedPassword(request.getPassword());
        try {
            // Lets add our user into the table
            PutItemOutcome outcome = this.table.putItem(new Item().withPrimaryKey("username", request.getUsername())
                    .with("password", hashedPassword)
                    .with("firstName", request.getFirstName())
                    .with("lastName", request.getLastName())
                    .with("image", request.getImage())
            );
            // If our outcome was successful, we will need to create a new user object and then generate an authToken
            User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());
            AuthToken authToken = authTokenDAO.createAuthToken();
            return new RegisterResponse(registeredUser, authToken);
        } catch (Exception e) {
            return new RegisterResponse(e.getMessage());
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return null;
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        try {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("username",request.getUserAlias());
            Item outcome = this.table.getItem(spec);
            User user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("username"), outcome.getString("image"));
            return new GetUserResponse(user);
        } catch (Exception e) {
            return new GetUserResponse(e.getMessage());
        }
    }

    private boolean validatePassword(String providedPassword, String storedPassword) {
        String providedHashed = getHashedPassword(providedPassword);
        return (providedHashed.equals(storedPassword));
    }

    private static String getHashedPassword(String password) {
        String salt = getSalt();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }
}
