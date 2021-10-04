package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {

    public interface GetFollowingObserver extends PresenterObserver { void getFollowingSucceeded(List<User> users, boolean hasMorePages); }

    public interface GetFollowersObserver extends PresenterObserver { void getFollowersSucceeded(List<User> users, boolean hasMorePages); }

    public interface FollowObserver extends PresenterObserver { void followSucceeded(); }

    public interface UnfollowObserver extends PresenterObserver{ void unfollowSucceeded(); }

    public interface GetFollowingCountObserver extends PresenterObserver { void getFollowingCountSucceeded(int count); }

    public interface GetFollowersCountObserver extends PresenterObserver{ void getFollowersCountSucceeded(int count); }

    public interface IsFollowerObserver extends PresenterObserver{ void isFollowSucceeded(String text, int backgroundColor, int textColor); }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer));
        executeTask(getFollowingTask);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        executeTask(getFollowersTask);
    }

    public void followUser(AuthToken authToken, User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken, selectedUser, new FollowHandler(observer));
        executeTask(followTask);
    }

    public void unfollowUser(AuthToken authToken, User selectedUser, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer));
        executeTask(unfollowTask);
    }

    public void getCounts(AuthToken authToken, User targetUser, GetFollowersCountObserver followersObserver, GetFollowingCountObserver followingObserver) {
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, targetUser, new GetFollowersCountHandler(followersObserver));
        executeCountTask(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, targetUser, new GetFollowingCountHandler(followingObserver));
        executeCountTask(followingCountTask);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, currUser, selectedUser, new IsFollowerHandler(observer));
        executeTask(isFollowerTask);
    }

    // GetFollowingHandler
    private class GetFollowingHandler extends ServiceHandler {
        private GetFollowingObserver observer;
        private List<User> followees;
        private boolean hasMorePages;

        public GetFollowingHandler(GetFollowingObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.getFollowingSucceeded(followees, hasMorePages);
        }
    }

    // GetFollowersHandler
    private class GetFollowersHandler extends ServiceHandler {
        private GetFollowersObserver observer;
        private List<User> followers;
        private boolean hasMorePages;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.getFollowersSucceeded(followers, hasMorePages);
        }
    }

    // FollowHandler
    private class FollowHandler extends ServiceHandler {
        private FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {observer.followSucceeded();}
    }

    // UnfollowHandler
    private class UnfollowHandler extends ServiceHandler {
        private UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {observer.unfollowSucceeded();}
    }

    // GetFollowingCountHandler
    private class GetFollowingCountHandler extends ServiceHandler {
        private GetFollowingCountObserver observer;
        private int count;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            observer.getFollowingCountSucceeded(count);
        }
    }

    // GetFollowersCountHandler
    private class GetFollowersCountHandler extends ServiceHandler {
        private GetFollowersCountObserver observer;
        private int count;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            observer.getFollowersCountSucceeded(count);
        }
    }

    // IsFollowerHandler
    private class IsFollowerHandler extends ServiceHandler {
        private IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            // If logged in user is a follower of the selected user, display the follow button as "following"
            if (isFollower) {observer.isFollowSucceeded("Following", R.color.white, R.color.lightGray);}
            else {observer.isFollowSucceeded("Follow", R.color.colorAccent, R.color.white );}
        }
    }
}
