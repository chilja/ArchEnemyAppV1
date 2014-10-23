package net.archenemy.archenemyapp.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.R.string;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.ui.FeedListElement;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.BaseAdapter;

public class Show 
	implements Comparable<Show>{
	
	private String mVenue;
	private String mShowDate;
	private Date mDate;
	private String mTicketUri;
	private String mLocation;
	private String mImageUri;
	private String mDescription;
	private String mCountry;
	
	private static final String TAG = "Show";
	
	public Show(String showDate, String venue, String location, String country, String ticketUri, String imageUri) {
		setShowDate(showDate);
		setVenue(venue);
		setLocation(location);
		setCountry(country);
		setTicketUri(ticketUri);
		setImageUri(imageUri);		
		mDate = DataAdapter.formatTourDate(showDate);
		if (mDate != null) {
			setShowDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(mDate));
		}
	}
	
	public boolean isUpcoming() {
		if (mDate != null) {
			Date currentDate = new Date();
			if (mDate.compareTo(currentDate)<0) return false;
		}
		return true;
	}
	
	public String getShareText(Activity activity) {
		return activity.getResources().getString(R.string.fb_share_name) + " "+ getDescription()
		+ ". " + activity.getResources().getString(R.string.fb_share_caption) + " " + getTicketUri();
	}
	
	public void setDescription(Activity activity) {
		mDescription = activity.getResources().getString(R.string.fb_share_description1) + " "
						+ getShowDate() + " " 
						+ activity.getResources().getString(R.string.fb_share_description2) + " "
						+ getVenue() + " "
						+ activity.getResources().getString(R.string.fb_share_description3) + " "
						+ getLocation()+ " "
						+ activity.getResources().getString(R.string.fb_share_description4) + " "
						+ getCountry();
		
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	public String getImageUri() {
		return mImageUri;
	}

	private void setImageUri(String mImageUri) {
		this.mImageUri = mImageUri;
	}

	public String getCountry() {
		return mCountry;
	}

	private void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}
	public String getVenue() {
		return mVenue;
	}

	private void setVenue(String venue) {
		this.mVenue = venue;
	}

	public String getShowDate() {
		return mShowDate;
	}

	private void setShowDate(String showDate) {
		mShowDate = showDate;
	}

	public String getTicketUri() {
		return mTicketUri;
	}

	private void setTicketUri(String ticketUri) {
		this.mTicketUri = ticketUri;
	}

	public String getLocation() {
		return mLocation;
	}

	private void setLocation(String location) {
		this.mLocation = location;
	}

	@Override
	public int compareTo(Show element) {
		if (mDate != null && element.mDate != null) {
			if ((mDate.getTime() - element.mDate.getTime())<0) return -1;
			if ((mDate.getTime() - element.mDate.getTime())>0) return 1;
		}
		return 0;
	}	
	
}