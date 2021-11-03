package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        GetStoryRequest getStoryRequest = new GetStoryRequest(authToken, getTargetUser().getAlias(), getLimit(), getLastItem());

        try {
            GetStoryResponse getStoryResponse = new ServerFacade().getStory(getStoryRequest, "/getstory");
            if (getStoryResponse.isSuccess()) {
                return new Pair<>(getStoryResponse.getStatuses(), getStoryResponse.getHasMorePages());
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }
        return null;
    }
}
