package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowPresenter implements FollowService.FollowObserver, FollowService.UnfollowObserver, FollowService.IsFollowerObserver {
    public interface View {
        void updateFollowButton(String message, int backgroundColor, int textColor);
        void enableFollowButton(boolean enabled);
        void setFollowVisible();
        void setFollowGone();
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
    }

    private View view;
    private AuthToken authToken;
    private User selectedUser;

    public FollowPresenter(View view, AuthToken authToken, User selectedUser) {
        this.view = view;
        this.authToken = authToken;
        this. selectedUser = selectedUser;
    }

    public void updateFollow(String buttonText) {
        view.enableFollowButton(false);
        if (buttonText.equals("Following")) {
            view.displayInfoMessage("Removing " + selectedUser.getName() + "...");
            new FollowService().unfollowUser(authToken, selectedUser, this);
        }
        else {
            view.displayInfoMessage("Adding " + selectedUser.getName() + "...");
            new FollowService().followUser(authToken,selectedUser,this);
        }
    }

    public void isFollower(User currUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowGone();
        }
        else {
            view.setFollowVisible();
            new FollowService().isFollower(authToken, currUser, selectedUser, this);
        }
    }

    @Override
    public void followSucceeded() {
        view.enableFollowButton(true);
        view.updateFollowButton("Following",R.color.white,R.color.lightGray);
    }
    @Override
    public void followFailed(String message) { view.displayErrorMessage("Follow user failed: " + message); }

    @Override
    public void followThrewException(Exception ex) { view.displayErrorMessage("Follow user threw exception: " + ex.getMessage()); }

    @Override
    public void unfollowSucceeded() {
        view.enableFollowButton(true);
        view.updateFollowButton("Follow", R.color.colorAccent, R.color.white);
    }

    @Override
    public void unfollowFailed(String message) { view.displayErrorMessage("Unfollow user failed: " + message); }

    @Override
    public void unfollowThrewException(Exception ex) { view.displayErrorMessage("Unfollow user threw exception: " + ex.getMessage()); }

    @Override
    public void isFollowSucceeded(String text, int backgroundColor, int textColor) {
        view.updateFollowButton(text, backgroundColor, textColor);
    }

    @Override
    public void isFollowFailed(String message) {
        view.displayErrorMessage("IsFollower check failed: " + message);
    }

    @Override
    public void isFollowThrewException(Exception ex) {
        view.displayErrorMessage("IsFollower check threw an exception: " + ex.getMessage());
    }
}
