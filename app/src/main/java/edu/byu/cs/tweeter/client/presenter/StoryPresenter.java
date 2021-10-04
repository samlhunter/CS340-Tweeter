package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements StatusService.GetStoryObserver {

    public interface View extends PagedView<Status> {}

    private static final int PAGE_SIZE = 10;

    // private StoryPresenter.View view;

    public StoryPresenter(View view, User targetUser) {
        super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(),  view);
    }

    @Override
    protected void getItems() {
        view.setLoading(true);
        new StatusService().getStory(this.authToken, this.targetUser, pageSize, (Status)lastItem, this);
    }

    @Override
    public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
        getStatusesSucceeded(hasMorePages, statuses.get(statuses.size() - 1));
        view.setLoading(false);
        view.addItems(statuses);
    }
}
