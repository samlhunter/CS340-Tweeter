package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StatusService.GetStoryObserver{

    public interface View {
        void addItems(List<Status> statuses);
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void setLoading(boolean value);
    }

    private static final int PAGE_SIZE = 10;

    private StoryPresenter.View view;
    private User targetUser;
    private AuthToken authToken;
    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public StoryPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems() {
        if(!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);
            new StatusService().getStory(authToken, targetUser, PAGE_SIZE, lastStatus, this);
        }
    }

    public void gotoUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, new StoryPresenter.UserObserver());
    }

    @Override
    public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        this.lastStatus = statuses.get(statuses.size() - 1);
    }

    @Override
    public void failed(String message) {
        view.displayErrorMessage("Get story failed: " + message);
    }

    @Override
    public void exceptionThrown(Exception ex) {
        view.displayErrorMessage("Get story threw exception: " + ex.getMessage());
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
