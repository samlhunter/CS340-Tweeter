package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        GetFolloweeCountRequest getFolloweeCountRequest = new GetFolloweeCountRequest(authToken, getTargetUser().getAlias());
        try {
            GetFolloweeCountResponse getFolloweeCountResponse= new ServerFacade().getFolloweeCount(getFolloweeCountRequest, "/getfolloweecount");
            if (getFolloweeCountResponse.isSuccess()) {
                return (getFolloweeCountResponse.getCount());
            }
            else {
                sendFailedMessage(getFolloweeCountResponse.getMessage());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
        return 0;
    }
}
