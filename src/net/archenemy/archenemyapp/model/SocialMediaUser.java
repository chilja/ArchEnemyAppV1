package net.archenemy.archenemyapp.model;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.User;

import com.facebook.model.GraphUser;

import net.archenemy.archenemyapp.presenter.FeedElement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SocialMediaUser {
	
	private String mName;
	private String mTwitterUserName;
	private String mTwitterUserId;
	private String mFacebookUserName;
	private String mFacebookUserId;
	private String mPrefKey;
	private int mUserId;
	private GraphUser mFacebookUser;
	private User mTwitterUser;

	private ArrayList<FeedElement> mPosts = new ArrayList<FeedElement>();
	private ArrayList<FeedElement> mTweets = new ArrayList<FeedElement>();
	
	public SocialMediaUser(String name, 
			String prefKey, 
			int userId,
			String twitterUserId, 
			String facebookUserName, 
			String facebookUserId){
		
		mName = name;
		mUserId = userId;
		mPrefKey = prefKey;
		mTwitterUserId = twitterUserId;
		mFacebookUserName = facebookUserName;
		mFacebookUserId = facebookUserId;
	}
	
	public boolean isEnabled(Activity activity) {
		// Get the app's shared preferences
		SharedPreferences pref = 
		        PreferenceManager.getDefaultSharedPreferences(activity);
			return pref.getBoolean(mPrefKey, true);
	}

	public String getName() {
		return mName;
	}

	public String getFacebookUserName() {
		return mFacebookUserName;
	}

	public int getUserId() {
		return mUserId;
	}

	public Long getTwitterUserId() {
		return Long.valueOf(mTwitterUserId);
	}

	public String getFacebookUserId() {
		return mFacebookUserId;
	}

	public ArrayList<FeedElement> getPosts() {
		return mPosts;
	}
	
	public ArrayList<FeedElement> getTweets() {
		return mTweets;
	}

	public void setPosts(ArrayList<FeedElement> posts) {
		mPosts = posts;
		Collections.sort(mPosts);
	}

	public void setTweets(ArrayList<FeedElement> tweets) {
		mTweets = tweets;
		Collections.sort(mTweets);
	}

	public User getTwitterUser() {
		return mTwitterUser;
	}

	public void setTwitterUser(User twitterUser) {
		mTwitterUser = twitterUser;
	}
}
