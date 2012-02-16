package tv.tanktop;

import tv.tanktop.db.DBDefinition.WatchListEpisodeTable;
import tv.tanktop.db.TanktopContentProvider;
import tv.tanktop.utils.NetImageLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class WLEpisodeFragment extends ListFragment implements LoaderCallbacks<Cursor>
{
  private static final String TAG = "WLEpisodeFragment";

  public static final String ARG_PG_ID = "pg_id";
  public static final String ARG_PG_NAME = "pg_name";

  private WLEpisodeAdapter mAdapter;

  public interface QUERY
  {
    public static final String[] PROJECTION = new String[] {
      WatchListEpisodeTable.COL_EPISODE_ID,
      WatchListEpisodeTable.COL_EPISODE_NAME,
      WatchListEpisodeTable.COL_SYNOPSIS,
      WatchListEpisodeTable.COL_IMAGE,
      WatchListEpisodeTable.COL_URL,
      WatchListEpisodeTable.COL_EXPIRES,
    };

    public static final int COL_EPISODE_ID = 0;
    public static final int COL_EPISODE_NAME = 1;
    public static final int COL_SYNOPSIS = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_URL = 4;
    public static final int COL_EXPIRES = 5;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    TTContextHolder activity = (TTContextHolder)getActivity();
    TanktopContext context = activity.getContext();
    mAdapter = new WLEpisodeAdapter(context, new NetImageLoader(context, new Handler()));
    setListAdapter(mAdapter);

    getActivity().setTitle(getArguments().getString(ARG_PG_NAME));

    setRetainInstance(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated");

    if (savedInstanceState == null)
    {
      getLoaderManager().initLoader(0, getArguments(), this);
      setListShown(false);
    }
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    long pg_id = args.getLong(ARG_PG_ID);
    Uri uri = TanktopContentProvider.WATCHLIST_CONTENT_URI.buildUpon().appendPath(Long.toString(pg_id)).appendPath("episodes").build();
    return new CursorLoader(getActivity(),
        uri,
        QUERY.PROJECTION, null, null, null);
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

  public void onLoaderReset(Loader<Cursor> loader)
  {
    mAdapter.swapCursor(null);
  }

  public static Fragment newInstance(long id, String pg_name)
  {
    Bundle args = new Bundle(2);
    args.putLong(ARG_PG_ID, id);
    args.putString(ARG_PG_NAME, pg_name);
    WLEpisodeFragment frag = new WLEpisodeFragment();
    frag.setArguments(args);
    return frag;
  }
}
