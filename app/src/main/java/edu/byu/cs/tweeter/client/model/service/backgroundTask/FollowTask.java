package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthorizedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;
    private final String currUsername;

    public FollowTask(AuthToken authToken, String currUsername, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currUsername = currUsername;
    }

    @Override
    protected void runTask() {
        FollowRequest followRequest = new FollowRequest(authToken,currUsername, followee.getAlias());

        try {
            FollowResponse followResponse = new ServerFacade().followUser(followRequest, "/follow");
            if (followResponse.isSuccess()) {
                return;
            }
            else {
                sendFailedMessage(followResponse.getMessage());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
