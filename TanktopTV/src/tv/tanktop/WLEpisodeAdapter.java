package tv.tanktop;

import tv.tanktop.WLEpisodeFragment.QUERY;
import tv.tanktop.utils.NetImageLoader;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;

public class WLEpisodeAdapter extends TimeCursorAdapter
{

  public WLEpisodeAdapter(Context context, NetImageLoader imageLoader)
  {
    super(context, imageLoader);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    Tag tag = (Tag) view.getTag();

    tag.mName.setText(cursor.getString(QUERY.COL_EPISODE_NAME));
    tag.mSynopsis.setText(Html.fromHtml(cursor.getString(QUERY.COL_SYNOPSIS)));
    tag.mExpires.setText(formatTime(cursor.getLong(QUERY.COL_EXPIRES)));
    tag.mImage.setImageDrawable(mImageLoader.getImage(cursor.getString(QUERY.COL_IMAGE), tag.mImage));
  }

  @Override
  protected int getLayoutId()
  {
    return R.layout.wl_ep_item;
  }

  @Override
  protected Tag newTag()
  {
    return new Tag();
  }
}
