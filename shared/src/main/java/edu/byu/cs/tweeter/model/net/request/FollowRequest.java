package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest {
    private AuthToken authToken;
    private String follweeName;
    private String currUsername;

    public FollowRequest() {}


    public FollowRequest(AuthToken authToken, String currUsername, String followeeName) {
        this.authToken = authToken;
        this.follweeName = followeeName;
        this.currUsername = currUsername;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFollweeName() {
        return follweeName;
    }

    public void setFollweeName(String follweeName) {
        this.follweeName = follweeName;
    }

    public String getCurrUsername() {
        return currUsername;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }
}
