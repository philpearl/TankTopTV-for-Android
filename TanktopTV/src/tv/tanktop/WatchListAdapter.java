package tv.tanktop;

import tv.tanktop.WatchListFragment.WATCHLIST_QUERY;
import tv.tanktop.utils.NetImageLoader;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchListAdapter extends CursorAdapter
{
  private final LayoutInflater mLayoutInflater;
  private final NetImageLoader mImageLoader;

  public WatchListAdapter(Context context, NetImageLoader imageLoader)
  {
    super(context, null, 0);
    mLayoutInflater = LayoutInflater.from(context);
    mImageLoader = imageLoader;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    Tag tag = (Tag) view.getTag();

    tag.mProgName.setText(cursor.getString(WATCHLIST_QUERY.COL_PROG_NAME));
    tag.mProgSynopsis.setText(Html.fromHtml(cursor.getString(WATCHLIST_QUERY.COL_SYNOPSIS)));

    tag.mProgImage.setImageDrawable(mImageLoader.getImage(cursor.getString(WATCHLIST_QUERY.COL_IMAGE), tag.mProgImage));
  }

  class Tag
  {
    TextView mProgName;
    TextView mProgSynopsis;
    ImageView mProgImage;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {

    View v = mLayoutInflater.inflate(R.layout.watchlist_item, null);
    Tag tag = new Tag();
    tag.mProgName = (TextView) v.findViewById(R.id.progName);
    tag.mProgSynopsis = (TextView) v.findViewById(R.id.progSynopsis);
    tag.mProgImage = (ImageView) v.findViewById(R.id.progImage);
    v.setTag(tag);
    return v;
  }

}
