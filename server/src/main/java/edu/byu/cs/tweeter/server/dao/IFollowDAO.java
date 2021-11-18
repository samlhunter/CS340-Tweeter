package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.util.List;

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

public interface IFollowDAO {
//    int getFolloweeCount(AuthToken authToken, String userName);
//    int getFollowingCount(AuthToken authToken, String userName);
//    boolean getIsFollower(AuthToken authToken, User follower, User followee);
//    FollowingResponse getFollowees(FollowingRequest request);
//    GetFollowersResponse getFollowers(GetFollowersRequest request);
//    FollowResponse followUser(FollowRequest request);
//    UnfollowResponse unfollowUser(UnfollowRequest request);

    FollowingResponse getFollowing (String username, String lastFollowee, int limit);
    GetFollowersResponse getFollowers (String username, String lastFollower, int limit);
    boolean isFollower(String currUser, String userToUnfollow);
    void putFollows(User currUser, User userToFollow);
    void deleteFollows(String currUser, String userToUnfollow);
}
