package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;




public class AWSDAOFactory extends DAOFactory{
    UserDAO userDAO;
    FollowDAO followDAO;
    StatusDAO statusDAO;
    AuthTokenDAO authTokenDAO;
    StoryDAO storyDAO;
    FeedDAO feedDAO;

    public AWSDAOFactory() {
        this.userDAO = new UserDAO();
        this.followDAO = new FollowDAO();
        this.statusDAO = new StatusDAO();
        this.authTokenDAO = new AuthTokenDAO();
        this.storyDAO = new StoryDAO();
        this.feedDAO = new FeedDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public FollowDAO getFollowDAO() {
        return followDAO;
    }

    @Override
    public StatusDAO getStatusDAO() {
        return statusDAO;
    }

    @Override
    public IAuthTokenDAO getAuthTokenDAO() { return authTokenDAO; }

    @Override
    public IStoryDAO getStoryDAO() { return storyDAO; }

    @Override
    public IFeedDAO getFeedDAO() { return feedDAO; }
}
