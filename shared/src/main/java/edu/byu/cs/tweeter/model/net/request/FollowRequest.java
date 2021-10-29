package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest {
    private AuthToken authToken;
    private String follweeName;

    public FollowRequest() {}

    public FollowRequest(AuthToken authToken, String followeeName) {
        this.authToken = authToken;
        this.follweeName = followeeName;
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
}
