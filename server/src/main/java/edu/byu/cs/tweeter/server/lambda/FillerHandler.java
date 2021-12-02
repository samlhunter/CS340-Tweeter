package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FillRequest;
import edu.byu.cs.tweeter.model.net.response.FillResponse;
import edu.byu.cs.tweeter.server.service.Filler;
import edu.byu.cs.tweeter.server.service.FollowService;

public class FillerHandler implements RequestHandler<FillRequest, FillResponse> {

    @Override
    public FillResponse handleRequest(FillRequest request, Context context) {
        Filler filler = new Filler();
        filler.fillDatabase();
        return new FillResponse(true);
    }
}
