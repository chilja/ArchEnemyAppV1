package net.archenemy.archenemyapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

public class Utility {
	
	public static boolean isConnectedToNetwork(Activity activity, boolean makeToast){
		//check internet connection
    	ConnectivityManager connMgr = (ConnectivityManager) 
    	        activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	return true;
        } else {
        	if (makeToast) 
        		Toast.makeText(activity,"No network connection available", Toast.LENGTH_LONG).show();
        	return false;
        }
	}
	
	public static void startBrowserActivity(Activity activity, String uri) {
		uri = uri.trim();
		Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse(uri));
    	activity.startActivity(intent);
	}

}
