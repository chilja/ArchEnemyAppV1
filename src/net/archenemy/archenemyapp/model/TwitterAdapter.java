package net.archenemy.archenemyapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import net.archenemy.archenemyapp.presenter.FeedElement;
import net.archenemy.archenemyapp.presenter.Tweet;

import twitter4j.MediaEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import com.twitter.sdk.android.core.TwitterAuthToken;
//import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

public class TwitterAdapter 
	implements ProviderAdapter{
	
	public static final String TAG = "TwitterAdapter";

	private static final String KEY = "DqhZFiXkeerd1gaqeD1reFmVX";
	private static final String SECRET = "pcpFXsv1YFqQ1BncuQw5qzBMvl9Ow3TUPKG2oFHnuR5RG9e2Ab";
	
    public TwitterAdapter(Activity activity) {
	}

    public boolean isEnabled() {
		return true;
	}

    public boolean isLoggedIn() {   	
    	if (com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession() != null) {    		
        	return true;
    	}
    			   	
    	return false;
    }
	
	public void logOut() { 
		com.twitter.sdk.android.Twitter.getSessionManager().clearActiveSession();
	}
	
	public void makeUserRequest(Long userId, UserCallback callback) {
		if (isLoggedIn()) {
			Log.i(TAG, "Get user ...");
			new UserTask(callback).execute(userId);
		}
	}
	
	public String getUserName() {
		TwitterSession session =
    			com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession();
		
		return session.getUserName();
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
	
	private Twitter getAuthorizedTwitterInstance() {
		
    	TwitterSession session =
    			com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession();
    	TwitterAuthToken authToken = session.getAuthToken();
    	String token = authToken.token;
    	String secret = authToken.secret;
    	
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(KEY)
          .setOAuthConsumerSecret(SECRET)
          .setOAuthAccessToken(token)
          .setOAuthAccessTokenSecret(secret);
        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();
        return twitter;	
	}
	
	public interface FeedCallback {
		void onFeedRequestCompleted(ArrayList<FeedElement> elements, Long id);
	}
    
    public interface UserCallback {
		void onUserRequestCompleted(User user);
	}
 	
	private class FeedTask extends AsyncTask<Void, Void, ArrayList<FeedElement>> {

		private FeedCallback mCallback;
		private Long mId;
		Twitter twitter;

		private FeedTask(FeedCallback callback, Long id) {
			mCallback = callback;
			mId = id;
			twitter = getAuthorizedTwitterInstance();
		}

		@Override
		protected ArrayList<FeedElement> doInBackground(Void... params) {
	        
	        try {
	            
	            return getFeedElements(twitter.getUserTimeline(mId));		                        
	        } catch (TwitterException te) {
	            te.printStackTrace();
	        }
	        return null;
	    }
		
		private ArrayList<FeedElement> getFeedElements(List<twitter4j.Status> statuses){
			ArrayList<FeedElement> feedElements= new ArrayList<FeedElement>();
			for (twitter4j.Status status : statuses) {
				URLEntity[] urlEntities = status.getURLEntities();
				String link = null;
				if (urlEntities != null && urlEntities.length>0) {
					URLEntity url = urlEntities[0];
					link = url.getExpandedURL();
				}
				String url = null;
				MediaEntity[] media = status.getMediaEntities();
				for (MediaEntity entity : media) {
					url = entity.getMediaURL();
					entity.getType();
					break;
				}
				//use media url as link if no other link is provided
				if (link == null)
					link = url;
				
				FeedElement element;
				if (url == null) {				
				element = 
						new Tweet( 
								status.getUser().getScreenName(), status.getText(), status.getCreatedAt(), link);
				}else {
					element = 
							new Tweet( 
									status.getUser().getScreenName(), status.getText(), status.getCreatedAt(), link, url);
				}
				feedElements.add(element);
	        }
			return feedElements;
		}

		@Override
		protected void onPostExecute(ArrayList<FeedElement> elements) {			
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
}