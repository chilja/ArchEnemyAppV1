package net.archenemy.archenemyapp.data;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.archenemy.archenemyapp.logic.BandMember;
import net.archenemy.archenemyapp.ui.FeedListElement;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.util.OAuthConfig;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAdapter {
	
	private static final String TAG = "TwitterAdapter";
	
	
	private boolean mIsLoggedIn = false;
	
	private Activity mActivity;
	private DataAdapter mDataAdapter;

	private String mConsumerKey;
	private String mConsumerSecret;	

	//twitter4j
	// Preference Constants
	private static final String PREF_KEY = "pref_twitter_key";
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String PREF_KEY_TWITTER_USER_ID = "twitterUserId";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String TWITTER_CALLBACK_URL = "oauth://ArchEnemyApp";
	
    // Twitter
    private static Twitter mTwitter;
    private static RequestToken mRequestToken;
	     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
	
   public TwitterAdapter(Activity activity) {
		mActivity = activity;
		loadConfig();
		mSharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(mActivity);
		mDataAdapter = new DataAdapter(mActivity);
	}

   public void login() {
       // Check if already logged in
       if (!isLoggedIn()) {
    	   Toast.makeText(mActivity,"Connecting...", Toast.LENGTH_LONG).show();
           new LoginTask().execute();
          
       } else {
           // user already logged in with twitter
           Toast.makeText(mActivity,
                   "Already Logged in with twitter", Toast.LENGTH_LONG).show();
       }
   }

   public boolean isLoggedIn() {
       // return twitter login status from Shared Preferences
       return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
   }
	
	public void authorize(Uri uri, ConnectionCallback callback) {
		if (!isLoggedIn()) {
	        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {	        	
	        	AuthorizeTask task = new AuthorizeTask(callback);
	        	task.execute(uri);
	        }
	    }
	}
	
	public static Date getDate(String timestamp){
		SimpleDateFormat ft = 
			new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss+'0000'");
		Date date = null; 
		try { 
		    date = ft.parse(timestamp);  
		} catch (ParseException e) { 
		    Log.e(TAG,"Unparseable using " + ft); 
		}
		return date;		
	}
	
	public void getFeed(final FeedCallback callback) {
		if (isEnabled()) {
			Log.d(TAG, "Get feeds...");
			ArrayList<BandMember> members = mDataAdapter.getEnabledBandMembers();
			BandMember[] memberArray = new BandMember[members.size()];
			members.toArray(memberArray);
			new FeedTask(callback).execute(memberArray);
		}
	}

	private void loadConfig(){
		SocialAuthConfig config = new SocialAuthConfig();
		Resources resources = mActivity.getResources();
		AssetManager assetManager = resources.getAssets();
		InputStream inputStream = null;
		boolean fileExists = false;
		// Check oauth_consumer.properties file exist
		try {
			inputStream = assetManager.open("oauth_consumer.properties");
			fileExists = true;
		} catch (Exception e) {
			fileExists = false;
			Log.d(TAG, "oauth_consumer.properties not found");
		}

		if (fileExists) {
			// Add keys from oauth_consumers file.
			try {
				config.load(inputStream);			
				OAuthConfig con = config.getProviderConfig(Provider.TWITTER.toString());
				mConsumerKey = con.get_consumerKey();
				mConsumerSecret = con.get_consumerSecret();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	public boolean isEnabled() {
		// Get the app's shared preferences
//		SharedPreferences pref = 
//		        PreferenceManager.getDefaultSharedPreferences(mActivity);
//			return pref.getBoolean(PREF_KEY, true);
		return true;
	}
	
	public void getUserProfile(ProfileCallback callback) {
		Log.d(TAG, "Get profile...");
		new ProfileTask(callback).execute();
	}
	
	public void logOut(ConnectionCallback callback) {
		// Clear the shared preferences
	    Editor editor = mSharedPreferences.edit();
	    editor.remove(PREF_KEY_OAUTH_TOKEN);
	    editor.remove(PREF_KEY_OAUTH_SECRET);
	    editor.remove(PREF_KEY_TWITTER_LOGIN);
	    editor.commit();
	    mIsLoggedIn = false;
		callback.onLogoutCompleted(mIsLoggedIn);
	}
	    
    public interface FeedCallback extends ConnectionCallback {
//    	ArrayList<FeedListElement> getFeedElements();
		void onFeedRequestCompleted();
	}
    
    public interface ProfileCallback extends ConnectionCallback {
		void onProfileRequestCompleted(User user);
	}
    
    public interface ConnectionCallback {
//    	TwitterAdapter getTwitterAdapter();
//		void onConnectionRequestCompleted();
		void onLogoutCompleted(boolean isLoggedIn);
		void onAuthorizationCompleted(Boolean isAuthorized);
	}
 	
	private class FeedTask extends AsyncTask<BandMember, Void, Void> {

		FeedCallback mCallback;

		private FeedTask(FeedCallback callback) {
			mCallback = callback;
		}

		@Override
		protected Void doInBackground(BandMember... members) {
	        
	        try {
	        	// Access Token 
	            String token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
	            // Access Token Secret
	            String tokenSecret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
	        	
	            ConfigurationBuilder cb = new ConfigurationBuilder();
	            cb.setDebugEnabled(true)
	              .setOAuthConsumerKey(mConsumerKey)
	              .setOAuthConsumerSecret(mConsumerSecret)
	              .setOAuthAccessToken(token)
	              .setOAuthAccessTokenSecret(tokenSecret);
	            TwitterFactory factory = new TwitterFactory(cb.build());
	            Twitter twitter = factory.getInstance();
	            for (int i = 0; i<members.length; i++){
	            	members[i].setTwitterFeedElements(
	            			getFeedElements(twitter.getUserTimeline(Long.valueOf(members[i].getTwitterUserId()))));	
	            }	            
	            return null;
	            
	        } catch (TwitterException te) {
	            te.printStackTrace();
	        }
	        return null;
	    }
		
		private ArrayList<FeedListElement> getFeedElements(List<twitter4j.Status> statuses){
			ArrayList<FeedListElement> listElements= new ArrayList<FeedListElement>();
			for (twitter4j.Status status : statuses) {
				URLEntity[] urlEntities = status.getURLEntities();
				String link = null;
				if (urlEntities != null && urlEntities.length>0) {
					URLEntity url = urlEntities[0];
					link = url.getExpandedURL();
				}
				FeedListElement.TwitterElement element = 
						new FeedListElement.TwitterElement(mActivity, 
								status.getUser().getScreenName(), status.getText(), status.getCreatedAt(), link);
				listElements.add(element);
	        }
			return listElements;
		}

		@Override
		protected void onPostExecute(Void v) {			
			mCallback.onFeedRequestCompleted();
		}
	}
	
	private class ProfileTask extends AsyncTask<Void, Void, User> {

		ProfileCallback mCallback;

		private ProfileTask(ProfileCallback callback) {
			mCallback = callback;
		}

		@Override
		protected User doInBackground(Void... params) {
	        
	        try {
	        	if (mTwitter == null) {
		        	// Access Token 
		            String accessToken = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
		            // Access Token Secret
		            String accessTokenSecret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
		            
		            ConfigurationBuilder cb = new ConfigurationBuilder();
		            cb.setDebugEnabled(true)
		              .setOAuthConsumerKey(mConsumerKey)
		              .setOAuthConsumerSecret(mConsumerSecret)
		              .setOAuthAccessToken(accessToken)
		              .setOAuthAccessTokenSecret(accessTokenSecret);
		            TwitterFactory factory = new TwitterFactory(cb.build());
		            mTwitter = factory.getInstance();
	        	}
	        	// Getting user details from twitter
	        	Long userId = mSharedPreferences.getLong(PREF_KEY_TWITTER_USER_ID,0l);
	            User user = mTwitter.showUser(userId);
	            return user;
	            
	        } catch (TwitterException te) {
	            te.printStackTrace();
	        }
	        return null;
	    }

		@Override
		protected void onPostExecute(User user) {
			mCallback.onProfileRequestCompleted(user);
		}
	}
	
	private class LoginTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mConsumerKey);
			builder.setOAuthConsumerSecret(mConsumerSecret);
			Configuration configuration = builder.build();
            
			TwitterFactory factory = new TwitterFactory(configuration);
			mTwitter = factory.getInstance();
			try {
               mRequestToken = mTwitter
                       .getOAuthRequestToken(TWITTER_CALLBACK_URL);
               //open twitter web login with browser
               Utility.startBrowserActivity(mActivity, Uri.parse(mRequestToken.getAuthenticationURL()).toString());
			} catch (TwitterException e) {
               e.printStackTrace();
			}
			return null;
		}		
	}
	
	private class AuthorizeTask extends AsyncTask<Uri, Void, Boolean> {
		ConnectionCallback mCallback;

		private AuthorizeTask(ConnectionCallback callback) {
			mCallback = callback;
		}

		@Override
		protected Boolean doInBackground(Uri... uris) {
			if (uris.length > 0) {
				 // oAuth verifier
	            String verifier = uris[0]
	                    .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
				 try {
			         // Get the access token
			         AccessToken accessToken = mTwitter.getOAuthAccessToken(
			                 mRequestToken, verifier);
			
			         // Shared Preferences
			         Editor e = mSharedPreferences.edit();
			
			         // After getting access token, access token secret
			         // store them in application preferences
			         e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			         e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			         // Store login status - true
			         e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			         //store User id
			         e.putLong(PREF_KEY_TWITTER_USER_ID, accessToken.getUserId());
			         e.commit(); // save changes
			
			         Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
			
			     } catch (Exception e) {
			         // Check log for login errors
			         Log.e("Twitter Login Error", "> " + e.getMessage());
			         return false;
			     }
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean isAuthorized) {
			mCallback.onAuthorizationCompleted(isAuthorized);		
		}
	}
}