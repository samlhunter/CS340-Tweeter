package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.UpdateFeedQueueMessage;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg: event.getRecords()) {
            UpdateFeedQueueMessage toHandle = JsonSerializer.deserialize(msg.getBody(), UpdateFeedQueueMessage.class);
            List<UserDTO> followers = toHandle.users;
            Status status = toHandle.status;

            //Now we need to batch write to the feed table
            new FeedDAO().putFeed(status, followers);
        }
        return null;
    }
}
