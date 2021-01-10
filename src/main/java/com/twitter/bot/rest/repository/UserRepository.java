package com.twitter.bot.rest.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import com.twitter.bot.rest.models.User;

@Component
public class UserRepository {
	
	private Map<String,User> userMap;

	public UserRepository() {
		userMap = new ConcurrentHashMap<>();
		// Put a registered user in the map for login
		// encrypt a password using https://www.browserling.com/tools/bcrypt and enter below
		userMap.put("user_name", new User("userName","brcypt userPassword"));
	}
	
	public void save(User user)
	{
		userMap.put(user.getUsername(), user);
	}

	public User findByUsername(String username) {
		return userMap.get(username);
	}

}
