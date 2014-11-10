package net.archenemy.archenemyapp.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class BitmapUtility {
	
	private static final String TAG = "BitmapUtility";
	
	private static ArrayList<AsyncTask>  mTasks = new ArrayList<AsyncTask>();
	
	private static LruCache<String, Bitmap> mMemoryCache ;
	
	//static initialization block
	static {
	// Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	
	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
    
	}

	private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	private static Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	public static void onDestroy() {
		if (!mTasks.isEmpty()) {
			for (AsyncTask task:mTasks) {
				task.cancel(true);
			}
			mTasks.clear();
		}
		Log.i(TAG, "all async tasks cancelled");
	}
	
	
	public static void loadBitmap(
			String bitmapUrl, ImageView imageView, int reqWidth, int reqHeight) {
		if (imageView != null && bitmapUrl != null) {       
			//check cache
			Bitmap bitmap = getBitmapFromMemCache(bitmapUrl.toString());
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				return;
			}
			BitmapFromUrlTask task = new BitmapFromUrlTask(imageView, reqWidth, reqHeight);
			task.execute(bitmapUrl);
		}
	}

	public static void loadBitmap(Activity activity, int resId, ImageView imageView, int reqWidth, int reqHeight) {		
	    BitmapFromResourcesTask task = new BitmapFromResourcesTask(activity, imageView, reqWidth, reqHeight);
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
		URL mUrl;
		
		private BitmapFromUrlTask(ImageView imageView, int reqWidth, int reqHeight) {
			mImageViewReference = new WeakReference<ImageView>(imageView);
			mReqWidth = reqWidth;
			mReqHeight = reqHeight;
			mTasks.add(this);
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			if (urls != null && urls.length > 0) {				
								
				try {
					mUrl = new URL(urls[0].trim());
					return decodeSampledBitmapFromUrl(mUrl, mReqWidth, mReqHeight);
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
			addBitmapToMemoryCache(mUrl.toString(), bitmap);
			mTasks.remove(this);
		}
	}
	
	private static class BitmapFromResourcesTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> mImageViewReference;
	    private int mResId = 0;
	    final int mReqWidth; 
		final int mReqHeight;
		final Activity mActivity;

	    public BitmapFromResourcesTask(Activity activity, ImageView imageView, int reqWidth, int reqHeight) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        mImageViewReference = new WeakReference<ImageView>(imageView);
	        mReqWidth = reqWidth;
			mReqHeight = reqHeight;
			mActivity = activity;
			mTasks.add(this);
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
	        mTasks.remove(this);
	    }
	}
}
