package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public abstract class DAOFactory {

    public abstract IUserDAO getUserDAO();

    public abstract IFollowDAO getFollowDAO();

    public abstract IStatusDAO getStatusDAO();

    public abstract IAuthTokenDAO getAuthTokenDAO();

    public abstract IStoryDAO getStoryDAO();

    public abstract IFeedDAO getFeedDAO();
}
