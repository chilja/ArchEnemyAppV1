package net.archenemy.archenemyapp.data;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import net.archenemy.archenemyapp.ui.ListElement;
import net.archenemy.archenemyapp.ui.Tweet;

import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.util.OAuthConfig;

import twitter4j.MediaEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAdapter 
	implements ProviderAdapter{
	
	public static final String TAG = "TwitterAdapter";
	
	// twitter4j
	// Preference Constants
	static final String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String PREF_KEY_TWITTER_USER_ID = "twitterUserId";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String TWITTER_CALLBACK_URL = "oauth://ArchEnemyApp";
	
	private Activity mActivity;
	private DataAdapter mDataAdapter;

	private String mConsumerKey;
	private String mConsumerSecret;
	
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

    public boolean isEnabled() {
		return true;
	}

    public void logIn() {
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
	
	public void logOut() {
	// Clear the shared preferences
	    Editor editor = mSharedPreferences.edit();
	    editor.remove(PREF_KEY_OAUTH_TOKEN);
	    editor.remove(PREF_KEY_OAUTH_SECRET);
	    editor.remove(PREF_KEY_TWITTER_LOGIN);
	    editor.commit();    
	}

	public void makeTokenRequest(Uri uri, TokenCallback callback) {
		if (!isLoggedIn()) {
	        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {	        	
	        	TokenTask task = new TokenTask(callback);
	        	task.execute(uri);
	        }
	    }
	}
	
	public void makeUserRequest(Long userId, UserCallback callback) {
		if (isLoggedIn()) {
			Log.i(TAG, "Get user ...");
			new UserTask(callback).execute(userId);
		}
	}
	
	public void makeMeRequest(UserCallback callback) {
		if (isLoggedIn()) {
			Log.i(TAG, "Get user ...");
        	// Getting user details from twitter
        	Long userId = mSharedPreferences.getLong(PREF_KEY_TWITTER_USER_ID,0l);
			new UserTask(callback).execute(userId);
		}
	}

	public void makeFeedRequest(Long id, final FeedCallback callback) {
		if (isEnabled() && isLoggedIn()) {
			Log.d(TAG, "Get feeds...");
			new FeedTask(callback, id).execute();
		}
	}

	public static Date getDate(String timestamp){
		SimpleDateFormat ft = 
			new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss+'0000'", Locale.US);
		Date date = null; 
		try { 
		    date = ft.parse(timestamp);  
		} catch (ParseException e) { 
		    Log.e(TAG,"Unparseable using " + ft); 
		}
		return date;		
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
				e.printStackTrace();
			}
		} 
	}
	
	private Twitter getAuthorizedTwitterInstance() {
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
        return twitter;	
	}
	
	public interface FeedCallback {
		void onFeedRequestCompleted(ArrayList<ListElement> elements, Long id);
	}
    
    public interface UserCallback {
		void onUserRequestCompleted(User user);
	}
    
    public interface TokenCallback {
		void onTokenRequestCompleted();
	}
 	
	private class FeedTask extends AsyncTask<Void, Void, ArrayList<ListElement>> {

		private FeedCallback mCallback;
		private Long mId;

		private FeedTask(FeedCallback callback, Long id) {
			mCallback = callback;
			mId = id;
		}

		@Override
		protected ArrayList<ListElement> doInBackground(Void... params) {
	        
	        try {
	            Twitter twitter = getAuthorizedTwitterInstance();
	            return getFeedElements(twitter.getUserTimeline(mId));		                        
	        } catch (TwitterException te) {
	            te.printStackTrace();
	        }
	        return null;
	    }
		
		private ArrayList<ListElement> getFeedElements(List<twitter4j.Status> statuses){
			ArrayList<ListElement> listElements= new ArrayList<ListElement>();
			for (twitter4j.Status status : statuses) {
				URLEntity[] urlEntities = status.getURLEntities();
				String link = null;
				if (urlEntities != null && urlEntities.length>0) {
					URLEntity url = urlEntities[0];
					link = url.getExpandedURL();
				}
				String url = link;
				MediaEntity[] media = status.getMediaEntities();
				for (MediaEntity entity : media) {
					url = entity.getMediaURL();
					String type = entity.getType();
				}
				ListElement element = 
						new Tweet(mActivity, 
								status.getUser().getScreenName(), status.getText(), status.getCreatedAt(), url);
				listElements.add(element);
	        }
			return listElements;
		}

		@Override
		protected void onPostExecute(ArrayList<ListElement> elements) {			
			mCallback.onFeedRequestCompleted(elements, mId);
		}
	}
	
	private class UserTask extends AsyncTask<Long, Void, User> {

		UserCallback mCallback;

		private UserTask(UserCallback callback) {
			mCallback = callback;
		}

		@Override
		protected User doInBackground(Long... params) {
	        
	        try {
		        Twitter twitter =  getAuthorizedTwitterInstance();
		        Long userId = params[0];
	            User user = twitter.showUser(userId);
	            return user;
	            
	        } catch (TwitterException te) {
	            te.printStackTrace();
	        }
	        return null;
	    }

		@Override
		protected void onPostExecute(User user) {
			mCallback.onUserRequestCompleted(user);
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
	
	private class TokenTask extends AsyncTask<Uri, Void, Void> {
		TokenCallback mCallback;

		private TokenTask(TokenCallback callback) {
			mCallback = callback;
		}

		@Override
		protected Void doInBackground(Uri... uris) {
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
			         return null;
			     }
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v) {
			if (mCallback != null)
				mCallback.onTokenRequestCompleted();		
		}
	}
}