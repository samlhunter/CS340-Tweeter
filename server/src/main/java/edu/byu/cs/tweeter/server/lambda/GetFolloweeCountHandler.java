package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFolloweeCountHandler implements RequestHandler<GetFolloweeCountRequest, GetFolloweeCountResponse> {
    @Override
    public GetFolloweeCountResponse handleRequest(GetFolloweeCountRequest getFolloweeCountRequest, Context context) {
        FollowService followService = new FollowService();
        return followService.getFolloweeCount(getFolloweeCountRequest.getTargetUser());
    }
}
