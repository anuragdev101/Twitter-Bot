# Twitter Bot

This is a twitter automation bot written in Java using twitter4j library

## Pre-requisites

1. Twitter Developer API access.
2. Heroku access (For deployment)

## Features

1. Auto Follow / Unfollow: 
The bot will automatically follow if they follow the authenticated user. Same for unfollow.

2. Auto retweet and favorite: The bot will automatically retweet and favorite recent 20 (customisable) tweets of recent followers.

3. Followers timeline monitoring: Creates a stream to monitor the timeline of followers and retweet and favorite them automatically.

4. Mass tweets: Allows to post a number of tweets on your timeline supporting a trend or hashtag.

5. Live trending tweets retweet: Creates a stream to monitor live tweets done by users including particular 'keywords' and retweet and favorite them. 



## Configuring the bot

1. Create an app in your [twitter developer account](https://developer.twitter.com/en/portal/dashboard).
2. Generate consumerKey, consumerSecret, accessToken and accessTokenSecret and replace them in "ConfigureBot.java"

```python
private static void configureBuilder(){
		
		logger.debug("Configuring the Bot");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("your_consumer_key")
		  .setOAuthConsumerSecret("your_consuer_key_secret")
		  .setOAuthAccessToken("your_access_token")
		  .setOAuthAccessTokenSecret("your_access_token_secret");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitterBot = tf.getInstance();
	}
```

3. Replace value of "AUTH_USER" with your twitter username in StatusStreamListener.java

```python
private static final String AUTH_USER="your_user_name";
```
## REST Endpoints
The bot includes following endpoints:

```bash
 1. POST: /api/bot/mat 
```
Creates mass tweets on your timeline.

Parameters:

*  limit: Number of tweets to post

* tweetText: The text to tweet

* batchSize: The number of tweets in one batch (see twitter rate limits)

```python
2. POST: /api/bot/hashtag
```
Retweets and favorites public users' tweets containing particular key words.

Parameters:

* hashTagText: retweets all tweets containing this text. You can define multiple matching texts also.

* locations: users' tweets location

* language: specifies the language of the tweets to be monitored

* time: the time upto which bot will monitor and retweet, favorite

```python
3. POST: /api/bot/users/signup
```
Service for registering new application users.

Parameters:

* username: user name of user

* password: password of new user

```python
4. POST: /login
```
Service for user login
Parameters:

* username: user name of user

* password: password of new user

## Usage
After deploying the bot on heroku, follow the below guidelines:


1. Login to the application using registered username and password

2. Upon successful authorization and authentication, you'll receive an Authorization token in response header in this format:

```python
Authorization: Bearer random_token_values
```

3. Pass this token to other REST endpoints in headers in same format while making a request.
