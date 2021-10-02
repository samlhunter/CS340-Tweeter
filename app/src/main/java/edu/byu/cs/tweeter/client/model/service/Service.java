package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public abstract class Service <T extends Runnable> {

    public interface ServiceObserver {
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

    protected abstract class ServiceHandler extends Handler {

        private ServiceObserver observer;

        public ServiceHandler(ServiceObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                handleSucceeded(msg);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.failed(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.exceptionThrown(ex);
            }
        }
        public abstract void handleSucceeded(Message msg);
    }
}


