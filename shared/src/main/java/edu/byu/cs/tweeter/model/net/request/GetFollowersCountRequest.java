package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowersCountRequest {
    private AuthToken authToken;
    private String userName;

    public GetFollowersCountRequest() {}

    public GetFollowersCountRequest(AuthToken authToken, String userName) {
        this.authToken = authToken;
        this.userName = userName;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
