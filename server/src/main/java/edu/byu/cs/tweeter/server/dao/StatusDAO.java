package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusDAO implements IStatusDAO {
    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusesIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);

                for (int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex < allStatuses.size();
            }
        }
        return new GetFeedResponse(responseStatuses, hasMorePages);
    }

    @Override
    public GetStoryResponse getStory(GetStoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusesIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);

                for (int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex < allStatuses.size();
            }
        }
        return new GetStoryResponse(responseStatuses, hasMorePages);
    }

    @Override
    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }

    private int getStatusesStartingIndex(Status lastStatus, List<Status> allStatuses) {
        int statusIndex = 0;

        if(lastStatus != null) {
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    statusIndex = i+1;
                    break;
                }
            }
        }
        return statusIndex;
    }

    private List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }

    private FakeData getFakeData() { return new FakeData(); }
}
