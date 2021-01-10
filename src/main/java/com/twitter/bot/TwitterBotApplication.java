package com.twitter.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.twitter.bot.listener.StatusStreamListener;
import com.twitter.bot.streamfactory.StreamFactory;
import com.twitter.bot.util.TwitterBotHelper;

import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

@SpringBootApplication
public class TwitterBotApplication {
	
	public static void main(String[] args) throws TwitterException {
		SpringApplication.run(TwitterBotApplication.class, args);

		Twitter twitterBot = ConfigureBot.getTwitterBot();
		IDs initialFollowers = twitterBot.getFollowersIDs(-1);
		long[] followerIds = initialFollowers.getIDs();
		TwitterStream twitterStatusStream = StreamFactory.getStreamObject();

		setupHelper(twitterBot, followerIds, twitterStatusStream);

		StatusStreamListener statusListener = new StatusStreamListener(twitterBot);
		FilterQuery filter = new FilterQuery(followerIds);
		twitterStatusStream.addListener(statusListener);
		twitterStatusStream.filter(filter);

		Runnable followersThread = TwitterBotHelper.createFollowerMonitorThread();
		TwitterBotHelper.executor.execute(followersThread);
	}

	private static void setupHelper(Twitter twitterBot, long[] followerIds, TwitterStream twitterStatusStream) {
		TwitterBotHelper.followersList = followerIds;
		TwitterBotHelper.twitterBot = twitterBot;
		TwitterBotHelper.stream = twitterStatusStream;
	}
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
