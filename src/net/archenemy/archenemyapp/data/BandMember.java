package net.archenemy.archenemyapp.data;

import java.util.ArrayList;
import java.util.Collections;

import com.facebook.model.GraphUser;

import net.archenemy.archenemyapp.ui.ListElement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BandMember {
	
	private String mName;
	private String mTwitterUser;
	private String mTwitterUserId;
	private String mFacebookUser;
	private String mFacebookUserId;
	private String mPrefKey;
	private int mUserId;
	private GraphUser mGraphUser;

	private ArrayList<ListElement> mPosts = new ArrayList<ListElement>();
	private ArrayList<ListElement> mTweets = new ArrayList<ListElement>();
	
	public BandMember(String name, String prefKey, int userId,
								String twitterUser, String twitterUserId, 
								String facebookUser, String facebookUserId){
		mName = name;
		mUserId = userId;
		mPrefKey = prefKey;
		mTwitterUser = twitterUser;
		mTwitterUserId = twitterUserId;
		mFacebookUser = facebookUser;
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

	public String getTwitterUser() {
		return mTwitterUser;
	}

	public String getFacebookUser() {
		return mFacebookUser;
	}

	public int getUserId() {
		return mUserId;
	}

	public String getTwitterUserId() {
		return mTwitterUserId;
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

	public GraphUser getGraphUser() {
		return mGraphUser;
	}
	
	public void setGraphUser(GraphUser graphUser) {
		mGraphUser = graphUser;
	}

}
