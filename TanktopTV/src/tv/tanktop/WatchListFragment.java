package tv.tanktop;

import tv.tanktop.db.DBDefinition.WatchListTable;
import tv.tanktop.db.TanktopContentProvider;
import tv.tanktop.utils.NetImageLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class WatchListFragment extends ListFragment implements LoaderCallbacks<Cursor>, ItemEventListener
{
  private static final String TAG = "WatchListFragment";

  private TimeCursorAdapter mAdapter;
  private NetImageLoader mImageLoader;
  private Handler mBackgroundHandler;

  public interface WATCHLIST_QUERY
  {
    public static final String[] PROJECTION = new String[] {
      WatchListTable.COL_PROGRAMME_ID,
      WatchListTable.COL_PROGRAMME_NAME,
      WatchListTable.COL_SYNOPSIS,
      WatchListTable.COL_IMAGE,
      WatchListTable.COL_EXPIRES,
      WatchListTable.COL_EPISODE_COUNT,
    };

    public static final int COL_PROG_ID = 0;
    public static final int COL_PROG_NAME = 1;
    public static final int COL_SYNOPSIS = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_EXPIRES = 4;
    public static final int COL_EPISODE_COUNT = 5;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    TTContextHolder activity = (TTContextHolder)getActivity();
    TanktopContext context = activity.getContext();

    HandlerThread handlerThread = new HandlerThread(TAG);
    handlerThread.start();

    mBackgroundHandler = new Handler(handlerThread.getLooper());

    mImageLoader = new NetImageLoader(context, handlerThread, new Handler());
    mAdapter = new WatchListAdapter(context, this, mImageLoader);
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

  @Override
  public void onDestroy()
  {
    mImageLoader.onDestroy();
    super.onDestroy();
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

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Cursor cursor = (Cursor) getListAdapter().getItem(position);

    getActivity().startActivity(new Intent(getActivity(), WLEpisodeActivity.class)
      .putExtra(WLEpisodeFragment.ARG_PG_ID, id)
      .putExtra(WLEpisodeFragment.ARG_PG_NAME, cursor.getString(WATCHLIST_QUERY.COL_PROG_NAME)));
  }

  public void onDeleteRequest(final long id)
  {
    Log.d(TAG, "Delete requested for id " + id);
    mBackgroundHandler.post(new Runnable()
    {
      public void run()
      {
        ContentResolver cr = getActivity().getContentResolver();

        Uri uri = ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_CONTENT_URI, id);
        int deleted = cr.delete(uri, null, null);
        Log.d(TAG, "deleted " + deleted);

        cr.notifyChange(uri, null);
      }
    });

  }
}
