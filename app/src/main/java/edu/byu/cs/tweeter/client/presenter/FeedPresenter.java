package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> implements StatusService.GetFeedObserver{
    public interface FeedView extends PagedView<Status> {}

    private static final int PAGE_SIZE = 10;

    public FeedPresenter(FeedView view, User targetUser) {
        super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(), view);
    }

    @Override
    protected void getItems() {
        new StatusService().getFeed(this.authToken, this.targetUser, pageSize, (Status)lastItem, this);
    }

    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        getItemsSucceeded(hasMorePages, statuses.get(statuses.size() - 1));
        view.addItems(statuses);
    }
}