

package edu.byu.cs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {
    private MainPresenter.MainView mockMainView;
    private StatusService mockStatusService;
    private Cache mockCache;
    private AuthToken mockAuthToken;
    private StatusService.PostStatusObserver mockPostStatusObserver;
    private User user;
    private Status mockStatus;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);
        mockAuthToken = new AuthToken("test","test");
        mockPostStatusObserver = Mockito.mock(StatusService.PostStatusObserver.class);
        user = new User();
        mockStatus = new Status("test message",user, "test datetime", Arrays.asList(""), Arrays.asList(""));

        Cache.setInstance(mockCache);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView, user));
        mainPresenterSpy.setService(mockStatusService);

        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getService();
        Mockito.doReturn(mockAuthToken).when(mainPresenterSpy).getAuthToken();
        Mockito.doReturn(mockStatus).when(mainPresenterSpy).getStatus("sample post");
    }

    @Test
    public void testPostStatusSucceeded() {
        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
          @Override
          public Void answer(InvocationOnMock invocation) throws Throwable {
              StatusService.PostStatusObserver observer = invocation.getArgument(2);
              Assert.assertEquals(mockAuthToken,invocation.getArgument(0));
              Assert.assertEquals(mockStatus, invocation.getArgument(1));
              observer.postStatusSucceeded();
              return null;
          }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        // Run case
        mainPresenterSpy.postStatus("sample post");

        Mockito.verify(mockMainView).displayInfoMessage("Posting status...");
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatusFailed() {
        Answer<Void> postStatusFailedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.failed("Failed to post status: " + Mockito.anyString());
                return null;
            }
        };

        Mockito.doAnswer(postStatusFailedAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        // Run Case
        mainPresenterSpy.postStatus("sample post");

        Mockito.verify(mockMainView).displayInfoMessage("Posting status...");
        Mockito.verify(mockMainView).displayInfoMessage("Failed to post status: " + Mockito.anyString());
    }

    @Test
    public void testPostStatusException() {
        Answer<Void> postStatusExceptionThrown = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2);
                observer.exceptionThrown(new Exception());
                return null;
            }
        };

        Mockito.doAnswer(postStatusExceptionThrown).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        // Run case
        mainPresenterSpy.postStatus("sample post");

        Mockito.verify(mockMainView).displayInfoMessage("Posting status...");
        Mockito.verify(mockMainView).displayInfoMessage("Failed to post status: " + Mockito.anyString());
    }

}
