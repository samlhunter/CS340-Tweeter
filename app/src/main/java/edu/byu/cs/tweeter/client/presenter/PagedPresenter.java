package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter implements Service.PresenterObserver {
    public interface PagedView<U> extends PresenterView {
        void setLoading(boolean value);
        void addItems(List<U> items);
        void navigateToUser(User user);
    }

    protected int pageSize;
    protected User targetUser;
    protected AuthToken authToken;
    protected T lastItem;
    protected boolean pagedHasMorePages = true;
    protected boolean pagedLoading = false;
    protected boolean isGettingUser = false;
    protected PagedView view;

    protected PagedPresenter(int pageSize, User targetUser, AuthToken authToken, PagedView view){
        super(view);
        this.pageSize = pageSize;
        this.targetUser = targetUser;
        this.authToken = authToken;
        this.view = view;
    }

    public void loadMoreItems() {
        if(!this.pagedLoading && this.pagedHasMorePages) {
            view.setLoading(true);
            view.displayInfoMessage("Loading More Items");
            this.pagedLoading = true;
            getItems();
        }
    }

    public void getUser(String alias) {
        if (!isGettingUser) {
            view.setLoading(true);
            view.displayInfoMessage("Getting user's profile...");
            this.isGettingUser = true;
            new UserService().getUser(this.authToken, alias, new UserObserver());
        }

    }

    protected void getItemsSucceeded(boolean hasMorePages, T lastItem){
        view.setLoading(false);
        this.pagedLoading = false;
        this.pagedHasMorePages = hasMorePages;
        this.lastItem = lastItem;
    }

    protected abstract void getItems();

    private class UserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSucceeded(User user) {
            isGettingUser = false;
            view.navigateToUser(user);
        }

        @Override
        public void failed(String message) {
            view.displayErrorMessage(message);
        }

        @Override
        public void exceptionThrown(Exception ex) {
            view.displayErrorMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }
}
