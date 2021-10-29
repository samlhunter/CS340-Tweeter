package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthorizedTask {

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() {
        PostStatusRequest postStatusRequest = new PostStatusRequest(authToken, status);
        try {
            PostStatusResponse postStatusResponse = new ServerFacade().postStatus(postStatusRequest, "/poststatus");
            if (postStatusResponse.isSuccess()) {
                return;
            }
            else {
                sendFailedMessage(postStatusResponse.getMessage());
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
