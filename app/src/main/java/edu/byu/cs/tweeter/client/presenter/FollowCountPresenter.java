package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowCountPresenter implements FollowService.GetFollowersCountObserver, FollowService.GetFollowingCountObserver {
    public interface View{
        public void updateFollowersCount(int count);
        public void updateFollowingCount(int count);
        public void displayErrorMessage(String message);
    }

    private View view;
    private AuthToken authToken;
    private User targetUser;

    public FollowCountPresenter(View view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void updateFollowCount() {
        new FollowService().getCounts(authToken, targetUser, this, this);
    }

    @Override
    public void getFollowingCountSucceeded(int count) {
        view.updateFollowingCount(count);
    }

    @Override
    public void getFollowingCountFailed(String message) {
        view.displayErrorMessage("There was an error getting the following count: " + message);
    }

    @Override
    public void getFollowingCountThrewException(Exception ex) {
        view.displayErrorMessage("There was an exception in getting the following count: " + ex.getMessage());
    }

    @Override
    public void getFollowersCountSucceeded(int count) {
        view.updateFollowersCount(count);
    }

    @Override
    public void getFollowersCountFailed(String message) {
        view.displayErrorMessage("There was an error getting the followers count: " + message);
    }

    @Override
    public void getFollowersCountThrewException(Exception ex) {
        view.displayErrorMessage("There was an exception in getting the followers count: " + ex.getMessage());
    }
}
