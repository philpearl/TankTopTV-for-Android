package tv.tanktop;


import tv.tanktop.WatchListFragment.WATCHLIST_QUERY;
import tv.tanktop.utils.NetImageLoader;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WatchListAdapter extends TimeCursorAdapter implements OnClickListener
{
  public WatchListAdapter(Context context, NetImageLoader imageLoader)
  {
    super(context, imageLoader);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    Tag tag = (Tag) view.getTag();

    tag.mName.setText(cursor.getString(WATCHLIST_QUERY.COL_PROG_NAME));
    tag.mSynopsis.setText(Html.fromHtml(cursor.getString(WATCHLIST_QUERY.COL_SYNOPSIS)));
    tag.mExpires.setText(formatTime(cursor.getLong(WATCHLIST_QUERY.COL_EXPIRES)));
    tag.mImage.setImageDrawable(mImageLoader.getImage(cursor.getString(WATCHLIST_QUERY.COL_IMAGE), tag.mImage));
    tag.mEpCount.setText(new StringBuilder("(").append(cursor.getInt(WATCHLIST_QUERY.COL_EPISODE_COUNT)).append(")").toString());
  }

  public class Tag extends TimeCursorAdapter.Tag
  {
    TextView mEpCount;
  }

  @Override
  protected int getLayoutId()
  {
    return R.layout.watchlist_item;
  }

  @Override
  protected Tag newTag()
  {
    return new Tag();
  }

  @Override
  protected void setupTag(TimeCursorAdapter.Tag tag, View v)
  {
    super.setupTag(tag, v);
    Tag ttag = (Tag)tag;
    ttag.mEpCount = (TextView) v.findViewById(R.id.epCount);
  }
}
