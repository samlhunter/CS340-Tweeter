package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        GetFollowersCountRequest getFollowersCountRequest = new GetFollowersCountRequest(getTargetUser());
        try {
            GetFollowersCountResponse getFollowersCountResponse= new ServerFacade().getFollowersCount(getFollowersCountRequest, "/getFollowersCount");
            if (getFollowersCountResponse.isSuccess()) {
                return (getFollowersCountResponse.getCount());
            }
            else {
                sendFailedMessage(getFollowersCountResponse.getMessage());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
        return 0;
    }
}
