package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {
    public interface GetFeedObserver extends PresenterObserver { void getFeedSucceeded(List<Status> statuses, boolean hasMorePages); }
    public interface GetStoryObserver extends PresenterObserver{ void getStorySucceeded(List<Status> statuses, boolean hasMorePages); }
    public interface PostStatusObserver extends PresenterObserver{ void postStatusSucceeded(); }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken, targetUser, limit, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
    }

    // GetFeedHandler
    private class GetFeedHandler extends ServiceHandler {
        private GetFeedObserver observer;
        private List<Status> statuses;
        private boolean hasMorePages;

        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
            observer.getFeedSucceeded(statuses, hasMorePages);

        }
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetStoryObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken, targetUser, limit, lastStatus, new GetStoryHandler(observer));
        executeTask(getStoryTask);
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    // GetStoryHandler
    private class GetStoryHandler extends ServiceHandler {
        private GetStoryObserver observer;
        private List<Status> statuses;
        private boolean hasMorePages;

        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            this.statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
            this.hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
            observer.getStorySucceeded(statuses, hasMorePages);
        }
    }

    // PostStatusHandler
    private class PostStatusHandler extends ServiceHandler {
        private PostStatusObserver observer;

        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        public void handleSucceeded(Message msg) {
            observer.postStatusSucceeded();
        }
    }
}
