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

public class PublicStatusListener implements StatusListener {

	private static Twitter twitterBot = TwitterBotHelper.twitterBot;
	
	private static Logger logger = LoggerFactory.getLogger(PublicStatusListener.class);
	
	@Override
	public void onException(Exception ex) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatus(Status status) {
		
		logger.debug("Found matching public status: \n"+status.getText());
		if(!status.isRetweetedByMe()) {
			try {
				logger.debug("Retweeting above");
				twitterBot.unRetweetStatus(status.getId());
				twitterBot.retweetStatus(status.getId());
				twitterBot.destroyFavorite(status.getId());
				twitterBot.createFavorite(status.getId());
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

}
