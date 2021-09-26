package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostPresenter implements StatusService.PostStatusObserver {
    public interface View {
        void displayInfoMessage(String message);
        void displayErrorMessage(String message);
    }
    private final View view;
    private AuthToken authToken;

    public PostPresenter(View view, AuthToken authToken) {
        this.view = view;
        this.authToken = authToken;
    }

    public void postStatus(Status newStatus) {
        view.displayInfoMessage("Posting status...");
        new StatusService().postStatus(authToken, newStatus, this);
    }

    @Override
    public void postStatusSucceeded() {
        view.displayInfoMessage("Status successfully posted");
    }

    @Override
    public void postStatusFailed(String message) {
        view.displayErrorMessage("Failed to post status: " + message);
    }

    @Override
    public void postStatusThrewException(Exception ex) {
        view.displayErrorMessage("Posting status threw exception: " + ex.getMessage());
    }
}
