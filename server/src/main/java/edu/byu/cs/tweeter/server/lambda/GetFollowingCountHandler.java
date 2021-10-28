package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, Integer> {
    @Override
    public Integer handleRequest(GetFollowingCountRequest getFollowingCountRequest, Context context) {
        FollowService followService = new FollowService();
        return followService.getFollowingCount(getFollowingCountRequest.getTargetUser());
    }
}
