package com.twitter.bot.streamfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.bot.ConfigureBot;

import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

public class StreamFactory {
	
	private static Logger logger = LoggerFactory.getLogger(StreamFactory.class);

	public static TwitterStream getStreamObject() throws TwitterException {
		
		logger.debug("Twitter Stream Object requested");
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		AccessToken accessToken = new AccessToken(ConfigureBot.getTwitterBot().getConfiguration().getOAuthAccessToken(),
				ConfigureBot.getTwitterBot().getConfiguration().getOAuthAccessTokenSecret(),
				ConfigureBot.getTwitterBot().getId());
		twitterStream.setOAuthConsumer(ConfigureBot.getTwitterBot().getConfiguration().getOAuthConsumerKey(),
				ConfigureBot.getTwitterBot().getConfiguration().getOAuthConsumerSecret());
		twitterStream.setOAuthAccessToken(accessToken);
		return twitterStream;
	}
	
}
