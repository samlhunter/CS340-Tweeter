package edu.byu.cs.tweeter.server.dao;

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
    int getFolloweeCount(AuthToken authToken, String userName);
    int getFollowingCount(AuthToken authToken, String userName);
    boolean getIsFollower(AuthToken authToken, User follower, User followee);
    FollowingResponse getFollowees(FollowingRequest request);
    GetFollowersResponse getFollowers(GetFollowersRequest request);
    FollowResponse followUser(FollowRequest request);
    UnfollowResponse unfollowUser(UnfollowRequest request);
}
