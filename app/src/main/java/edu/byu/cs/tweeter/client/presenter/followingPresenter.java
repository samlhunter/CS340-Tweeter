package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class followingPresenter extends PagedPresenter<User> implements FollowService.GetFollowingObserver {
    public interface FollowingView extends PagedView<User> {}

    private static final int PAGE_SIZE = 10;

    public followingPresenter(FollowingView view, User targetUser) { super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(), view); }

    @Override
    protected void getItems()  { new FollowService().getFollowing(this.authToken, this.targetUser, pageSize, (User)lastItem, this); }

    @Override
    protected String getDescription() { return("Following"); }

    @Override
    public void getFollowingSucceeded(List<User> users, boolean hasMorePages) {
        if (users.size() == 0) {
            getItemsSucceeded(hasMorePages, null);
        }
        else {
            getItemsSucceeded(hasMorePages, users.get(users.size() - 1));
        }
        view.addItems(users);
    }
}
