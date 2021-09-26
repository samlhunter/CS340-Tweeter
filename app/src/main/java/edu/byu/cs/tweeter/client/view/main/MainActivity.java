package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FollowCountPresenter;
import edu.byu.cs.tweeter.client.presenter.FollowPresenter;
import edu.byu.cs.tweeter.client.presenter.LogoutPresenter;
import edu.byu.cs.tweeter.client.presenter.PostPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.client.view.util.ImageUtils;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements
        StatusDialogFragment.Observer,
        FollowPresenter.View,
        LogoutPresenter.View,
        PostPresenter.View,
        FollowCountPresenter.View
{

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private FollowPresenter followPresenter;
    private LogoutPresenter logoutPresenter;
    private PostPresenter postPresenter;
    private FollowCountPresenter countPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        followPresenter = new FollowPresenter(this, Cache.getInstance().getCurrUserAuthToken(), selectedUser);
        logoutPresenter = new LogoutPresenter(this, Cache.getInstance().getCurrUserAuthToken());
        postPresenter = new PostPresenter(this,Cache.getInstance().getCurrUserAuthToken());
        countPresenter = new FollowCountPresenter(this, Cache.getInstance().getCurrUserAuthToken(), selectedUser);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        countPresenter.updateFollowCount();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(selectedUser.getImageBytes()));

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        followPresenter.isFollower(Cache.getInstance().getCurrUser());

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followPresenter.updateFollow(followButton.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            logoutPresenter.logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStatusPosted(String post) {
        try{
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            postPresenter.postStatus(newStatus);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Exception caught when parsing DateTime", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInfoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateFollowButton(String message, int backgroundColor, int textColor) {
        followButton.setText(message);
        followButton.setBackgroundColor(getResources().getColor(backgroundColor));
        followButton.setTextColor(getResources().getColor(textColor));
    }

    @Override
    public void enableFollowButton(boolean enabled) {
        followButton.setEnabled(enabled);
    }

    @Override
    public void navigateToMenu() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Move to presenter
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void clearInfoMessage() {
        if(logOutToast != null) {
            logOutToast.cancel();
            logOutToast = null;
        }
    }

    @Override
    public void clearErrorMessage() {
        if(logOutToast != null) {
            logOutToast.cancel();
            logOutToast = null;
        }
    }

    @Override
    public void updateFollowersCount(int count) {
        followerCount.setText("Followers: " + count);
    }

    @Override
    public void updateFollowingCount(int count) {
        followeeCount.setText("Following: " + count);
    }

    @Override
    public void setFollowVisible() {
        followButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFollowGone() {
        followButton.setVisibility(View.GONE);
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
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
}
