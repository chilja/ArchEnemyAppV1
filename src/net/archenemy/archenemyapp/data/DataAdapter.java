package net.archenemy.archenemyapp.data;

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
import net.archenemy.archenemyapp.logic.BandMember;
import net.archenemy.archenemyapp.logic.Show;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DataAdapter {
	
	private Activity mActivity;
	private static final String TAG = "DataAdapter";
	private static final String PREF_KEY_MICHAEL = "pref_key_michael_amott";
	private static final String PREF_KEY_ALYSSA = "pref_key_alyssa_white_gluz";
	
	private static TreeMap<Integer,BandMember> mBandMembers = createBandMembers();
	
	public DataAdapter(Activity activity) {
		mActivity = activity;
	}
	
	public static void getBitmap(String bitmapUrl, BitmapCallback callback) {
		BitmapTask task = new DataAdapter.BitmapTask(callback);
		task.execute(bitmapUrl);		
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
				new BandMember("Alyssa White-Gluz", PREF_KEY_ALYSSA,2, 
						"AWhiteGluz", "383472626",
						"Alyssa White-Gluz's - Official Page", "49373264983"));
		
		members.put(3,
				new BandMember("Michael Amott", PREF_KEY_MICHAEL, 3,
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
	
	/**
	 * AsyncTask to retrieve images
	 */
	private static class BitmapTask extends AsyncTask<String, Void, Bitmap> {

		BitmapCallback mCallback;

		private BitmapTask(BitmapCallback callback) {
			mCallback = callback;
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			if (urls != null && urls.length > 0) {
				try {
					URL url = new URL(urls[0]);
					URLConnection uc = url.openConnection();
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
			mCallback.onPostExecute(bitmap);
		}
	}
	
	public interface BitmapCallback {
		void onPostExecute(Bitmap bitmap);
	}
	
}