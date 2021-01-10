package com.twitter.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
public class ConfigureBot {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigureBot.class);
	private static Twitter twitterBot;
	
	private static void configureBuilder(){
		
		logger.debug("Configuring the Bot");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("your_oAuth_Consumer_Key")
		  .setOAuthConsumerSecret("your_OAuth_Consumer_Secret")
		  .setOAuthAccessToken("your_OAuth_Access_Token")
		  .setOAuthAccessTokenSecret("your_OAuthAccess_Token_Secret");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitterBot = tf.getInstance();
	}
	
	public static Twitter getTwitterBot() {
		if(twitterBot==null) {
			configureBuilder();
		}
		return twitterBot;
	}
	
}
