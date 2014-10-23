package net.archenemy.archenemyapp.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.logic.BandMember;
import net.archenemy.archenemyapp.ui.FacebookShareElement;
import net.archenemy.archenemyapp.ui.FeedListElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FacebookAdapter {
	
	protected static final String TAG = "FacebookAdapter";
	private static final String PREF_KEY = "pref_facebook_key";
	
	// Activity code to flag an incoming activity result is due 
	// to a new permissions request
	public static final int REAUTH_ACTIVITY_CODE = 100;

	/// List of additional write permissions being requested
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "public_profile");
	
	// Redirect URL for authentication errors requiring a user action
	private static final Uri FACEBOOK_URL = Uri.parse("http://m.facebook.com");
		
	// JSON Node names
	private static final String TAG_DATA = "data";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PICTURE = "picture";
	private static final String TAG_LINK = "link";
	private static final String TAG_DATE = "created_time";
	
	private Activity mActivity;
	private DataAdapter mDataAdapter;
	private ProgressDialog mProgressDialog;
	
	private boolean mPendingPublishReauthorization = false;
	
	public FacebookAdapter(Activity activity){
		mActivity = activity;
		mDataAdapter = new DataAdapter(mActivity);
	}
	
	public boolean isEnabled() {
		// Get the app's shared preferences
//		SharedPreferences pref = 
//		        PreferenceManager.getDefaultSharedPreferences(mActivity);
//			return pref.getBoolean(PREF_KEY, true);
		return true;
	}
	
	public boolean isLoggedIn() {
		//check for open facebook session
        Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) 
			return true;
		return false;
	}
	
	public static Date getDate(String timestamp){
		SimpleDateFormat ft = 
			new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+'0000'", Locale.US);
		Date date = null; 
		try { 
		    date = ft.parse(timestamp);  
		} catch (ParseException e) { 
		    Log.e(TAG,"Unparseable using " + ft); 
		}
		return date;		
	}
	
	public void makeUserRequest(UserCallback userCallback) {
		if (Utility.isConnectedToNetwork(mActivity, false)){
		final Session session = Session.getActiveSession();
		final UserCallback callback = userCallback;
		if (session != null && session.isOpened()) {

	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
		    Request request = Request.newMeRequest(session, 
		            new Request.GraphUserCallback() {
		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            // If the response is successful
		        	Log.i(TAG, "User received");
		            if (session == Session.getActiveSession()) {
		                callback.onUserRequestCompleted(user);
		            }
		            if (response.getError() != null) {
		            	handleError(response.getError());
		            }
		        }
		    });
			Log.i(TAG, "Make user request");
		    request.executeAsync();
		}
		}
	}
	
	public void makeFeedRequest(FeedCallback feedCallback, final BandMember member){
		if (Utility.isConnectedToNetwork(mActivity, false)){
		final Session session = Session.getActiveSession();
		final FeedCallback callback = feedCallback;
		StringBuffer query = new StringBuffer(member.getFacebookUserId());
		query.append("/feed");
		// make the API call
		Request request = new Request(
		    session,
		    query.toString(),
		    null,
		    HttpMethod.GET,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		        	Log.i(TAG, "Feeds received");		        			        	
		        	if (response.getError() != null) {
		            	handleError(response.getError());
		            	return;
		            }
		        	// If the response is successful
		        	if (session == Session.getActiveSession()) {
		        		//Evaluate response
		                JSONObject graphResponse = response
		                                           .getGraphObject()
		                                           .getInnerJSONObject();
		        		ArrayList<FeedListElement> elements = parseJson(graphResponse);
		        		member.setFacebookFeedElements(elements);
		                callback.onFeedRequestCompleted(member);
		            }
		        }
		    }
		);
		Log.i(TAG, "Make feed request");
		request.executeAsync();	
		}
	}
	
	public void publishStory(FacebookShareElement shareElement) {
		if (Utility.isConnectedToNetwork(mActivity, true)){
		setPendingPublish(false);
		
	    Session session = Session.getActiveSession();
	
	    if (session != null){
	
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            setPendingPublish(true);
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(mActivity, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	        
	
	        Bundle postParams = shareElement.getPostingParameters();
	
	
	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	            	
	            	//Dismiss progress dialog
	            	if (mProgressDialog != null) {
	                    mProgressDialog.dismiss();
	                    mProgressDialog = null;
	                }
	            	
	            	//Evaluate response
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                
	                String successMessage = mActivity.getString(R.string.result_dialog_button_text);	               
	                FacebookRequestError error = response.getError();
	                
	                //Show result toast
	                if (error != null) {
	                    Toast.makeText(mActivity
	                         .getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(mActivity
	                             .getApplicationContext(), 
	                             successMessage,
	                             Toast.LENGTH_SHORT).show();
	                }
	            }
	        };
	
	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);
	
	        RequestAsyncTask requestTask = new RequestAsyncTask(request);
	        
	        //Show progress dialog
	        mProgressDialog = ProgressDialog.show(mActivity, "", 
	                mActivity.getResources()
	                .getString(R.string.progress_dialog_text), true);
			Log.i(TAG, "Publish story");
	        requestTask.execute();	
	    }
		}
	}

	private void requestPublishPermissions(Session session) {
	    if (session != null) {
	        Session.NewPermissionsRequest newPermissionsRequest = 
	            new Session.NewPermissionsRequest(mActivity, PERMISSIONS).
	                setRequestCode(REAUTH_ACTIVITY_CODE);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	    }
	}
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}

	private void handleError(FacebookRequestError error) {
	    DialogInterface.OnClickListener listener = null;
	    String dialogBody = null;

	    if (error == null) {
	        // There was no response from the server.
	        dialogBody = mActivity.getString(R.string.error_dialog_default_text);
	    } else {
	        switch (error.getCategory()) {
	            case AUTHENTICATION_RETRY:
	                // Tell the user what happened by getting the
	                // message id, and retry the operation later.
	                String userAction = (error.shouldNotifyUser()) ? "" :
	                	mActivity.getString(error.getUserActionMessageId());
	                dialogBody = mActivity.getString(R.string.error_authentication_retry, 
	                                       userAction);
	                listener = new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, 
	                                        int i) {
	                        // Take the user to the mobile site.
	                        Intent intent = new Intent(Intent.ACTION_VIEW, 
	                                                   FACEBOOK_URL);
	                        mActivity.startActivity(intent);
	                    }
	                };
	                break;

	            case AUTHENTICATION_REOPEN_SESSION:
	                // Close the session and reopen it.
	                dialogBody = 
	                		mActivity.getString(R.string.error_authentication_reopen);
	                listener = new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, 
	                                        int i) {
	                        Session session = Session.getActiveSession();
	                        if (session != null && !session.isClosed()) {
	                            session.closeAndClearTokenInformation();
	                        }
	                    }
	                };
	                break;

	            case PERMISSION:
	                // A permissions-related error
	                dialogBody = mActivity.getString(R.string.error_permission);
	                listener = new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, 
	                                        int i) {
	                    	//new
	                        setPendingPublish(true);
	                        // Request publish permission
	                        requestPublishPermissions(Session.getActiveSession());
	                    }
	                };
	                break;

	            case SERVER:
	            case THROTTLING:
	                // This is usually temporary, don't clear the fields, and
	                // ask the user to try again.
	                dialogBody = mActivity.getString(R.string.error_server);
	                break;

	            case BAD_REQUEST:
	                // This is likely a coding error, ask the user to file a bug.
	                dialogBody = mActivity.getString(R.string.error_bad_request, 
	                                       error.getErrorMessage());
	                break;

	            case OTHER:
	            case CLIENT:
	            default:
	                // An unknown issue occurred, this could be a code error, or
	                // a server side issue, log the issue, and either ask the
	                // user to retry, or file a bug.
	                dialogBody = mActivity.getString(R.string.error_unknown, 
	                                       error.getErrorMessage());
	                break;
	        }
	    }

	    // Show the error and pass in the listener so action
	    // can be taken, if necessary.
	    new AlertDialog.Builder(mActivity)
	            .setPositiveButton(R.string.error_dialog_button_text, listener)
	            .setTitle(R.string.error_dialog_title)
	            .setMessage(dialogBody)
	            .show();
	}
	
	public boolean isPendingPublish() {
		return mPendingPublishReauthorization;
	}

	public void setPendingPublish(boolean mPendingPublishReauthorization) {
		this.mPendingPublishReauthorization = mPendingPublishReauthorization;
	}

	private ArrayList<FeedListElement> parseJson (JSONObject jsonObj){
		ArrayList<FeedListElement> feedElements = new ArrayList<FeedListElement>();
		JSONArray posts = null;
		Log.e(TAG, "Parse response...");
		if (jsonObj != null) {    		                    
	        try {
				posts = jsonObj.getJSONArray(TAG_DATA);					
				for (int i = 0; i < posts.length(); i++) {
					try {
						JSONObject object = posts.getJSONObject(i);
	             
						String date = object.getString(TAG_DATE);
						String message = object.getString(TAG_MESSAGE);
						String picture = object.getString(TAG_PICTURE);
						String link = object.getString(TAG_LINK);
						String id = object.getString(TAG_ID);
						String name = object.getString(TAG_NAME);
	            
						FeedListElement element = 
								new FeedListElement.FacebookElement(mActivity,name, id, message, date, picture, link);
						feedElements.add(element);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
	        } catch (JSONException e1) {
	        	//ignore objects with missing tags
	        }
	    
		} else {
			Log.e(TAG, "Couldn't parse response");
		}
	    return feedElements;
	}

	public interface UserCallback {
		void onUserRequestCompleted(GraphUser user);
	}
	public interface FeedCallback {
		void onFeedRequestCompleted(BandMember member);
	}

}


