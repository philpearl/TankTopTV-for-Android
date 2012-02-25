package tv.tanktop;

import java.text.DateFormat;
import java.util.Date;

import tv.tanktop.Slider.OnSlideListener;
import tv.tanktop.utils.NetImageLoader;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public abstract class TimeCursorAdapter extends CursorAdapter implements OnClickListener, OnSlideListener
{
  protected final LayoutInflater mLayoutInflater;
  protected final NetImageLoader mImageLoader;
  protected final DateFormat mTimeFormat;
  protected final DateFormat mDateFormat;
  private final Date mDate = new Date();
  protected final ItemEventListener mItemEventListener;

  public TimeCursorAdapter(Context context, ItemEventListener eventListener, NetImageLoader imageLoader)
  {
    super(context, null, 0);
    mLayoutInflater = LayoutInflater.from(context);
    mImageLoader = imageLoader;
    mItemEventListener = eventListener;

    mTimeFormat = android.text.format.DateFormat.getTimeFormat(mContext);
    mDateFormat = android.text.format.DateFormat.getMediumDateFormat(mContext);

  }

  public class Tag
  {
    // This position may not be reliable after the list has been updated
    int mPosition;
    long mId;

    TextView mName;
    TextView mSynopsis;
    TextView mExpires;
    ImageView mImage;
    Slider mSlider;
  }

  abstract protected int getLayoutId();

  abstract protected Tag newTag();

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View view = super.getView(position, convertView, parent);
    Tag tag = (Tag) view.getTag();
    tag.mPosition = position;
    tag.mId = getItemId(position);

    view.clearAnimation();

    return view;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {
    View v = mLayoutInflater.inflate(getLayoutId(), null);
    Tag tag = newTag();
    setupTag(tag, v);
    v.setTag(tag);
    return v;
  }

  protected void setupTag(Tag tag, View v)
  {
    tag.mName = (TextView) v.findViewById(R.id.name);
    tag.mSynopsis = (TextView) v.findViewById(R.id.synopsis);
    tag.mExpires = (TextView) v.findViewById(R.id.expires);
    tag.mImage = (ImageView) v.findViewById(R.id.image);

    // The Slider touch listener lets us slide the view off the screen
    // The onClickListener then allows clicks through and translates
    // to onItemClick
    tag.mSlider = new Slider(v, this);
    v.setOnClickListener(this);
    v.setOnTouchListener(tag.mSlider);
  }

  protected String formatTime(long time)
  {
    mDate.setTime(time);
    if (DateUtils.isToday(time))
    {
      return mTimeFormat.format(mDate);
    }
    else
    {
      return mDateFormat.format(mDate);
    }
  }

  public void onClick(View v)
  {
    Tag tag = (Tag) v.getTag();

    ((ListView)(v.getParent())).performItemClick(v, tag.mPosition, tag.mId);
  }

  public void onSlideComplete(View v)
  {
    Tag tag = (Tag) v.getTag();

    mItemEventListener.onDeleteRequest(tag.mId);
  }
}
