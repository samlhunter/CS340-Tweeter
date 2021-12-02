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
            if(authenticate(request.getAuthToken())) {
                User currUser = factory.getUserDAO().getUser(request.getCurrUsername());
                User userToFollow = factory.getUserDAO().getUser(request.getFollweeName());
                factory.getFollowDAO().putFollows(currUser, userToFollow);
                factory.getUserDAO().incrementFollowerCount(request.getFollweeName());
                factory.getUserDAO().incrementFollowingCount(request.getCurrUsername());
                return new FollowResponse();
            }
            else {
                return new FollowResponse("Session expired, login again");
            }
        } catch (Exception e) {
            return new FollowResponse(e.getMessage());
        }
    }

    public UnfollowResponse unfollowUser(UnfollowRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            try {
                factory.getFollowDAO().deleteFollows(request.getCurrUsername(), request.getUnfollweeName());
                factory.getUserDAO().decrementFollowingCount(request.getCurrUsername());
                factory.getUserDAO().decrementFollowerCount(request.getUnfollweeName());
                return new UnfollowResponse();
            } catch (Exception e) {
                return new UnfollowResponse(e.getMessage());
            }
        }
        else {
            return new UnfollowResponse("Session expired! Login again");
        }
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            return factory.getFollowDAO().getFollowing(request.getFollowerAlias(), request.getLastFolloweeAlias(), request.getLimit());
        }
        else {
            return new FollowingResponse("Session expired! Login again");
        }
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            return factory.getFollowDAO().getFollowers(request.getUserAlias(), request.getLastFollowerAlias(), request.getLimit());
        }
        else {
            return new GetFollowersResponse("Session expired! Login again");
        }
    }

    public GetFollowersCountResponse getFollowingCount(AuthToken authToken, String username) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(authToken));
        if (authenticated) {
            return new GetFollowersCountResponse(factory.getUserDAO().getFollowerCount(username));
        }
        else {
            return new GetFollowersCountResponse("Session expired! Login again");
        }
    }

    public GetFolloweeCountResponse getFolloweeCount(AuthToken authToken, String username) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(authToken));
        if (authenticated) {
            return new GetFolloweeCountResponse(factory.getUserDAO().getFollowingCount(username));
        }
        else {
            return new GetFolloweeCountResponse("Session expired! Login again");
        }
    }

    public IsFollowerResponse isFollower(AuthToken authToken, User follower, User followee) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(authToken));
        if (authenticated) {
            System.out.println("Successfully authenticated, checking is follower");
            assert follower.getAlias() != null;
            assert followee.getAlias() != null;
            boolean isFollower = false;

            isFollower = factory.getFollowDAO().getFollows(follower.getAlias(),
                    followee.getAlias());
            if (isFollower) {
                System.out.println("Got a successful response");
                return new IsFollowerResponse(true);
            } else {
                return new IsFollowerResponse("Not a follower");
            }
        }
        else {
            return new IsFollowerResponse("Session expired");
        }
    }

    private boolean authenticate(AuthToken authToken) {
        try {
            this.factory.getAuthTokenDAO().validateAuthToken(authToken);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
