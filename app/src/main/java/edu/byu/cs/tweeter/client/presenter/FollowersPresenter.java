package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements FollowService.GetFollowersObserver {
   public interface View {
       void addItems(List<User> followers);
       void navigateToUser(User user);
       void displayErrorMessage(String message);
       void displayInfoMessage(String message);
       void setLoading(boolean value);
   }

   private static final int PAGE_SIZE = 10;

   private View view;
   private User targetUser;
   private AuthToken authToken;
   private User lastFollower;
   private boolean hasMorePages = true;
   private boolean isLoading = false;

   public FollowersPresenter(View view, AuthToken authToken, User targetUser) {
       this.view = view;
       this.authToken = authToken;
       this.targetUser = targetUser;
   }

   public void loadMoreItems() {
       if(!this.isLoading && this.hasMorePages) {
           isLoading = true;
           view.setLoading(true);
           new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
       }
   }

    @Override
    public void getFollowersSucceeded(List<User> users, boolean hasMorePages) {
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
        this.isLoading = false;
        this.lastFollower = users.get(users.size() - 1);
    }

    @Override
    public void getFollowersFailed(String message) {
        view.displayErrorMessage("Get followers failed: " + message);
    }

    @Override
    public void getFollowersThrewException(Exception ex) {
        view.displayErrorMessage("Get followers threw exception: " + ex.getMessage());
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
