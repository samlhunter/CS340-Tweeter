package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;

public interface IFeedDAO {
    GetFeedResponse getFeed(String username, String lastStatusDatetime, int limit);
    void putFeed(Status status, List<User> followers);
}
