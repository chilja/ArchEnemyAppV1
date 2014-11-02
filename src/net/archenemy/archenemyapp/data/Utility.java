package net.archenemy.archenemyapp.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
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
        		Toast.makeText(activity,"No network connection available", Toast.LENGTH_SHORT).show();
        	return false;
        }
	}
	
	public static void startBrowserActivity(Activity activity, String uri) {
		if (uri != null && uri != "") {
			uri = uri.trim();
			Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent.setData(Uri.parse(uri));
	    	activity.startActivity(intent);
		}
	}
	
	
	public static void makeTextShareIntent(Activity activity, String message, String subject){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.setType("text/plain");
		activity.startActivity(Intent.createChooser(intent, "Share text to.."));;
	}
	
	public static boolean saveImageToInternalStorage(Activity activity, Bitmap image) {

		try {
		    // Use the compress method on the Bitmap object to write image to
		    // the OutputStream
		    FileOutputStream fos = activity.openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);
	
		    // Writing the bitmap to the output stream
		    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    fos.close();
	
		    return true;
	    } catch (Exception e) {
		    Log.e("saveToInternalStorage()", e.getMessage());
		    return false;
	    }
    }
	 
	public static void makeBitmapShareIntent(Activity activity, String bitmapUrl){
		
//		String FILENAME = "image";
//
//		FileOutputStream fos;
//		try {
//			fos = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
//			// Writing the bitmap to the output stream
//		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//			fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//				
//		File imagePath = new File(activity.getFilesDir(), "images");
//		File newFile = new File(imagePath, FILENAME);
//		Uri contentUri = FileProvider.getUriForFile(activity, "net.archenemy.archenemyapp.fileprovider", newFile);
//		
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_SEND);
//		intent.putExtra(Intent.EXTRA_STREAM, contentUri);
//		intent.setType("image/png");
//		intent.setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION);
//		activity.startActivity(Intent.createChooser(intent, "Share image to.."));;
	}
}
