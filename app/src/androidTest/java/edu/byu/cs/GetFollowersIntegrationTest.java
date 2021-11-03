package edu.byu.cs;

import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;

public class GetFollowersIntegrationTest {
    ServerFacade serverFacade;

    @Before
    public void setup() {
        this.serverFacade = new ServerFacade();
    }

    @Test
    public void GetFollowersSucceeded() {
        GetFollowersRequest request = new GetFollowersRequest(new AuthToken(),"a", 20, "a");
        String urlPath = "/getfollowers";
        try {
            GetFollowersResponse response = this.serverFacade.getFollowers(request, urlPath);
            assert (response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }

    @Test
    public void GetFollowersFailed() {
        GetFollowersRequest request = new GetFollowersRequest(new AuthToken(),"a", 20, "a");
        String urlPath = "/dummy";
        try {
            GetFollowersResponse response = this.serverFacade.getFollowers(request, urlPath);
            assert (!response.isSuccess());
        } catch (Exception e) {
            System.out.println("Exception caught!");
        }
    }
}
