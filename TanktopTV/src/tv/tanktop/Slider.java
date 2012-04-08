package tv.tanktop;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Touch listener that allows you to slide a view off the screen and causes the
 * view to fade as it slides.  The intention is to use it for "slide to delete"
 */
public class Slider implements
                   OnTouchListener, AnimationListener
{
  private static final String TAG = "Slider";

  private static final float MIN_VELOCITY = 2.0f; // pixels/ms
  private static final float TARGET_WIDTH = 0.66f;
  // Fraction of target width after which we block other touch gestures
  private static final float THRESHOLD_WIDTH = 0.1f;

  private boolean mTracking;
  private float mStartX;
  private float mLastX;
  private VelocityTracker mVelocityTracker;
  private float mMaxDist;
  private float mTargetDist;
  private final OnSlideListener mOnSlideListener;
  private final View mView;

  public interface OnSlideListener
  {
    public void onSlideComplete(View v);
  }

  public Slider(View v, OnSlideListener listener)
  {
    mOnSlideListener = listener;
    mView = v;
  }

  public boolean onTouch(View v, MotionEvent event)
  {
    int action = event.getActionMasked();

    //Log.d(TAG, "onTouch " + event);

    if (mTargetDist == 0)
    {
      Rect rect = new Rect();
      v.getWindowVisibleDisplayFrame(rect);
      int screenWidth = rect.width();
      mTargetDist = screenWidth * TARGET_WIDTH;
      Log.d(TAG, "screen width " + screenWidth);
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

        if (dist > mTargetDist * THRESHOLD_WIDTH)
        {
          mView.getParent().requestDisallowInterceptTouchEvent(true);
        }

        //Log.d(TAG, "dist " + dist + " target " + (mScreenWidth *0.5));
        // Move the view to follow the user's finger
        Animation anim = buildAnimation(mLastX, event.getX(), 10);
        v.startAnimation(anim);
        mLastX = event.getX();
      }
      break;

    case MotionEvent.ACTION_UP:

      if (mTracking)
      {
        float dist = Math.abs(event.getX() - mStartX);
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

          if ((Math.abs(mVelocityTracker.getXVelocity()) > 2) || (dist > mTargetDist))
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

    case MotionEvent.ACTION_CANCEL: // Can't see why not to process on cancel in this case
      // Get this if the user scrolls vertically
      // May want to stop this happening!
      //v.clearAnimation();
      Animation anim = v.getAnimation();
      if (anim != null)
      {
        anim.cancel();

        anim = buildAnimation(event.getX(), mStartX, 0);
        v.startAnimation(anim);
      }
      if (mTracking)
      {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
      }
      mTracking = false;
      break;
    }

    return mTracking;
  }

  /**
   * Calculate the alpha value for a given x coord. The view should fade out when
   * moved about 3/4 of the way across
   * @param x
   * @return Alpha value 0 invisible, 1 fully visible
   */
  private float xToAlpha(float x)
  {
    float dist = (Math.abs(x - mStartX));
    if (dist < mTargetDist)
    {
      return 1  - (dist / mTargetDist);
    }
    return 0;
  }

  /**
   * Animate the view off the screen at the current velocity
   * @param v
   * @param event
   */
  private void animateOff(View v, MotionEvent event)
  {
    float velocity = mVelocityTracker.getXVelocity();
    //Log.d(TAG, "velocity " + velocity);
    boolean toTheRight = velocity > 0;
    velocity = Math.min(Math.abs(velocity), MIN_VELOCITY);
    float distSoFar = event.getX() - mStartX;
    int remaining = (int) (mTargetDist - Math.abs(distSoFar));
    if (remaining < 0)
    {
      remaining = 0;
    }
    int duration = (int) (remaining / velocity);

    //Log.d(TAG, "distSoFar " + distSoFar);
    //Log.d(TAG, "remaining " + remaining);
    //Log.d(TAG, "duration " + duration);

    Animation anim = buildAnimation(event.getX(), toTheRight ? mTargetDist : -mTargetDist, duration);

    anim.setAnimationListener(this);
    v.startAnimation(anim);
  }

  /**
   * Animate the view back to its original position
   * @param v
   * @param event
   */
  private void animateBack(View v, MotionEvent event)
  {
    Animation anim = buildAnimation(event.getX(), mStartX, 500);
    anim.setInterpolator(new BounceInterpolator());
    v.startAnimation(anim);
  }

  /**
   * Build an animation that combines sliding the view and fading it
   * out.
   * @param startX
   * @param endX
   * @param duration duration of animation in ms.
   * @return An animation set encapsulating the animation
   */
  private Animation buildAnimation(float startX, float endX, long duration)
  {
    AnimationSet animSet = new AnimationSet(true);

    TranslateAnimation transAnim = new TranslateAnimation(startX - mStartX, endX - mStartX, 0, 0);
    AlphaAnimation alphaAnim = new AlphaAnimation(xToAlpha(startX), xToAlpha(endX));

    animSet.addAnimation(transAnim);
    animSet.addAnimation(alphaAnim);

    transAnim.setDuration(duration);
    alphaAnim.setDuration(duration);

    animSet.setFillAfter(true);

    return animSet;
  }

  public void onAnimationEnd(Animation animation)
  {
    mView.playSoundEffect(SoundEffectConstants.CLICK);
    if (mOnSlideListener != null)
    {
      mOnSlideListener.onSlideComplete(mView);
    }
  }

  public void onAnimationRepeat(Animation animation)
  {
  }

  public void onAnimationStart(Animation animation)
  {
  }
}
