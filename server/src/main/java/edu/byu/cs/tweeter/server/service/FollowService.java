package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

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
        FollowResponse response = new FollowResponse();
        return response;
    }

    public UnfollowResponse unfollowUser(UnfollowRequest request) {
        UnfollowResponse response = new UnfollowResponse();
        return response;
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        return getFollowingDAO().getFollowees(request);
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        return getFollowingDAO().getFollowers(request);
    }

    public GetFollowersCountResponse getFollowingCount(AuthToken authToken, String userName) {
        GetFollowersCountResponse response = new GetFollowersCountResponse(getFollowingDAO().getFollowingCount(authToken, userName));
        return response;
    }

    public GetFolloweeCountResponse getFolloweeCount(AuthToken authToken, String userName) {
        GetFolloweeCountResponse response = new GetFolloweeCountResponse(getFollowingDAO().getFolloweeCount(authToken, userName));
        return response;
    }
    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }
}
