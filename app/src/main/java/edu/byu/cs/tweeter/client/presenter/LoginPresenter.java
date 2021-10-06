package edu.byu.cs.tweeter.client.presenter;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter implements UserService.LoginObserver {
    public interface LoginView extends Presenter.PresenterView {
        void navigateToUser(User user);
        void clearErrorMessage();
        void clearInfoMessage();
    }

    private final LoginView view;

    public LoginPresenter(LoginView view) {
        super(view);
        this.view = view;
    }

    public void login(String alias, String password) {
        view.clearInfoMessage();
        view.clearErrorMessage();

        String message = validateLogin(alias, password);

        if (message == null) {
            view.displayInfoMessage("Logging in ...");
            new UserService().login(alias, password, this);
        }
        else{
            view.displayErrorMessage("Login failed: " + message);
        }
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return ("Password cannot be empty.");
        }
        return null;
    }

    @Override
    protected String getDescription() { return("Login"); }

    @Override
    public void loginSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello, " + user.getName());
    }
}
