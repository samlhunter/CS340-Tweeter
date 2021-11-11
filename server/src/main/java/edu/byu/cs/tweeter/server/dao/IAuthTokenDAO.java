package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {
    boolean validateAuthToken(AuthToken authToken);
    AuthToken createAuthToken() throws Exception;
    String generateNewToken();
}
