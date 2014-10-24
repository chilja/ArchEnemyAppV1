package net.archenemy.archenemyapp.data;

import java.util.ArrayList;
import java.util.Collections;

import net.archenemy.archenemyapp.ui.FeedListElement;
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

	private ArrayList<FeedListElement> mFacebookFeedElements = new ArrayList<FeedListElement>();
	private ArrayList<FeedListElement> mTwitterFeedElements = new ArrayList<FeedListElement>();
	
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

	public ArrayList<FeedListElement> getFacebookFeedElements() {
		return mFacebookFeedElements;
	}
	
	public ArrayList<FeedListElement> getTwitterFeedElements() {
		return mTwitterFeedElements;
	}

	public void setFacebookFeedElements(ArrayList<FeedListElement> facebookFeedElements) {
		mFacebookFeedElements = facebookFeedElements;
		Collections.sort(mFacebookFeedElements);
	}

	public void setTwitterFeedElements(ArrayList<FeedListElement> twitterFeedElements) {
		mTwitterFeedElements = twitterFeedElements;
		Collections.sort(mTwitterFeedElements);
	}
}
