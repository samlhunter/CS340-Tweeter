package edu.byu.cs;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryTest {
    // Service should have an operation that creates a background task to retrieve a user's story
    // from the server and notifies the server's observer of the operation's outcome. Test should verify
    // that service's observer is notified in the case of a successful story retrieval
    private StatusService statusService;
    private AuthToken authToken;
    private User user;
    private CountDownLatch countdownLatch;
    private FakeObserver fakeObserverSpy;
    private int limit;

    private void resetCountdownLatch() {
        countdownLatch = new CountDownLatch(1);
    }

    private void awaitCountdownLatch() throws InterruptedException {
        countdownLatch.await();
        resetCountdownLatch();
    }

    private class FakeObserver implements StatusService.GetStoryObserver {

        @Override
        public void failed(String message) {

        }

        @Override
        public void exceptionThrown(Exception ex) {

        }

        @Override
        public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
            countdownLatch.countDown();
        }
    }

    @Before
    public void setup() {
        this.statusService = new StatusService();
        authToken = new AuthToken();
        user = new User("firstName", "lastName", null);
        FakeObserver mySpy = new FakeObserver();
        fakeObserverSpy = Mockito.spy(mySpy);
        limit = 10;

        resetCountdownLatch();
    }

    @Test
    public void getStorySucceeded() throws InterruptedException{
        statusService.getStory(authToken, user, limit, null, fakeObserverSpy);

        awaitCountdownLatch();

        Mockito.verify(fakeObserverSpy).getStorySucceeded(Mockito.anyList(), Mockito.anyBoolean());
    }
}
