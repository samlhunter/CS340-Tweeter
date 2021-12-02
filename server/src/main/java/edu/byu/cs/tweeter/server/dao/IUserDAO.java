package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import java.util.List;

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

public interface IUserDAO {
    int getFollowerCount(String username);
    int getFollowingCount(String username);
    void incrementFollowerCount(String username);
    void incrementFollowingCount(String username);
    void decrementFollowerCount(String username);
    void decrementFollowingCount(String username);
    void addUserBatch(List<UserDTO> users);
    User getUser(String username);
    String getUserPassword(String username);
    PutItemOutcome putUser(String firstname, String lastName, String username, String password, String image);
}
