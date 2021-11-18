package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthorizedTask {

    /**
     * The user that is being followed.
     */
    private final User followee;
    private String currUsername;

    public UnfollowTask(AuthToken authToken,String currUsername, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.currUsername = currUsername;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        UnfollowRequest unfollowRequest = new UnfollowRequest(authToken, currUsername, followee.getAlias());

        try {
            UnfollowResponse unfollowResponse = new ServerFacade().unfollowUser(unfollowRequest, "/unfollow");
            if (unfollowResponse.isSuccess()) {
                return;
            }
            else {
                sendFailedMessage(unfollowResponse.getMessage());
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
