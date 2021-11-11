package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public abstract class DAOFactory {
    protected UserDAO userDAO;
    protected FollowDAO followDAO;
    protected StatusDAO statusDAO;

    public abstract UserDAO getUserDAO();

    public abstract FollowDAO getFollowDAO();

    public abstract StatusDAO getStatusDAO();
}
