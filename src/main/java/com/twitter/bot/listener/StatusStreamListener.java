package com.twitter.bot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.bot.util.TwitterBotHelper;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class StatusStreamListener implements StatusListener {
	
	private Twitter twitterBot;
	
	public StatusStreamListener(Twitter twitterBot) {
		this.twitterBot=twitterBot;
	}
	
	private static final String AUTH_USER="twitter_user_name";
	
	private static Logger logger = LoggerFactory.getLogger(StatusStreamListener.class);

	@Override
	public void onStatus(Status status) {
		if(status.isRetweetedByMe() || status.getUser().getScreenName().equalsIgnoreCase(AUTH_USER)) {
			return;
		}
		logger.debug("Status updated by one of the follower: "+status.getUser().getScreenName());
		logger.debug("Retweeting status "+status.getText());
		try {
			twitterBot.unRetweetStatus(status.getId());
			twitterBot.retweetStatus(status.getId());
			twitterBot.createFavorite(status.getId());
			TwitterBotHelper.getHashmap().put(status.getUser().getId(), status.getId());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onException(Exception ex) {
		// TODO Auto-generated method stub
		
	}

}
