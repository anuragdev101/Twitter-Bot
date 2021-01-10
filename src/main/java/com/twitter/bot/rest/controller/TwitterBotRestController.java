package com.twitter.bot.rest.controller;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.twitter.bot.listener.PublicStatusListener;
import com.twitter.bot.rest.models.HashTag;
import com.twitter.bot.rest.models.Tweet;
import com.twitter.bot.streamfactory.StreamFactory;
import com.twitter.bot.util.TwitterBotHelper;

import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

@RestController
@RequestMapping("/api/bot")
public class TwitterBotRestController {
	
	private static Logger logger = LoggerFactory.getLogger(TwitterBotRestController.class);

	@PostMapping("/mat")
    public String massTweet(@Validated @RequestBody Tweet tweet){
		Twitter twitterBot = TwitterBotHelper.twitterBot;
		String tweetText = tweet.getTweetText();
		int limit = tweet.getLimit();
		
		Runnable massTweetThread = () -> {
			for(int currentTweet = 1;currentTweet<=limit;currentTweet++) {
				try {
					logger.debug("Tweeting status number "+currentTweet);
					twitterBot.updateStatus("My Tweet number "+currentTweet+"\n"+tweetText);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				TwitterBotHelper.lastTweetCount++;
			}
		};
		massTweetThread.run();
        return "Starting mass tweet "+tweetText+" with limit "+limit;
    }
	
	@PostMapping("/hashtag")
	public String searchHashTags(@Validated @RequestBody HashTag hashTag) throws InterruptedException {

		TwitterBotHelper.executor.invokeAll(Arrays.asList(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				TwitterStream twitterStream = StreamFactory.getStreamObject();
				FilterQuery tweetFilterQuery = new FilterQuery();
				tweetFilterQuery.track(hashTag.getHashTagText());
				if (hashTag.getLocations() != null) {
					tweetFilterQuery.locations(hashTag.getLocations());
				}
				if (hashTag.getLanguage() != null) {
					tweetFilterQuery.language(hashTag.getLanguage());
				}
				twitterStream.addListener(new PublicStatusListener());
				twitterStream.filter(tweetFilterQuery);
				return null;
			}
		}), hashTag.getTime(), TimeUnit.MINUTES); // Timeout of 10 minutes.
		return "Starting retweets of text " + Arrays.toString(hashTag.getHashTagText()) + "|| for time "
				+ hashTag.getTime() + " minutes!";
	}
	
}
