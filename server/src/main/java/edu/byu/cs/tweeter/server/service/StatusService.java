package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class StatusService {
    DAOFactory factory;

    public StatusService () {
        this.factory = new AWSDAOFactory();
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            String lastStatusDate = null;
            if (request.getLastStatus() != null) {
                lastStatusDate = request.getLastStatus().getDate();
            }
            return factory.getFeedDAO().getFeed(request.getUserAlias(), lastStatusDate, request.getLimit());
        }
        else {
            return new GetFeedResponse("Session expired, login again");
        }
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            String lastStatusDate = null;
            if (request.getLastStatus() != null) {
                lastStatusDate = request.getLastStatus().getDate();
            }
            return factory.getStoryDAO().getStory(request.getUserAlias(), lastStatusDate, request.getLimit());
        }
        else {
            return new GetStoryResponse("Session expired, login again");
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        boolean authenticated = authenticate(factory.getAuthTokenDAO().getAuthToken(request.getAuthToken()));
        if (authenticated) {
            try {
                boolean hasMorePages = true;
                List<User> usersToAdd = new ArrayList<>();
                factory.getStoryDAO().putStory(request.getStatus());
                System.out.println("successfully put status in story");
                // put request in post status queue and get success response then return
                String messageBody = JsonSerializer.serialize(request.getStatus());
                System.out.println("this is the message body: ");
                System.out.println(messageBody);
                String queueUrl = "https://sqs.us-west-2.amazonaws.com/378511175017/CS340TweeterPostStatusQueue";

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(messageBody)
                        .withDelaySeconds(5);

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                System.out.println("Sending message");
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
                System.out.println("successfully added to queue");
                return new PostStatusResponse();
//                // Confirm a successful response
//                String msgId = send_msg_result.getMessageId();
//                System.out.println("Message ID: " + msgId);
//                // return success here
//                // need to post to feed as well
//                // Get all followers of our current user
//                while (hasMorePages) {
//                    System.out.println("Handling the feed table");
//                    GetFollowersResponse response = factory.getFollowDAO().getFollowers(request.getStatus().getUser().getAlias(), null, 10);
//                    hasMorePages = response.getHasMorePages();
//                    usersToAdd = response.getFollowers();
//                    factory.getFeedDAO().putFeed(request.getStatus(), usersToAdd);
//                }
//                return new PostStatusResponse();
            } catch (Exception e) {
                return new PostStatusResponse(e.getMessage());
            }
        }
        else {
            return new PostStatusResponse("Session expired, login again");
        }
    }

    private boolean authenticate(AuthToken authToken) {
        try {
            this.factory.getAuthTokenDAO().validateAuthToken(authToken);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
