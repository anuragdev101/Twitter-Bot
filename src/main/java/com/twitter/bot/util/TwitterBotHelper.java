package com.twitter.bot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.bot.listener.PublicStatusListener;
import com.twitter.bot.streamfactory.StreamFactory;

import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

public class TwitterBotHelper {
	
	private static Logger logger = LoggerFactory.getLogger(TwitterBotHelper.class);
	
	private static ConcurrentMap<Long, Long> hashMap = new ConcurrentHashMap<>();
	
	public static long[] followersList;
	public static TwitterStream stream;
	public static Twitter twitterBot;
	public static int lastTweetCount = 0;
	public static ExecutorService executor = Executors.newCachedThreadPool();
	
	public static void checkFollowers() throws InterruptedException, TwitterException {
		
		logger.debug("Checking followers");
		long[] newFollowersList;
		try {
			newFollowersList = twitterBot.getFollowersIDs(-1).getIDs();
		} catch (TwitterException e) {
			e.printStackTrace();
			return;
		}
		Arrays.sort(followersList);
		Arrays.sort(newFollowersList);
		updateFollowers(newFollowersList);
		Thread.sleep(60000);
	}
	
	private static void updateFollowers(long[] newFollowersList) throws TwitterException, InterruptedException{
		
		long[] unFollowed = fetchUnfollowersList(newFollowersList);
		long[] followers = fetchFollowersList(newFollowersList);
		
		if (unFollowed.length == 0 && followers.length == 0) {
			logger.debug("Followers not updated, returning call");
			return;
		}
		destroyFriendship(unFollowed);
		createFriendship(followers);
		followersList = newFollowersList;
		restartStream();
	}

	private static long[] fetchFollowersList(long[] newFollowersList) {
		Set<Long> followersSet = LongStream.of(followersList).boxed().collect(Collectors.toCollection(HashSet::new));
		return LongStream.of(newFollowersList).filter(val -> !followersSet.contains(val)).toArray();
	}

	private static long[] fetchUnfollowersList(long[] newFollowersList) {
		Set<Long> newFollowersSet = LongStream.of(newFollowersList).boxed().collect(Collectors.toCollection(HashSet::new));
		return LongStream.of(followersList).filter(val -> !newFollowersSet.contains(val)).toArray();
	}

	private static void createFriendship(long[] followers) throws TwitterException {
		for (long follower : followers) {
			logger.debug("Creating friendship for "+follower);
			twitterBot.createFriendship(follower);
			retweetRecent(follower);
		}
	}

	private static void destroyFriendship(long[] unFollowed) throws TwitterException {
		for (long unfollower : unFollowed) {
			logger.debug("Destroying friendship for "+unfollower);
			twitterBot.destroyFriendship(unfollower);
			hashMap.remove(unfollower);
		}
	}
	
	private static void retweetRecent(long userId) throws TwitterException {
		
		logger.debug("Rtweeting status for "+userId);
		ResponseList<Status> recentStatus = twitterBot.getUserTimeline(userId);
		for (Status status : recentStatus) {
			twitterBot.unRetweetStatus(status.getId());
			twitterBot.retweetStatus(status.getId());
			twitterBot.destroyFavorite(status.getId());
			twitterBot.createFavorite(status.getId());
		}
		addUserToStatusCount(userId, recentStatus.get(0).getId());
	}

 	private static void addUserToStatusCount(long userId, long recentStatusId) {
 		
 		logger.debug("Adding user to file");
 		hashMap.put(userId, recentStatusId);
 	}
	
	private static void restartStream() throws TwitterException, InterruptedException {
		
		logger.debug("Closing and restarting stream");
		stream.cleanUp();
		stream.shutdown();
		stream.filter(new FilterQuery(twitterBot.getFollowersIDs(-1).getIDs()));
		Thread.sleep(30000);
		retweetAndFavoriteSkippedStatus();
	}
	
	public static void retweetAndFavoriteSkippedStatus() throws TwitterException {

		logger.debug("Rwteeting status of skipped followers");
		
		Set<Long> users = hashMap.keySet();
		for (Long user : users) {
			ResponseList<Status> userStatuses = twitterBot.getUserTimeline(user);
			for (Status status : userStatuses) {
				if (Long.valueOf(status.getId()).equals(hashMap.get(status.getUser().getId()))) {
					break;
				}
				twitterBot.retweetStatus(status.getId());
				twitterBot.createFavorite(status.getId());
			}
		}
	}
	
	public static Runnable createFollowerMonitorThread() {
		return () -> {
			try {
				Thread.sleep(30000);
				logger.debug("Starting Follower monitoring service");
				while (true) {
					checkFollowers();
				}
			} catch (TwitterException | InterruptedException e) {
				e.printStackTrace();
			}
		};
	}
	
	public static ConcurrentMap<Long, Long> getHashmap() {
		return hashMap;
	}
	
	public static TwitterStream generateNewStream() throws TwitterException {
		TwitterStream twitterStatusStream = StreamFactory.getStreamObject();
		twitterStatusStream.addListener(new PublicStatusListener());
		return twitterStatusStream;
	}
}
