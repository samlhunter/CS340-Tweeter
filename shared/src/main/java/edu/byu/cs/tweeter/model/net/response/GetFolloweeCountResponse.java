package edu.byu.cs.tweeter.model.net.response;

public class GetFolloweeCountResponse extends Response {
    private int count;

    public GetFolloweeCountResponse(String message) { super (false, message); }

    public GetFolloweeCountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
