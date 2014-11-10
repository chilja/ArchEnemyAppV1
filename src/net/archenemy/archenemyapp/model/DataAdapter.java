package net.archenemy.archenemyapp.model;

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
import android.util.Log;

public class DataAdapter {
	
//	private Activity mActivity;
	private static final String TAG = "DataAdapter";
	
	private static TreeMap<Integer,SocialMediaUser> mSocialMediaUsers = createSocialMediaUsers();
	
	private DataAdapter() {
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
		TreeMap<Integer,SocialMediaUser> users = new TreeMap<Integer,SocialMediaUser>();
		// String name, String prefKey, int userId,
		// String twitterUserId, 
		// String facebookUser, String facebookUserId)
		users.put(1,
				new SocialMediaUser("Arch Enemy", null, 1, 
						 "19564489",
						"Arch Enemy", "142695605765331") {
					public boolean isEnabled(Activity activity) {
						return true;
					}
				});
		
		users.put(2,
				new SocialMediaUser("Alyssa White-Gluz", Constants.PREF_KEY_ALYSSA, 2, 
						"383472626",
						"Alyssa White-Gluz's - Official Page", "49373264983"));
		
		users.put(3,
				new SocialMediaUser("Michael Amott", Constants.PREF_KEY_MICHAEL, 3,
						 "88349752",
						"Official Michael Amott","116270908441437"));
		
		return users;
	}
	
	public static SocialMediaUser getSocialMediaUser(int userId) {		
		return mSocialMediaUsers.get(userId);
	}
	
	public static ArrayList<SocialMediaUser> getEnabledSocialMediaUsers(Activity activity){
		TreeMap<Integer,SocialMediaUser> members = getSocialMediaUsers();
		ArrayList<SocialMediaUser> enabledMembers = new ArrayList<SocialMediaUser>();
		Set<Integer> keys = members.keySet();
		for (Integer key: keys) {
			if (members.get(key).isEnabled(activity)) enabledMembers.add(members.get(key));
		}
		return enabledMembers;
	}
	
	public static ArrayList<Show> getShowList(Activity activity) {

		ArrayList<Show> listElements;

  		listElements = new ArrayList<Show>();
		
  		String[] tourData = activity.getResources()
                .getStringArray(R.array.tourData);
  		String imageUri = activity.getResources()
                .getString(R.string.tour_image_url);
  		for (String showData: tourData){
  			String[] data = showData.split(";");
  			Show show = new Show(data[0],data[1], data[2], data[3], data[4], imageUri);
  			if (show.isUpcoming()) {
  			show.setDescription(activity);
  			listElements.add(show);
  			}
  		}
  		Collections.sort(listElements);
  		
	  	return listElements;
	}
	
	public static String[] getShowStringArray(Activity activity) {		
		ArrayList<Show> showList = getShowList(activity);
		String[] shows = new String[showList.size()];
		int i = 0;
		for(Show show:showList){
			shows[i] = show.getDescription();
			i++;
		}		
		return shows;
	}	
}
