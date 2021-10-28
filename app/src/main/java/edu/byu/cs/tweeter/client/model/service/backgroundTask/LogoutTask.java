package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthorizedTask {

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        try {
            LogoutResponse logoutResponse = new ServerFacade().logout(logoutRequest, "/logout");
            if (logoutResponse.isSuccess()) {
                return;
            }
            else {
                sendFailedMessage(logoutResponse.getMessage());
            }
        } catch (Exception e){
            sendExceptionMessage(e);
        }
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
