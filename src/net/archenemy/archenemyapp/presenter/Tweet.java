package net.archenemy.archenemyapp.presenter;

import java.util.Date;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapSharable;
import net.archenemy.archenemyapp.model.BitmapShareListener;
import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.FacebookSharable;
import net.archenemy.archenemyapp.model.FacebookShareListener;
import net.archenemy.archenemyapp.model.TextSharable;
import net.archenemy.archenemyapp.model.TextShareListener;
import net.archenemy.archenemyapp.model.Utility;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Tweet 
	implements 
		FeedElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseAdapter mAdapter;
	private String mMessage;
	private Date mDate;
	private String mLink;
	private String mImageUrl ;
	private transient ImageView mImageView;
	private Bitmap mBitmap;
	
	// Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

	
	public Tweet(String name, String message, Date createdAt, String link) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
	}
	
	public Tweet(String name, String message, Date createdAt, String link, String imageUrl) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
		mImageUrl = imageUrl;
	}
	
	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Tweet) {
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())<0) return 1;
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())>0) return -1;
		}
		return 0;
	}
	
	@Override
	public View getView(Activity activity){
        
        LayoutInflater inflater =
                (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.tweet, null);
        

    	TextView messageView = (TextView) view.findViewById(R.id.messageView);
    	TextView dateView = (TextView) view.findViewById(R.id.dateView);
    	
    	mImageView = (ImageView) view.findViewById(R.id.imageView);
    	
    	//Bitmap already loaded?
    	if (mBitmap != null) {
    		mImageView.setImageBitmap(mBitmap);	
    	// URL provided? -> load bitmap
    	} else if (mImageUrl != null){
    		BitmapUtility.loadBitmap(mImageUrl, mImageView, 600, 600);
    		mImageView.setVisibility(View.VISIBLE);
    	// no picture -> hide image view	
    	} else {
    		mImageView.setVisibility(View.GONE);
    	}

    	if(messageView != null) {
    		messageView.setText(mMessage);
    	}

    	if(dateView != null) {
    		dateView.setText(mDate.toString());
    	}
    	
    	// Hook up clicks on the thumbnail views.       
    	mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View image) {
                zoomImageFromThumb(mImageView, view);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = activity.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    	
        return view;
	}
	
	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public String getLink() {
		return mLink;
	}
	
	private void zoomImageFromThumb(final View thumbView, View layout) {
	    // If there's an animation in progress, cancel it
	    // immediately and proceed with this one.
	    if (mCurrentAnimator != null) {
	        mCurrentAnimator.cancel();
	    }

	    // Load the high-resolution "zoomed-in" image.
	    final ImageView expandedImageView = (ImageView) layout.findViewById(
	            R.id.expandedImage);
	    BitmapUtility.loadBitmap(mImageUrl, expandedImageView, 400, 400);

	    // Calculate the starting and ending bounds for the zoomed-in image.
	    // This step involves lots of math. Yay, math.
	    final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();

	    // The start bounds are the global visible rectangle of the thumbnail,
	    // and the final bounds are the global visible rectangle of the container
	    // view. Also set the container view's offset as the origin for the
	    // bounds, since that's the origin for the positioning animation
	    // properties (X, Y).
	    thumbView.getGlobalVisibleRect(startBounds);
	    layout.findViewById(R.id.container)
	            .getGlobalVisibleRect(finalBounds, globalOffset);
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);

	    // Adjust the start bounds to be the same aspect ratio as the final
	    // bounds using the "center crop" technique. This prevents undesirable
	    // stretching during the animation. Also calculate the start scaling
	    // factor (the end scaling factor is always 1.0).
	    float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        // Extend start bounds horizontally
	        startScale = (float) startBounds.height() / finalBounds.height();
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        // Extend start bounds vertically
	        startScale = (float) startBounds.width() / finalBounds.width();
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }

	    // Hide the thumbnail and show the zoomed-in view. When the animation
	    // begins, it will position the zoomed-in view in the place of the
	    // thumbnail.
	    thumbView.setAlpha(0f);
	    expandedImageView.setVisibility(View.VISIBLE);

	    // Set the pivot point for SCALE_X and SCALE_Y transformations
	    // to the top-left corner of the zoomed-in view (the default
	    // is the center of the view).
	    expandedImageView.setPivotX(0f);
	    expandedImageView.setPivotY(0f);

	    // Construct and run the parallel animation of the four translation and
	    // scale properties (X, Y, SCALE_X, and SCALE_Y).
	    AnimatorSet set = new AnimatorSet();
	    set
	            .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
	                    startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
	                    startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
	            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
	                    View.SCALE_Y, startScale, 1f));
	    set.setDuration(mShortAnimationDuration);
	    set.setInterpolator(new DecelerateInterpolator());
	    set.addListener(new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	            mCurrentAnimator = null;
	        }

	        @Override
	        public void onAnimationCancel(Animator animation) {
	            mCurrentAnimator = null;
	        }
	    });
	    set.start();
	    mCurrentAnimator = set;

	    // Upon clicking the zoomed-in image, it should zoom back down
	    // to the original bounds and show the thumbnail instead of
	    // the expanded image.
	    final float startScaleFinal = startScale;
	    expandedImageView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
	            if (mCurrentAnimator != null) {
	                mCurrentAnimator.cancel();
	            }

	            // Animate the four positioning/sizing properties in parallel,
	            // back to their original values.
	            AnimatorSet set = new AnimatorSet();
	            set.play(ObjectAnimator
	                        .ofFloat(expandedImageView, View.X, startBounds.left))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.Y,startBounds.top))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_X, startScaleFinal))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_Y, startScaleFinal));
	            set.setDuration(mShortAnimationDuration);
	            set.setInterpolator(new DecelerateInterpolator());
	            set.addListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }

	                @Override
	                public void onAnimationCancel(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }
	            });
	            set.start();
	            mCurrentAnimator = set;
	        }
	    });
	}

}
