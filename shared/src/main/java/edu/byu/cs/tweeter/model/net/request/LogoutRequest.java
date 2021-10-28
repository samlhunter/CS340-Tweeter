package edu.byu.cs.tweeter.model.net.request;
import edu.byu.cs.tweeter.model.domain.AuthToken;
public class LogoutRequest {
    private AuthToken authtoken;
    private String userName;

    private LogoutRequest() {}

    public LogoutRequest(AuthToken authToken, String userName) {
        this.authtoken = authToken;
        this.userName = userName;
    }
}
