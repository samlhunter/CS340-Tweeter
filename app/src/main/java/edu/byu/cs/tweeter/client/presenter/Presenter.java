package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public abstract class Presenter {
    public interface View {
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
    }
}
