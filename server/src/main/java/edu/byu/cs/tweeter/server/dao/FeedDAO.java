package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

public class FeedDAO implements IFeedDAO{
    @Override
    public Item getFeed(String username) {
        return null;
    }

    @Override
    public PutItemOutcome putfeed() {
        return null;
    }
}
