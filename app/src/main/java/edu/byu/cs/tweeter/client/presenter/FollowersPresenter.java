package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> implements FollowService.GetFollowersObserver{
   public interface FollowersView extends PagedView<User> {}

   private static final int PAGE_SIZE = 10;

   public FollowersPresenter(FollowersView view, User targetUser) {
       super(PAGE_SIZE, targetUser, Cache.getInstance().getCurrUserAuthToken(), view);
   }

   public void getItems() {
       new FollowService().getFollowers(this.authToken, this.targetUser, pageSize, (User) lastItem, this);
   }

    @Override
    protected String getDescription() { return("Followers"); }

    @Override
    public void getFollowersSucceeded(List<User> users, boolean hasMorePages) {
       if (users.size() == 0) {
           getItemsSucceeded(hasMorePages, null);
       }
       else {
           getItemsSucceeded(hasMorePages, users.get(users.size() - 1));
       }
        view.addItems(users);
    }
}
