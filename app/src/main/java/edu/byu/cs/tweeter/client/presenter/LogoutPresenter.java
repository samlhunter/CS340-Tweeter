package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutPresenter implements UserService.LogoutObserver {
    public interface View {
        void navigateToMenu();
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
        void clearInfoMessage();
        void clearErrorMessage();
    }

    private final View view;
    private AuthToken authToken;

    public LogoutPresenter(View view, AuthToken authToken) {
        this.view = view;
        this.authToken = authToken;
    }

    public void logout() {
        view.displayInfoMessage("Logging out...");
        new UserService().logout(authToken, this);
    }

    @Override
    public void logoutSucceeded() {
        view.clearInfoMessage();
        view.clearErrorMessage();
        view.navigateToMenu();
    }

    @Override
    public void logoutFailed(String message) {
        view.displayErrorMessage("Logout failed: " + message);
    }

    @Override
    public void logoutThrewException(Exception ex) {
        view.displayErrorMessage("Logout threw exception: " + ex.getMessage());
    }
}
