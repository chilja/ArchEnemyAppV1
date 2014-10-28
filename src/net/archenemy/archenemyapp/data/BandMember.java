package net.archenemy.archenemyapp.data;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.User;

import com.facebook.model.GraphUser;

import net.archenemy.archenemyapp.ui.ListElement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BandMember {
	
	private String mName;
	private String mTwitterUserName;
	private String mTwitterUserId;
	private String mFacebookUserName;
	private String mFacebookUserId;
	private String mPrefKey;
	private int mUserId;
	private GraphUser mGraphUser;
	private User mTwitterUser;

	private ArrayList<ListElement> mPosts = new ArrayList<ListElement>();
	private ArrayList<ListElement> mTweets = new ArrayList<ListElement>();
	
	public BandMember(String name, String prefKey, int userId,
								String twitterUserName, String twitterUserId, 
								String facebookUserName, String facebookUserId){
		mName = name;
		mUserId = userId;
		mPrefKey = prefKey;
		mTwitterUserName = twitterUserName;
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

	public String getTwitterUserName() {
		return mTwitterUserName;
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

	public ArrayList<ListElement> getPosts() {
		return mPosts;
	}
	
	public ArrayList<ListElement> getTweets() {
		return mTweets;
	}

	public void setPosts(ArrayList<ListElement> posts) {
		mPosts = posts;
		Collections.sort(mPosts);
	}

	public void setTweets(ArrayList<ListElement> tweets) {
		mTweets = tweets;
		Collections.sort(mPosts);
	}

	public User getTwitterUser() {
		return mTwitterUser;
	}

	public void setTwitterUser(User twitterUser) {
		mTwitterUser = twitterUser;
	}

//	public GraphUser getGraphUser() {
//		return mGraphUser;
//	}
//	
//	public void setGraphUser(GraphUser graphUser) {
//		mGraphUser = graphUser;
//	}

}
