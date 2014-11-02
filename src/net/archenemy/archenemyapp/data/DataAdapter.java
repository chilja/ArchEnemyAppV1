package net.archenemy.archenemyapp.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import Tour.Show;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DataAdapter {
	
	private Activity mActivity;
	private static final String TAG = "DataAdapter";
	
	private static TreeMap<Integer,SocialMediaUser> mSocialMediaUsers = createSocialMediaUsers();
	
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
	
	public static TreeMap<Integer,SocialMediaUser> getSocialMediaUsers(){
		return mSocialMediaUsers;
	}
	
	public static TreeMap<Integer,SocialMediaUser> createSocialMediaUsers(){
		TreeMap<Integer,SocialMediaUser> members = new TreeMap<Integer,SocialMediaUser>();
		// String name, String prefKey, int userId,
		// String twitterUserId, 
		// String facebookUser, String facebookUserId)
		members.put(1,
				new SocialMediaUser("Arch Enemy", null, 1, 
						 "19564489",
						"Arch Enemy", "142695605765331") {
					public boolean isEnabled(Activity activity) {
						return true;
					}
				});
		
		members.put(2,
				new SocialMediaUser("Alyssa White-Gluz", Constants.PREF_KEY_ALYSSA, 2, 
						"383472626",
						"Alyssa White-Gluz's - Official Page", "49373264983"));
		
		members.put(3,
				new SocialMediaUser("Michael Amott", Constants.PREF_KEY_MICHAEL, 3,
						 "88349752",
						"Official Michael Amott","116270908441437"));
		
		return members;
	}
	
	public static SocialMediaUser getSocialMediaUser(int userId) {		
		return mSocialMediaUsers.get(userId);
	}
	
	public ArrayList<SocialMediaUser> getEnabledSocialMediaUsers(){
		TreeMap<Integer,SocialMediaUser> members = getSocialMediaUsers();
		ArrayList<SocialMediaUser> enabledMembers = new ArrayList<SocialMediaUser>();
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
	
	public static void loadBitmap(
			String bitmapUrl, ImageView imageView, BitmapCallback callback, int reqWidth, int reqHeight) {
		BitmapFromUrlTask task = new DataAdapter.BitmapFromUrlTask(imageView, callback, reqWidth, reqHeight);
		task.execute(bitmapUrl);		
	}

	public void loadBitmap(int resId, ImageView imageView, int reqWidth, int reqHeight) {		
	    BitmapFromResourcesTask task = new BitmapFromResourcesTask(imageView, reqWidth, reqHeight);
	    task.execute(resId);
	}

	private static Bitmap decodeSampledBitmapFromUrl(URL url, int reqWidth, int reqHeight) 
			throws IOException {
		URLConnection uc = url.openConnection();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		
		// First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(is, null, options);
	    is.close();
	
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    uc = url.openConnection();
	    is = new BufferedInputStream(uc.getInputStream());
	    return BitmapFactory.decodeStream(uc.getInputStream(), null, options);		
	}
	
	private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {
	
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);
	
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
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


	/**
	 * AsyncTask to retrieve images
	 */
	private static class BitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> mImageViewReference;
		final int mReqWidth; 
		final int mReqHeight;
		BitmapCallback mCallback;
		
		private BitmapFromUrlTask(ImageView imageView, BitmapCallback callback, int reqWidth, int reqHeight) {
			mImageViewReference = new WeakReference<ImageView>(imageView);
			mReqWidth = reqWidth;
			mReqHeight = reqHeight;
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			if (urls != null && urls.length > 0) {
				try {
					URL url = new URL(urls[0].trim());
					return decodeSampledBitmapFromUrl(url, mReqWidth, mReqHeight);
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
	
	private class BitmapFromResourcesTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> mImageViewReference;
	    private int mResId = 0;
	    final int mReqWidth; 
		final int mReqHeight;

	    public BitmapFromResourcesTask(ImageView imageView, int reqWidth, int reqHeight) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        mImageViewReference = new WeakReference<ImageView>(imageView);
	        mReqWidth = reqWidth;
			mReqHeight = reqHeight;
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        mResId = params[0];
	        return decodeSampledBitmapFromResource(mActivity.getResources(), mResId, mReqWidth, mReqHeight);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (mImageViewReference != null && bitmap != null) {
	            final ImageView imageView = mImageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}
	
	public static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<BitmapFromUrlTask> bitmapTaskReference;
    
	    public AsyncDrawable(Resources res, Bitmap bitmap,
	            BitmapFromUrlTask bitmapWorkerTask) {
	        super(res,bitmap);
	        bitmapTaskReference =
	            new WeakReference<BitmapFromUrlTask>(bitmapWorkerTask);
	    }

	    public BitmapFromUrlTask getBitmapWorkerTask() {
	        return bitmapTaskReference.get();
	    }
	}
	
	public interface BitmapCallback {
		void onPostExecute(Bitmap bitmap);
	}
}
