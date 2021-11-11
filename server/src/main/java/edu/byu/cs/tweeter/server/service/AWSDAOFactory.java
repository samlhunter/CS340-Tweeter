package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class AWSDAOFactory extends DAOFactory{
    public AWSDAOFactory() {
        this.userDAO = new UserDAO();
        this.followDAO = new FollowDAO();
        this.statusDAO = new StatusDAO();
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
}
