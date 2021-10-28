package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersCountRequest {
    private User targetUser;

    public GetFollowersCountRequest() {}

    public GetFollowersCountRequest(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }
}