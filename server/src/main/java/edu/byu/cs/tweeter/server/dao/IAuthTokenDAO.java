package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {
    AuthToken validateAuthToken(AuthToken authToken) throws Exception;
    AuthToken createAuthToken() throws Exception;
    AuthToken getAuthToken(AuthToken authToken);
    String generateNewToken();
    void deleteAuthToken(AuthToken authToken);
}
