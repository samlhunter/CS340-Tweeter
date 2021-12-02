package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements StatusService.GetStoryObserver {
    public interface StoryView extends PagedView<Status> {}

    private static final int PAGE_SIZE = 10;

    public StoryPresenter(StoryView view, User targetUser) { super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(), view); }

    @Override
    protected void getItems() { new StatusService().getStory(this.authToken, this.targetUser, pageSize, (Status)lastItem, this); }

    @Override
    protected String getDescription() { return("Story"); }

    @Override
    public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
        if (statuses.size() == 0) {
            getItemsSucceeded(hasMorePages, null);
        }
        else {
            getItemsSucceeded(hasMorePages, statuses.get(statuses.size() - 1));
        }
        view.addItems(statuses);
    }
}
