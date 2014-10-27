package net.archenemy.archenemyapp.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class ImageService extends IntentService {

	public ImageService(String name) {
		super(name);
		String status = "loaded";
		/*
	     * Creates a new Intent containing a Uri object
	     * BROADCAST_ACTION is a custom Intent action
	     */
	    Intent localIntent =
	            new Intent(Constants.BROADCAST_ACTION)
	            // Puts the status into the Intent
	            .putExtra(Constants.EXTENDED_DATA_STATUS, status );
	    // Broadcasts the Intent to receivers in this app.
	    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

	}

}
