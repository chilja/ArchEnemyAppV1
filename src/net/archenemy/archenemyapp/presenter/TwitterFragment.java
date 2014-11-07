package net.archenemy.archenemyapp.presenter;

import java.util.ArrayList;

import twitter4j.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.TwitterAdapter.UserCallback;

public class TwitterFragment extends BaseFragment{
	
	public static final int TITLE = R.string.title_twitter;
    public static final String TAG = "TwitterFragment";
    
    //view pager
	protected ViewPager mViewPager;
	protected TwitterPagerAdapter mPagerAdapter;

	//one page for each band member
	private static ArrayList<SocialMediaUser> mSocialMediaUsers;
	
	private TwitterAdapter mTwitterAdapter;
	
	private View mView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    mView = inflater.inflate(R.layout.pager_fragment, container, false);

        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        
        mSocialMediaUsers = mDataAdapter.getEnabledSocialMediaUsers();
        
        mTwitterAdapter = new TwitterAdapter(mActivity);
        
        refresh();
	    	
	    return mView;
	}
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	public void refresh(){ 	
		if (mFragmentManager != null && mViewPager != null) {
			synchronized (mFragmentManager) {

		 	mPagerAdapter = new TwitterPagerAdapter(mFragmentManager);	        
				if (mPagerAdapter != null) {
					mViewPager.setOffscreenPageLimit(6);
					mViewPager.setAdapter(mPagerAdapter);
				}
			}
		}
	}
	

	class TwitterPagerAdapter extends FragmentPagerAdapter {

	    public TwitterPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	
	    @Override
	    public Fragment getItem(int i) {
	    	//create page with feeds of the corresponding band member
	    	return	getNewFragment(i);
	    }
	
	    @Override
	    public int getCount() {
	        return mSocialMediaUsers.size();
	    }
	
	    @Override
	    public CharSequence getPageTitle(int position) {
	        return mSocialMediaUsers.get(position).getName();
	    }  
	    
	    protected TwitterPageFragment getNewFragment(int i) {
			TwitterPageFragment fragment = new TwitterPageFragment();
		    Bundle args = new Bundle();
	        args.putInt(TwitterPageFragment.USER_ID, i+1);
	        fragment.setArguments(args);
	        return fragment;	
		}
	}
}
