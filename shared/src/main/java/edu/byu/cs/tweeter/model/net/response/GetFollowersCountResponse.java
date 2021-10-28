package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersCountResponse extends Response {
    private int count;

    public GetFollowersCountResponse(String message) { super (false, message); }

    public GetFollowersCountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
