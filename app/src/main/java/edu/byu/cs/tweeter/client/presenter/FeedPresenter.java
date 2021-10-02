package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements StatusService.GetFeedObserver {

    public interface View {
        void addItems(List<Status> statuses);
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void setLoading(boolean value) throws MalformedURLException;
    }

    private static final int PAGE_SIZE = 10;

    private FeedPresenter.View view;
    private User targetUser;
    private AuthToken authToken;
    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FeedPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems() throws MalformedURLException{
        if(!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);
            new StatusService().getFeed(authToken, targetUser, PAGE_SIZE, lastStatus, this);
        }
    }

    public void gotoUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, new FeedPresenter.UserObserver());
    }

    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        try {
            view.setLoading(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        this.lastStatus = statuses.get(statuses.size() - 1);
    }

    @Override
    public void failed(String message) {
        view.displayErrorMessage("Get feed failed: " + message);
    }

    @Override
    public void exceptionThrown(Exception ex) {
        view.displayErrorMessage("Get feed threw exception: " + ex.getMessage());
    }

    private class UserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSucceeded(User user) {
            view.navigateToUser(user);
        }

        @Override
        public void failed(String message) {
            view.displayErrorMessage(message);
        }

        @Override
        public void exceptionThrown(Exception ex) {
            view.displayErrorMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }
}