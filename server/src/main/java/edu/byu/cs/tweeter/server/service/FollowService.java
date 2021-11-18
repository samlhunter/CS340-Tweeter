package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    DAOFactory factory;

    public FollowService() {
        this.factory = new AWSDAOFactory();
    }
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowResponse followUser(FollowRequest request) {
        try {
            User currUser = factory.getUserDAO().getUser(request.getCurrUsername());
            User userToFollow = factory.getUserDAO().getUser(request.getFollweeName());
            factory.getFollowDAO().putFollows(currUser, userToFollow);
            //TODO: Make sure to update counts
            return new FollowResponse();
        } catch (Exception e) {
            return new FollowResponse(e.getMessage());
        }
    }

    public UnfollowResponse unfollowUser(UnfollowRequest request) {
        try {
            factory.getFollowDAO().deleteFollows(request.getCurrUsername(), request.getUnfollweeName());
            //TODO: Make sure to update counts
            return new UnfollowResponse();
        } catch (Exception e) {
            return new UnfollowResponse(e.getMessage());
        }
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        return factory.getFollowDAO().getFollowing(request.getFollowerAlias(), request.getLastFolloweeAlias(), request.getLimit());
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        return factory.getFollowDAO().getFollowers(request.getUserAlias(), request.getLastFollowerAlias(), request.getLimit());
    }

    public GetFollowersCountResponse getFollowingCount(AuthToken authToken, String userName) {
//        return GetFollowingDAO().getFollowingCount(authToken, userName);
        //GetFollowersCountResponse response = new GetFollowersCountResponse(GetFollowingDAO().getFollowingCount(authToken, userName));
        //return response;
        return null;
    }

    public GetFolloweeCountResponse getFolloweeCount(AuthToken authToken, String userName) {
//        return GetFollowingDAO().getFolloweeCount(authToken, userName);
//        GetFolloweeCountResponse response = new GetFolloweeCountResponse(GetFollowingDAO().getFolloweeCount(authToken, userName));
//        return response;
        return null;
    }

    public IsFollowerResponse isFollower(AuthToken authToken, User follower, User followee) {
//        return GetFollowingDAO().getIsFollower(authToken, follower, followee);
//        IsFollowerResponse response = new IsFollowerResponse(GetFollowingDAO().getIsFollower(authToken, follower, followee));
//        return response;
        return null;
    }
    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    private IFollowDAO GetFollowingDAO() {
        return factory.getFollowDAO();
    }
}
