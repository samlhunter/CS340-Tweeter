package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {
    private AuthToken authToken;
    private String unfollweeName;

    public UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String unfolloweeName) {
        this.authToken = authToken;
        this.unfollweeName = unfolloweeName;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUnfollweeName() {
        return unfollweeName;
    }

    public void setUnfollweeName(String unfollweeName) {
        this.unfollweeName = unfollweeName;
    }
}
