package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter implements
        FollowService.GetFollowersCountObserver,
        FollowService.GetFollowingCountObserver,
        FollowService.FollowObserver,
        FollowService.UnfollowObserver,
        FollowService.IsFollowerObserver,
        UserService.LogoutObserver,
        StatusService.PostStatusObserver
{
    public interface MainView extends PresenterView {
        void updateFollowersCount(int count);
        void updateFollowingCount(int count);
        void clearInfoMessage();
        void clearErrorMessage();
        void updateFollowButton(String message, int backgroundColor, int textColor);
        void enableFollowButton(boolean enabled);
        void setFollowVisible();
        void setFollowGone();
        void navigateToMenu();
    }

    private MainView view;
    private AuthToken authToken;
    private User selectedUser;
    private StatusService service;

    public MainPresenter(MainView view, User selectedUser) {
        super(view);
        this.view = view;
        this.authToken = Cache.getInstance().getCurrUserAuthToken();
        this.selectedUser = selectedUser;
        this.service = new StatusService();
    }

    // Logic for Follow Count
    public void updateFollowCount() {new FollowService().getCounts(authToken, selectedUser, this, this); }

    @Override
    public void getFollowingCountSucceeded(int count) {
        view.updateFollowingCount(count);
    }

    @Override
    public void getFollowersCountSucceeded(int count) {
        view.updateFollowersCount(count);
    }

    public MainView getView() { return view; }

    public AuthToken getAuthToken() { return authToken; }

    public StatusService getService() { return service; }

    public void setService(StatusService service) {
        this.service = service;
    }

    // Logic for handling Follow Action
    public void updateFollow(String buttonText) {
        view.enableFollowButton(false);
        if (buttonText.equals("Following")) {
            view.displayInfoMessage("Removing " + selectedUser.getName() + "...");
            new FollowService().unfollowUser(authToken, Cache.getInstance().getCurrUser().getAlias(), selectedUser, this);
        }
        else {
            view.displayInfoMessage("Adding " + selectedUser.getName() + "...");
            new FollowService().followUser(authToken, Cache.getInstance().getCurrUser().getAlias(), selectedUser,this);
        }
    }

    public void isFollower() {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) { view.setFollowGone(); }
        else {
            view.setFollowVisible();
            new FollowService().isFollower(authToken, Cache.getInstance().getCurrUser(), selectedUser, this);
        }
    }

    @Override
    protected String getDescription() { return("Main"); }

    @Override
    public void followSucceeded() {
        view.enableFollowButton(true);
        view.updateFollowButton("Following", R.color.white,R.color.lightGray);
    }

    @Override
    public void unfollowSucceeded() {
        view.enableFollowButton(true);
        view.updateFollowButton("Follow", R.color.colorAccent, R.color.white);
    }

    @Override
    public void isFollowSucceeded(String text, int backgroundColor, int textColor) {
        view.updateFollowButton(text, backgroundColor, textColor);
    }

    // Logout logic
    public void logout() {
        view.displayInfoMessage("Logging out...");
        new UserService().logout(authToken, this);
    }

    @Override
    public void logoutSucceeded() {
        view.clearInfoMessage();
        view.clearErrorMessage();
        Cache.getInstance().clearCache();
        view.navigateToMenu();
    }

    public Status getStatus (String post) {
        try {
            return new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
        }
        catch (Exception ex) {
            getView().displayErrorMessage("Exception caught when generating status: " + ex.getMessage());
            return null;
        }
    }

    // Post Status Logic
    public void postStatus(String post) {
        try {
            getView().displayInfoMessage("Posting status...");
            Status newStatus = getStatus(post);
            getService().postStatus(getAuthToken(), newStatus, getPostStatusObserver());
        } catch(Exception ex) {
            getView().displayErrorMessage("Exception caught when posting status: " + ex.getMessage());
        }
    }

    @Override
    public void postStatusSucceeded() {
        getView().displayInfoMessage("Successfully Posted!");
    }

    // Methods for posting status
    public String getFormattedDateTime() throws ParseException{
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }
        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }
        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public StatusService.PostStatusObserver getPostStatusObserver() { return new StatusService.PostStatusObserver()
    {
        @Override
        public void postStatusSucceeded() {
            getView().displayInfoMessage("Successfully Posted!");
        }

        @Override
        public void failed(String message) {
            getView().displayInfoMessage("Failed to post status: " + message);
        }

        @Override
        public void exceptionThrown(Exception ex) {
            getView().displayErrorMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    };
    }

}
