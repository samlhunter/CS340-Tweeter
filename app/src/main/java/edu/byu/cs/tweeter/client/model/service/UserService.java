package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


public class UserService extends Service{
    public interface LoginObserver extends PresenterObserver{ void loginSucceeded(AuthToken authToken, User user); }

    public interface RegisterObserver extends PresenterObserver{ void registerSucceeded(AuthToken authToken, User user); }

    public interface LogoutObserver extends PresenterObserver{ void logoutSucceeded(); }

    public interface GetUserObserver extends PresenterObserver{ void getUserSucceeded(User user); }

    public void login(String alias, String password, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        executeTask(loginTask);
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        executeTask(logoutTask);
    }

    public void register(String firstName, String lastName, String alias, String password,
                         ImageView imageToUpload, RegisterObserver observer) {
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(observer));
        executeTask(registerTask);
    }

    // LoginHandler
    private class LoginHandler extends ServiceHandler {
        private final LoginObserver observer;
        private User loggedInUser;
        private AuthToken authToken;

        private LoginHandler(LoginObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            this.authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.loginSucceeded(authToken, loggedInUser);
        }
    }

    // LogoutHandler
    private class LogoutHandler extends ServiceHandler {
        private final LogoutObserver observer;

        private LogoutHandler(LogoutObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            observer.logoutSucceeded();
        }
    }

    private class RegisterHandler extends ServiceHandler {
        private RegisterObserver observer;
        private User registeredUser;
        private AuthToken authToken;

        private RegisterHandler(RegisterObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            this.authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.registerSucceeded(authToken,registeredUser);
        }
    }
}
