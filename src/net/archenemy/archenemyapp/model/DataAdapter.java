package net.archenemy.archenemyapp.model;

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
	
}
