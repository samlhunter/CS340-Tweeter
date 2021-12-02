package edu.byu.cs.tweeter.model.net.request;
import edu.byu.cs.tweeter.model.domain.AuthToken;
public class LogoutRequest {
    private AuthToken authtoken;

    private LogoutRequest() {}

    public LogoutRequest(AuthToken authToken) {
        this.authtoken = authToken;
    }

    public AuthToken getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(AuthToken authtoken) {
        this.authtoken = authtoken;
    }
}
