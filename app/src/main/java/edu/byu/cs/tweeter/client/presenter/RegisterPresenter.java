package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    public interface View {
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private final View view;

    public RegisterPresenter(View view) { this.view = view; }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        view.clearInfoMessage();
        view.clearErrorMessage();

        String message = validateRegistration(firstName, lastName, alias, password, imageToUpload);

        if (message == null) {
            view.displayInfoMessage("Registering ...");
            new UserService().register(firstName, lastName, alias, password, imageToUpload, this);
        }
        else{
            view.displayErrorMessage("Register failed: " + message);
        }
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            return("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            return("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            return("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return("Password cannot be empty.");
        }
        if (imageToUpload.getDrawable() == null) {
            return("Profile image must be uploaded.");
        }
        return null;
    }

    @Override
    public void registerSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello, " + user.getName());
    }

    @Override
    public void registerFailed(String message) {
        view.displayErrorMessage("Login failed: " + message);
    }

    @Override
    public void registerThrewException(Exception ex) {
        view.displayErrorMessage("Login threw exception: " + ex.getMessage());
    }
}
