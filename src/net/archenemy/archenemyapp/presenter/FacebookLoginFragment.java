package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class FacebookLoginFragment extends AccountFragment {
	
	public static final String TAG = "FacebookLoginFragment";
		
	protected static final int TITLE = R.string.title_facebook;
	
	protected FacebookAdapter mFacebookAdapter;
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mFacebookAdapter = FacebookAdapter.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.facebook_login_fragment, container, false);
	    
	    final ImageView providerIcon = (ImageView) view.findViewById(R.id.providerIcon);
	    final Animation fade = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
	    providerIcon.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if (Utility.isConnectedToNetwork(getActivity(), true))  {
		    		providerIcon.startAnimation(fade);; 
		    	}
		    }
		});

		// Find the facebook login button
		mLoginButton = (Button) view.findViewById(R.id.loginButton);
		if (!Utility.isConnectedToNetwork(getActivity(), false)) {
			mLoginButton.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View view) {
			    	Utility.isConnectedToNetwork(getActivity(), true); 
			    }
			});
		}
			
		return view;
	}
	
	public interface OnFacebookLoginListener {
		void onFacebookLogin();
	}
}
