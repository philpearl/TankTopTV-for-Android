package tv.tanktop;

import tv.tanktop.db.DBDefinition.WatchListTable;
import tv.tanktop.db.TanktopContentProvider;
import tv.tanktop.utils.NetImageLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class WatchListFragment extends ListFragment implements LoaderCallbacks<Cursor>
{
  private static final String TAG = "WatchListFragment";

  private WatchListAdapter mAdapter;

  public interface WATCHLIST_QUERY
  {
    public static final String[] PROJECTION = new String[] {
      WatchListTable.COL_PROGRAMME_ID,
      WatchListTable.COL_PROGRAMME_NAME,
      WatchListTable.COL_SYNOPSIS,
      WatchListTable.COL_IMAGE,
    };

    public static final int COL_PROG_ID = 0;
    public static final int COL_PROG_NAME = 1;
    public static final int COL_SYNOPSIS = 2;
    public static final int COL_IMAGE = 3;

  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    TanktopTVActivity activity = (TanktopTVActivity)getActivity();
    mAdapter = new WatchListAdapter(activity, new NetImageLoader(activity.getContext(), new Handler()));
    setListAdapter(mAdapter);

    setRetainInstance(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated");

    if (savedInstanceState == null)
    {
      getLoaderManager().initLoader(0, null, this);
      setListShown(false);
    }
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    return new CursorLoader(getActivity(),
        TanktopContentProvider.WATCHLIST_CONTENT_URI,
        WATCHLIST_QUERY.PROJECTION, null, null, null);
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
  {
    Log.d(TAG, "Cursor count " + cursor.getCount());
    mAdapter.swapCursor(cursor);
    if (isResumed())
    {
      setListShown(true);
    }
    else
    {
      setListShownNoAnimation(true);
    }
  }

  public void onLoaderReset(Loader<Cursor> arg0)
  {
    mAdapter.swapCursor(null);
  }
}
