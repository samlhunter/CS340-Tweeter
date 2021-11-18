package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserService {
    DAOFactory factory;

    public UserService() {
        this.factory = new AWSDAOFactory();
    }

    public LoginResponse login(LoginRequest request) {
        try {
            User loggedInUser = factory.getUserDAO().getUser(request.getUsername());
            String storedPassword = factory.getUserDAO().getUserPassword(request.getUsername());
            if (validatePasswords(request.getPassword(), storedPassword, request.getUsername())) {
                AuthToken authToken = factory.getAuthTokenDAO().createAuthToken();
                return new LoginResponse(loggedInUser, authToken);
            } else {
                return new LoginResponse("Incorrect Password");
            }
        } catch (Exception e) {
            return new LoginResponse(e.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        String securePassword = getHashedPassword(request.getPassword(), request.getUsername());
        try {
            String url = setS3ImageFile(request);

            PutItemOutcome outcome = factory.getUserDAO().putUser(request.getFirstName(),
                    request.getLastName(), request.getUsername(), securePassword, url);
            AuthToken authToken = factory.getAuthTokenDAO().createAuthToken();
            User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), url);
            return new RegisterResponse(registeredUser, authToken);

        } catch (Exception e) {
            return new RegisterResponse(e.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        // Will need to delete authToken
        return new LogoutResponse();
    }

    public GetUserResponse getUser(GetUserRequest request) {
        assert request.getUserAlias() != null;
        try {
            return new GetUserResponse(factory.getUserDAO().getUser(request.getUserAlias()));
        } catch (Exception e) {
            return new GetUserResponse(e.getMessage());
        }
    }

    private String getHashedPassword(String password, String username) {
        System.out.println("In get hashed password. Password: " + password + " username: " + username);
        String salt = username;
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

    private String setS3ImageFile(RegisterRequest request) throws IOException {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
        String bucket = "cs340samlh98";
        String fileName = request.getUsername() + "-image.png";
        byte[] imageBytes = Base64.getDecoder().decode(request.getImage());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, byteArrayInputStream, new ObjectMetadata())
                .withCannedAcl(CannedAccessControlList.PublicReadWrite);
        s3.putObject(putObjectRequest);
        String imageUrl = s3.getUrl(bucket, fileName).toString();
        return imageUrl;

    }

    private boolean validatePasswords(String givenPassword, String storedPassword, String username) {
        String givenHashed = getHashedPassword(givenPassword, username);
        return givenHashed.equals(storedPassword);
    }
}
