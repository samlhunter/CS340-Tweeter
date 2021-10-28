package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFolloweeCountRequest {
    private User targetUser;

    public GetFolloweeCountRequest() {}

    public GetFolloweeCountRequest(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }
}
