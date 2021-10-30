package edu.byu.cs.tweeter.client.model.net;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetFolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    // TODO: Set this to the invoke URL of your API. Find it by going to your API in AWS, clicking
    //  on stages in the right-side menu, and clicking on the stage you deployed your API to.
    private static final String SERVER_URL = "https://mwt3fx47h2.execute-api.us-west-2.amazonaws.com/TweeterDev";

    private final ClientCommunicator clientCommunicator = new ClientCommunicator(SERVER_URL);

    /**
     * Performs a login and if successful, returns the logged in user and an auth token.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LoginResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        LoginResponse response = clientCommunicator.doPost(urlPath, request, null, LoginResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest request, String urlPath) throws IOException, TweeterRemoteException {
        LogoutResponse response = clientCommunicator.doPost(urlPath, request, null, LogoutResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request, String urlPath) throws IOException, TweeterRemoteException {
        RegisterResponse response = clientCommunicator.doPost(urlPath, request, null, RegisterResponse.class);
        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public FollowResponse followUser(FollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        FollowResponse response = clientCommunicator.doPost(urlPath, request, null, FollowResponse.class);
        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public UnfollowResponse unfollowUser(UnfollowRequest request, String urlPath) throws IOException, TweeterRemoteException {
        UnfollowResponse response = clientCommunicator.doPost(urlPath, request, null, UnfollowResponse.class);
        if (response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetFollowersCountResponse response = clientCommunicator.doPost(urlPath, request, null, GetFollowersCountResponse.class);
        if(response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetFolloweeCountResponse getFolloweeCount(GetFolloweeCountRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetFolloweeCountResponse response = clientCommunicator.doPost(urlPath, request, null, GetFolloweeCountResponse.class);
        if(response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request, String urlPath)
            throws IOException, TweeterRemoteException {

        FollowingResponse response = clientCommunicator.doPost(urlPath, request, null, FollowingResponse.class);

        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetFollowersResponse response = clientCommunicator.doPost(urlPath, request, null, GetFollowersResponse.class);
        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public IsFollowerResponse getIsFollower(IsFollowerRequest request, String urlPath) throws IOException, TweeterRemoteException{
        IsFollowerResponse response = clientCommunicator.doPost(urlPath, request, null, IsFollowerResponse.class);
        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetUserResponse getUser(GetUserRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetUserResponse response = clientCommunicator.doPost(urlPath, request, null, GetUserResponse.class);
        if(response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request, String urlPath)
        throws IOException, TweeterRemoteException {

        PostStatusResponse response = clientCommunicator.doPost(urlPath, request, null, PostStatusResponse.class);

        if(response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetFeedResponse getFeed(GetFeedRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetFeedResponse response = clientCommunicator.doPost(urlPath, request, null, GetFeedResponse.class);

        if(response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }

    public GetStoryResponse getStory(GetStoryRequest request, String urlPath) throws IOException, TweeterRemoteException {
        GetStoryResponse response = clientCommunicator.doPost(urlPath, request, null, GetStoryResponse.class);
        if(response.isSuccess()) {
            return response;
        }
        else {
            throw new RuntimeException(response.getMessage());
        }
    }
}