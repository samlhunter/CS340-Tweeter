package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        GetFollowingCountRequest getFollowingCountRequest = new GetFollowingCountRequest (getTargetUser());
        try {
            GetFollowing = new ServerFacade().login(loginRequest, "/getFollowingCount");
            if (loginResponse.isSuccess()) {
                return new Pair<>(loginResponse.getUser(), loginResponse.getAuthToken());
            }
            else {
                sendFailedMessage(loginResponse.getMessage());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
