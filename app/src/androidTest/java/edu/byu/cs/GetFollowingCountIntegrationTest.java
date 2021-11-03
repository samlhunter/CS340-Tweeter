package edu.byu.cs;

import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFolloweeCountResponse;

public class GetFollowingCountIntegrationTest {
    ServerFacade serverFacade;

    @Before
    public void setup() {
        this.serverFacade = new ServerFacade();
    }

    @Test
    public void GetFollowingCountSucceeded() {
        GetFolloweeCountRequest request = new GetFolloweeCountRequest(new AuthToken(), "a");
        String urlPath = "/getfolloweecount";
        try {
            GetFolloweeCountResponse response = this.serverFacade.getFolloweeCount(request, urlPath);
            assert (response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }

    @Test
    public void GetFollowingCountFailed() {
        GetFolloweeCountRequest request = new GetFolloweeCountRequest(new AuthToken(), "a");
        String urlPath = "/dummy";
        try {
            GetFolloweeCountResponse response = this.serverFacade.getFolloweeCount(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }
}

