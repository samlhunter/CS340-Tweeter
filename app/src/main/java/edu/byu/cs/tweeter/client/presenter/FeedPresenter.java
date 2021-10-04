package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> implements StatusService.GetFeedObserver{

    private static final int PAGE_SIZE = 10;

    public interface FeedView extends PagedView<Status> {}
    // private PagedPresenter.View view;

    public FeedPresenter(FeedView view, User targetUser) {
        super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(), view);
    }

    public void gettingUser(String alias) {
        view.displayInfoMessage("Getting user's profile...");
        getUser(alias);
    }

    @Override
    protected void getItems() {
        view.setLoading(true);
        new StatusService().getFeed(this.authToken, this.targetUser, pageSize, (Status)lastItem, this);
    }

    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        getStatusesSucceeded(hasMorePages, statuses.get(statuses.size() - 1));
        view.setLoading(false);
        view.addItems(statuses);
    }
}