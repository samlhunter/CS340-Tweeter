package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    DAOFactory factory;

    public StatusService () {
        this.factory = new AWSDAOFactory();
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        return GetStatusDAO().getFeed(request);
//        return GetStatusDAO().getFeed(request);
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        return GetStatusDAO().getStory(request);
//        return GetStatusDAO().getStory(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return GetStatusDAO().postStatus(request);
//        return new PostStatusResponse();
    }

    private IStatusDAO GetStatusDAO() {
        return factory.getStatusDAO();
    }
}
