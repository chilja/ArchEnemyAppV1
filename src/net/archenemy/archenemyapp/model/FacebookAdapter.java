package net.archenemy.archenemyapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.presenter.FeedElement;
import net.archenemy.archenemyapp.presenter.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request.Callback;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookAdapter 
	implements ProviderAdapter{
	
	protected static final String TAG = "FacebookAdapter";
	
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
	private static final String TAG_FROM = "from";
	
	private ProgressDialog mProgressDialog;
	
	// flag for pending reauthorization request
	private boolean mPendingPublishReauthorization = false;
	
	private static FacebookAdapter mFacebookAdapter;
		
	public static FacebookAdapter getInstance() {
		if (mFacebookAdapter == null) {
			mFacebookAdapter = new FacebookAdapter();
		}		
		return mFacebookAdapter;
	}
	
	private FacebookAdapter(){
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public boolean isLoggedIn() {
		//check for open facebook session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) 
			return true;
		return false;
	}
	
	public boolean hasValidToken() {
		//check for open facebook session
        Session session = Session.getActiveSession();
        if (session != null) {
	        String token = session.getAccessToken();
	        Date expDate = session.getExpirationDate();
	        Date date = new Date();
	        if (token != null && (date.before(expDate)))
	        	return true;
	        }
		return false;
	}
	
	public boolean isPendingPublish() {
		return mPendingPublishReauthorization;
	}

	public void setPendingPublish(boolean mPendingPublishReauthorization) {
		this.mPendingPublishReauthorization = mPendingPublishReauthorization;
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
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}

	private ArrayList<FeedElement> parseJson (JSONObject jsonObj, Activity activity){
		
		ArrayList<FeedElement> feedElements = new ArrayList<FeedElement>();
		JSONArray posts = null;
		Log.i(TAG, "Parse response...");
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
						JSONObject fromObj = object.getJSONObject(TAG_FROM);
						String name = fromObj.getString(TAG_NAME);
						String id = fromObj.getString(TAG_ID);
	            
						FeedElement element = 
								new Post(activity,name, id, message, date, picture, link);
						feedElements.add(element);
					} catch (JSONException e) {
						//ignore objects with missing tags
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

	private void requestPublishPermissions(Session session, Activity activity) {
	    if (session != null) {
	        Session.NewPermissionsRequest newPermissionsRequest = 
	            new Session.NewPermissionsRequest(activity, PERMISSIONS).
	                setRequestCode(REAUTH_ACTIVITY_CODE);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	    }
	}
	
	public void startShareDialog(Bundle shareParams, Activity activity) {	
		if (FacebookDialog.canPresentShareDialog(activity.getApplicationContext(), 
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
						
			// Publish the post using the Native Facebook Share Dialog			
			FacebookDialog.ShareDialogBuilder shareDialogBuilder = new FacebookDialog.ShareDialogBuilder(activity);
			shareDialogBuilder.setName(shareParams.getString("name"));
			shareDialogBuilder.setLink(shareParams.getString("link"));
			shareDialogBuilder.setCaption(shareParams.getString("caption"));
			shareDialogBuilder.setDescription(shareParams.getString("description"));
			shareDialogBuilder.setPicture(shareParams.getString("picture"));
			FacebookDialog shareDialog = shareDialogBuilder.build();
			shareDialog.present();

		} else {
			//Publish the post using the custom share dialog
			publishFeedDialog(shareParams, activity);
		}
	}

	public void makeMeRequest(UserCallback userCallback, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, false)){
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
			            	handleError(response.getError(), activity);
			            }
			        }
			    });
				Log.i(TAG, "Make user request");
			    request.executeAsync();
			}
		}
	}
	
	public void makeUserRequest(final UserCallback callback, final String id, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, false)){
			final Session session = Session.getActiveSession();
			if (session != null && session.isOpened()) {
				
		        Callback wrapper = new Callback() {
		            @Override
		            public void onCompleted(Response response) {
		            	
		            	Log.i(TAG, "user received");		        			        	
			        	if (response.getError() != null) {
			            	handleError(response.getError(), activity);
			            	return;
			            }
			        	// If the response is successful
			        	if (session == Session.getActiveSession()) {
			        		//Evaluate response
			                GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
			                if (callback != null) {
			                	callback.onUserRequestCompleted(graphUser);
			                }
			        	}
		            }
		        };
		        Request request = new Request(session, id, null, null, wrapper);
		        request.executeAsync();
			}
		}
	}
	
	public void makeFeedRequest(final FeedCallback callback, final String id, final Activity activity){
		if (Utility.isConnectedToNetwork(activity, false)){
		final Session session = Session.getActiveSession();
		StringBuffer query = new StringBuffer(id);
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
		            	handleError(response.getError(), activity);
		            	callback.onFeedRequestCompleted(null, id);
		            	return;
		            }
		        	// If the response is successful
		        	if (session == Session.getActiveSession()) {
		        		//Evaluate response
		                JSONObject graphResponse = response
		                                           .getGraphObject()
		                                           .getInnerJSONObject();
		        		ArrayList<FeedElement> elements = parseJson(graphResponse, activity);
		                callback.onFeedRequestCompleted(elements, id);
		            }
		        }
		    }
		);
		Log.i(TAG, "Make feed request");
		request.executeAsync();	
		}
	}
	
	public void publishFeedDialog(Bundle params, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, true) && isLoggedIn()){

		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(activity,
		            Session.getActiveSession(),
		            params))
		        .setOnCompleteListener(new OnCompleteListener() {
	
		            @Override
		            public void onComplete(Bundle values,
		                FacebookException error) {
		                if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                        Toast.makeText(activity,
		                            "Posted story, id: "+postId,
		                            Toast.LENGTH_SHORT).show();
		                    } else {
		                        // User clicked the Cancel button
		                        Toast.makeText(activity.getApplicationContext(), 
		                            "Publish cancelled", 
		                            Toast.LENGTH_SHORT).show();
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                    // User clicked the "x" button
		                    Toast.makeText(activity.getApplicationContext(), 
		                        "Publish cancelled", 
		                        Toast.LENGTH_SHORT).show();
		                } else {
		                    // Generic, ex: network error
		                    Toast.makeText(activity.getApplicationContext(), 
		                        "Error posting story", 
		                        Toast.LENGTH_SHORT).show();
		                }
		            }
		        })
		        .build();
		    feedDialog.show();
		}
		if (!isLoggedIn()) {
			Toast.makeText(activity, R.string.fb_share_error_log_in, Toast.LENGTH_LONG).show();
		}
	}
	
	public void publishStory(Bundle shareParams, final Activity activity) {
		if (Utility.isConnectedToNetwork(activity, true)){
		setPendingPublish(false);
		
	    Session session = Session.getActiveSession();
	
	    if (session != null){
	
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            setPendingPublish(true);
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(activity, PERMISSIONS);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
	
	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	            	
	            	//Dismiss progress dialog
	            	if (mProgressDialog != null) {
	                    mProgressDialog.dismiss();
	                    mProgressDialog = null;
	                }
	                
	                String successMessage = activity.getString(R.string.fb_result_dialog_button_text);	               
	                FacebookRequestError error = response.getError();
	                
	                //Show result toast
	                if (error != null) {
	                    Toast.makeText(activity
	                         .getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(activity
	                             .getApplicationContext(), 
	                             successMessage,
	                             Toast.LENGTH_SHORT).show();
	                }
	            }
	        };
	
	        Request request = new Request(session, "me/feed", shareParams, 
	                              HttpMethod.POST, callback);
	
	        RequestAsyncTask requestTask = new RequestAsyncTask(request);
	        
	        //Show progress dialog
	        mProgressDialog = ProgressDialog.show(activity, "", 
	                activity.getResources()
	                .getString(R.string.fb_progress_dialog_text), true);
			Log.i(TAG, "Publish story");
	        requestTask.execute();	
	    }
		}
	}

	private void handleError(FacebookRequestError error, final Activity activity) {
	    DialogInterface.OnClickListener listener = null;
	    String dialogBody = null;

	    if (error == null) {
	        // There was no response from the server.
	        dialogBody = activity.getString(R.string.fb_error_dialog_default_text);
	    } else {
	        switch (error.getCategory()) {
	            case AUTHENTICATION_RETRY:
	                // Tell the user what happened by getting the
	                // message id, and retry the operation later.
	                String userAction = (error.shouldNotifyUser()) ? "" :
	                	activity.getString(error.getUserActionMessageId());
	                dialogBody = activity.getString(R.string.fb_error_authentication_retry, 
	                                       userAction);
	                listener = new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, 
	                                        int i) {
	                        // Take the user to the mobile site.
	                        Intent intent = new Intent(Intent.ACTION_VIEW, 
	                                                   FACEBOOK_URL);
	                        activity.startActivity(intent);
	                    }
	                };
	                break;

	            case AUTHENTICATION_REOPEN_SESSION:
	                // Close the session and reopen it.
	                dialogBody = 
	                		activity.getString(R.string.fb_error_authentication_reopen);
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
	                dialogBody = activity.getString(R.string.fb_error_permission);
	                listener = new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, 
	                                        int i) {
	                    	//new
	                        setPendingPublish(true);
	                        // Request publish permission
	                        requestPublishPermissions(Session.getActiveSession(), activity);
	                    }
	                };
	                break;

	            case SERVER:
	            case THROTTLING:
	                // This is usually temporary, don't clear the fields, and
	                // ask the user to try again.
	                dialogBody = activity.getString(R.string.fb_error_server);
	                break;

	            case BAD_REQUEST:
	                // This is likely a coding error, ask the user to file a bug.
	                dialogBody = activity.getString(R.string.fb_error_bad_request, 
	                                       error.getErrorMessage());
	                break;

	            case OTHER:
	            case CLIENT:
	            default:
	                // An unknown issue occurred, this could be a code error, or
	                // a server side issue, log the issue, and either ask the
	                // user to retry, or file a bug.
	                dialogBody = activity.getString(R.string.fb_error_unknown, 
	                                       error.getErrorMessage());
	                break;
	        }
	    }

	    // Show the error and pass in the listener so action
	    // can be taken, if necessary.
	    new AlertDialog.Builder(activity)
	            .setPositiveButton(R.string.fb_error_dialog_button_text, listener)
	            .setTitle(R.string.fb_error_dialog_title)
	            .setMessage(dialogBody)
	            .show();
	}
	
	public interface UserCallback {
		void onUserRequestCompleted(GraphUser user);
	}
	public interface FeedCallback {
		void onFeedRequestCompleted(ArrayList<FeedElement> elements, String id);
	}

}


