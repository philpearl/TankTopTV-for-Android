package tv.tanktop;

import tv.tanktop.WatchListFragment.WATCHLIST_QUERY;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WatchListAdapter extends CursorAdapter
{
  private final LayoutInflater mLayoutInflater;

  public WatchListAdapter(Context context)
  {
    super(context, null, 0);
    mLayoutInflater = LayoutInflater.from(context);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    Tag tag = (Tag) view.getTag();

    tag.mProgName.setText(cursor.getString(WATCHLIST_QUERY.COL_PROG_NAME));
  }

  class Tag
  {
    TextView mProgName;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {

    View v = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null);
    Tag tag = new Tag();
    tag.mProgName = (TextView) v.findViewById(android.R.id.text1);
    v.setTag(tag);
    return v;
  }

}
