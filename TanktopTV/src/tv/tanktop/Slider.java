package tv.tanktop;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

public class Slider implements
                   OnTouchListener
{
  private static final String TAG = "Slider";

  private static final float MIN_VELOCITY = 2.0f; // pixels/ms

  private boolean mTracking;
  private float mStartX;
  private float mLastX;
  private VelocityTracker mVelocityTracker;
  private float mMaxDist;
  private int mScreenWidth;

  public boolean onTouch(View v, MotionEvent event)
  {
    int action = event.getActionMasked();

    Log.d(TAG, "onTouch " + event);

    if (mScreenWidth == 0)
    {
      Rect rect = new Rect();
      v.getWindowVisibleDisplayFrame(rect);
      mScreenWidth = rect.width();
      Log.d(TAG, "screen width " + mScreenWidth);
    }

    switch (action)
    {
    case MotionEvent.ACTION_DOWN:
      mTracking = true;
      mStartX = event.getX();
      mLastX = event.getX();
      mVelocityTracker = VelocityTracker.obtain();
      mMaxDist = 0;
      break;

    case MotionEvent.ACTION_MOVE:
      if (mTracking)
      {
        mVelocityTracker.addMovement(event);

        float dist = Math.abs(event.getX() - mStartX);

        mMaxDist = Math.max(mMaxDist, dist);

        Log.d(TAG, "dist " + dist + " target " + (mScreenWidth *0.5));
        if (dist > (mScreenWidth * 0.5))
        {
          mVelocityTracker.computeCurrentVelocity(1); // Pixels per milliseconds
          animateOff(v, event);
          mTracking = false;
        }
        else
        {
          TranslateAnimation anim = new TranslateAnimation(mLastX - mStartX, event.getX() - mStartX, 0, 0);
          anim.setDuration(10);
          anim.setFillAfter(true);
          v.startAnimation(anim);
          mLastX = event.getX();
        }
      }
      break;

    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_CANCEL: // Can't see why not to process on cancel in this case
      if (mTracking)
      {
        Log.d(TAG, "MaxDist " + mMaxDist);
        if (mMaxDist < 15)
        {
          // Really just a tap.  Could also look at time touched
          if (action != MotionEvent.ACTION_CANCEL)
          {
            Log.d(TAG, "clicking!");
            // Note this works because of a special onClick handler added in the
            // ListAdapter, which translates to onItemClick
            v.performClick();
          }
        }
        else
        {
          mVelocityTracker.computeCurrentVelocity(1); // Pixels per milliseconds
          Log.d(TAG, "velocity " + mVelocityTracker.getXVelocity());

          if (Math.abs(mVelocityTracker.getXVelocity()) > 2)
          {
            // Animate off in velocity direction at velocity rate
            animateOff(v, event);
          }
          else
          {
            // Animate back
            animateBack(v, event);
          }
        }

        mVelocityTracker.recycle();
        mVelocityTracker = null;
      }
      mTracking = false;
      break;
    }

    return mTracking;
  }

  private void animateOff(View v, MotionEvent event)
  {
    float velocity = mVelocityTracker.getXVelocity();
    //Log.d(TAG, "velocity " + velocity);
    boolean toTheRight = velocity > 0;
    velocity = Math.min(Math.abs(velocity), MIN_VELOCITY);
    float distSoFar = event.getX() - mStartX;
    int remaining = (int) (mScreenWidth - Math.abs(distSoFar));
    int duration = (int) (remaining / velocity);

    //Log.d(TAG, "distSoFar " + distSoFar);
    //Log.d(TAG, "remaining " + remaining);
    //Log.d(TAG, "duration " + duration);

    TranslateAnimation anim = new TranslateAnimation(event.getX() - mStartX,
        toTheRight ? mScreenWidth : -mScreenWidth, 0, 0);
    anim.setDuration(duration);
    anim.setFillAfter(true);
    v.startAnimation(anim);
  }

  private void animateBack(View v, MotionEvent event)
  {
    TranslateAnimation anim = new TranslateAnimation(event.getX() - mStartX, 0, 0, 0);
    anim.setDuration(500);
    BounceInterpolator interpolator = new BounceInterpolator();
    anim.setInterpolator(interpolator);
    v.startAnimation(anim);
  }
}
