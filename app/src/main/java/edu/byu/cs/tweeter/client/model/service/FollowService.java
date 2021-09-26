package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface GetFollowingObserver {
        void getFollowingSucceeded(List<User> users, boolean hasMorePages);
        void getFollowingFailed(String message);
        void getFollowingThrewException(Exception ex);
    }

    public interface GetFollowersObserver {
        void getFollowersSucceeded(List<User> users, boolean hasMorePages);
        void getFollowersFailed(String message);
        void getFollowersThrewException(Exception ex);
    }

    public interface FollowObserver {
        void followSucceeded();
        void followFailed(String message);
        void followThrewException(Exception ex);
    }

    public interface UnfollowObserver {
        void unfollowSucceeded();
        void unfollowFailed(String message);
        void unfollowThrewException(Exception ex);
    }

    public interface GetFollowingCountObserver {
        void getFollowingCountSucceeded(int count);
        void getFollowingCountFailed(String message);
        void getFollowingCountThrewException(Exception ex);
    }

    public interface GetFollowersCountObserver {
        void getFollowersCountSucceeded(int count);
        void getFollowersCountFailed(String message);
        void getFollowersCountThrewException(Exception ex);
    }

    public interface IsFollowerObserver {
        void isFollowSucceeded(String text, int backgroundColor, int textColor);
        void isFollowFailed(String message);
        void isFollowThrewException(Exception ex);
    }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    public void followUser(AuthToken authToken, User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken, selectedUser, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    public void unfollowUser(AuthToken authToken, User selectedUser, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    public void getCounts(AuthToken authToken, User targetUser, GetFollowersCountObserver followersObserver, GetFollowingCountObserver followingObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, targetUser, new GetFollowersCountHandler(followersObserver));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, targetUser, new GetFollowingCountHandler(followingObserver));
        executor.execute(followingCountTask);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, currUser, selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {

        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.getFollowingSucceeded(followees, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFollowingFailed(message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFollowingThrewException(ex);
            }
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends Handler {

        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.getFollowersSucceeded(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersThrewException(ex);
            }
        }
    }

    // FollowHandler

    private class FollowHandler extends Handler {

        private FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.followSucceeded();
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.followFailed(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.followThrewException(ex);
            }
        }
    }

    // UnfollowHandler

    private class UnfollowHandler extends Handler {

        private UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.unfollowSucceeded();
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.unfollowFailed(message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.unfollowThrewException(ex);
            }
        }
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends Handler {

        private GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) { this.observer = observer; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.getFollowingCountSucceeded(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.getFollowingCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.getFollowingCountThrewException(ex);
            }
        }
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends Handler {
        private GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) { this.observer = observer; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.getFollowersCountSucceeded(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.getFollowersCountFailed(message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.getFollowersCountThrewException(ex);
            }
        }
    }

    // IsFollowerHandler

    private class IsFollowerHandler extends Handler {

        private IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) { this.observer = observer; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

                // If logged in user is a follower of the selected user, display the follow button as "following"
                if (isFollower) {
                    observer.isFollowSucceeded("Following", R.color.white, R.color.lightGray);
                } else {
                    observer.isFollowSucceeded("Follow", R.color.colorAccent, R.color.white );
                }
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.isFollowFailed(message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.isFollowThrewException(ex);
            }
        }
    }

}
