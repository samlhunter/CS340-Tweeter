package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.model.domain.Status;

public abstract class Presenter implements Service.PresenterObserver{
    public interface PresenterView {
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
    }

    private PresenterView view;
    protected abstract String getDescription();

    Presenter(PresenterView view) {
        this.view = view;
    }

    @Override
    public void failed(String message) {
        view.displayErrorMessage(getDescription() + " Service failed: " + message);
    }

    @Override
    public void exceptionThrown(Exception ex) {
        view.displayErrorMessage(getDescription() +  " Service threw exception: " + ex.getMessage());
    }
}
