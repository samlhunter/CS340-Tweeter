package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

public interface IFollowDAO {

    FollowingResponse getFollowing (String username, String lastFollowee, int limit);
    GetFollowersResponse getFollowers (String username, String lastFollower, int limit);
    boolean getFollows(String currUser, String followee);
    void addFollowersBatch(List<String> users, String followTarget);
    void putFollows(User currUser, User userToFollow);
    void deleteFollows(String currUser, String userToUnfollow);
}
