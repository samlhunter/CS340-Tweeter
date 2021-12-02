package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.UpdateFeedQueueMessage;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class PostStatusQueueProcessor implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            // THIS WILL GET ALL USERS FOLLOWING THE AUTHOR OF THE POST, AND THEN BATCH WRITE TO THE UPDATE FEED QUEUE
            Status toHandle = JsonSerializer.deserialize(msg.getBody(),Status.class);
            // We now have the status object, we can get all users following the author
            List<UserDTO> followees = new FollowDAO().getAuthorFollowing(toHandle.getUser().getAlias());
//            System.out.println("Got the total number of users following the author: " );
//            System.out.println(followees.size());
            // Now we need to iterate over the list of users and put a message in the updateFeedQueue
            String message = JsonSerializer.serialize(new UpdateFeedQueueMessage(followees, toHandle));
            String queueUrl = "https://sqs.us-west-2.amazonaws.com/378511175017/CS340TweeterUpdateFeedQueue";

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(message)
                    .withDelaySeconds(5);

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        }
        return null;
    }
}
