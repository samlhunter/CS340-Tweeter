package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Service <T extends Runnable> {

    public interface PresenterObserver {
        void failed (String message);
        void exceptionThrown (Exception ex);
    }

    public void executeTask(T task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

    public void executeCountTask(T task) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(task);
    }

    public void getUser(AuthToken authToken, String alias, UserService.GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer));
        executeTask((T) getUserTask);
    }

    protected abstract class ServiceHandler extends Handler {
        private PresenterObserver observer;
        public ServiceHandler(PresenterObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
            if (success) {
                handleSucceeded(msg);
            } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
                observer.failed(message);
            } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
                observer.exceptionThrown(ex);
            }
        }
        public abstract void handleSucceeded(Message msg);
    }

    private class GetUserHandler extends ServiceHandler {
        private UserService.GetUserObserver observer;
        private User user;

        public GetUserHandler(UserService.GetUserObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            observer.getUserSucceeded(user);
        }
    }
}


