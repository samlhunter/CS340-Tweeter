package edu.byu.cs.tweeter.client.model.service.observer;

public interface ServiceObserver<T> {
    void handleSuccess(T value);
    void handleFailure(String message);
    void handleException(Exception ex);
}
