package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    public GetFeedResponse getFeed(GetFeedRequest request) {
        return GetStatusDAO().getFeed(request);
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        return GetStatusDAO().getStory(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }

    private StatusDAO GetStatusDAO() {
        return new StatusDAO();
    }
}
