package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class followingPresenter implements FollowService.GetFollowingObserver {
    public interface View {
        void addItems(List<User> followees);
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void setLoading(boolean value);
    }

    private static final int PAGE_SIZE = 10;

    private View view;
    private User targetUser;
    private AuthToken authToken;
    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public followingPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems() {
        if(!this.isLoading && this.hasMorePages) {
            isLoading = true;
            view.setLoading(true);
            new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
        }
    }

    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages) {
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        this.lastFollowee = users.get(users.size() - 1);
    }

    @Override
    public void failed(String message) {
        view.displayErrorMessage("Get following failed: " + message);
    }

    @Override
    public void exceptionThrown(Exception ex) {
        view.displayErrorMessage("Get following threw exception: " + ex.getMessage());
    }

    private class UserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSucceeded(User user) { view.navigateToUser(user); }

        @Override
        public void getUserFailed(String message) { view.displayErrorMessage(message);}

        @Override
        public void getUserThrewException(Exception ex) { view.displayErrorMessage("Failed to get user's profile because of exception" + ex.getMessage());}
    }

    public void gotoUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, new UserObserver());
    }
}
