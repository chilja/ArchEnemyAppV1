package net.archenemy.archenemyapp.data;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import net.archenemy.archenemyapp.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

public class DataAdapter {
	
	private Activity mActivity;
	private static final String TAG = "DataAdapter";
	
	private static TreeMap<Integer,BandMember> mBandMembers = createBandMembers();
	
	public DataAdapter(Activity activity) {
		mActivity = activity;
	}
	
	public static Date formatTourDate(String tourDate){
		SimpleDateFormat ft = 
			new SimpleDateFormat ("dd.MM.yyyy", Locale.US);
		Date date = null; 
		try { 
		    date = ft.parse(tourDate);  
		} catch (ParseException e) { 
		    Log.e(TAG,"Unparseable using " + ft); 
		}
		return date;		
	}
	
	public static TreeMap<Integer,BandMember> getBandMembers(){
		return mBandMembers;
	}
	
	public static TreeMap<Integer,BandMember> createBandMembers(){
		TreeMap<Integer,BandMember> members = new TreeMap<Integer,BandMember>();
		// String name, String prefKey, int userId,
		// String twitterUser, String twitterUserId, 
		// String facebookUser, String facebookUserId)
		members.put(1,
				new BandMember("Arch Enemy", null, 1, 
						"archenemymetal", "19564489",
						"Arch Enemy", "142695605765331") {
					public boolean isEnabled(Activity activity) {
						return true;
					}
				});
		
		members.put(2,
				new BandMember("Alyssa White-Gluz", Constants.PREF_KEY_ALYSSA,2, 
						"AWhiteGluz", "383472626",
						"Alyssa White-Gluz's - Official Page", "49373264983"));
		
		members.put(3,
				new BandMember("Michael Amott", Constants.PREF_KEY_MICHAEL, 3,
						"Michael_Amott", "88349752",
						"Official Michael Amott","116270908441437"));
		
		return members;
	}
	
	public static BandMember getBandMember(int userId) {		
		return mBandMembers.get(userId);
	}
	
	public ArrayList<BandMember> getEnabledBandMembers(){
		TreeMap<Integer,BandMember> members = getBandMembers();
		ArrayList<BandMember> enabledMembers = new ArrayList<BandMember>();
		Set<Integer> keys = members.keySet();
		for (Integer key: keys) {
			if (members.get(key).isEnabled(mActivity)) enabledMembers.add(members.get(key));
		}
		return enabledMembers;
	}
	
	public ArrayList<Show> getShowList() {

		ArrayList<Show> listElements;

  		listElements = new ArrayList<Show>();
		
  		String[] tourData = mActivity.getResources()
                .getStringArray(R.array.tourData);
  		String imageUri = mActivity.getResources()
                .getString(R.string.tour_image_url);
  		for (String showData: tourData){
  			String[] data = showData.split(";");
  			Show show = new Show(data[0],data[1], data[2], data[3], data[4], imageUri);
  			if (show.isUpcoming()) {
  			show.setDescription(mActivity);
  			listElements.add(show);
  			}
  		}
  		Collections.sort(listElements);
  		
	  	return listElements;
	}
	
	public String[] getShowStringArray() {		
		ArrayList<Show> showList = getShowList();
		String[] shows = new String[showList.size()];
		int i = 0;
		for(Show show:showList){
			shows[i] = show.getDescription();
			i++;
		}		
		return shows;
	}
	
	public static void loadBitmap(String bitmapUrl, ImageView imageView) {
		BitmapTask task = new DataAdapter.BitmapTask(imageView);
		task.execute(bitmapUrl);		
	}
	
	public static void loadBitmap(String bitmapUrl, ImageView imageView, BitmapCallback callback) {
		BitmapTask task = new DataAdapter.BitmapTask(imageView, callback);
		task.execute(bitmapUrl);		
	}


	/**
	 * AsyncTask to retrieve images
	 */
	private static class BitmapTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> mImageViewReference;
		final int mReqWidth; 
		final int mReqHeight;
		BitmapCallback mCallback;

		private BitmapTask(ImageView imageView) {
			mImageViewReference = new WeakReference<ImageView>(imageView);
			mReqWidth = 100;
			mReqHeight = 100;
		}
		
		private BitmapTask(ImageView imageView, BitmapCallback callback) {
			mImageViewReference = new WeakReference<ImageView>(imageView);
			mReqWidth = 100;
			mReqHeight = 100;
			mCallback = callback;
		}
		
		private BitmapTask(ImageView imageView, BitmapCallback callback, int reqWidth, int reqHeight) {
			mImageViewReference = new WeakReference<ImageView>(imageView);
			mReqWidth = reqWidth;
			mReqHeight = reqHeight;
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			if (urls != null && urls.length > 0) {
				try {
					URL url = new URL(urls[0].trim());
					URLConnection uc = url.openConnection();
//					return loadBitmap(uc, mReqWidth, mReqHeight);
					uc.getInputStream();
					return BitmapFactory.decodeStream(uc.getInputStream());
				} catch(Exception ex) {
					return null;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (mImageViewReference != null && bitmap != null) {
	            final ImageView imageView = mImageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
			if (mCallback != null)
				mCallback.onPostExecute(bitmap);
		}
	}
	
	
	
	private static Bitmap loadBitmap( URLConnection uc, int reqWidth, int reqHeight) throws IOException {
		Rect rect = new Rect();
		
		// First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(uc.getInputStream(), rect, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeStream(uc.getInputStream(), rect, options);		
	}
	
	
	private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
	}
	
	public interface BitmapCallback {
		void onPostExecute(Bitmap bitmap);
	}
}
