package tv.tanktop;

import tv.tanktop.db.DBDefinition.WatchListEpisodeTable;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class WLEpisodeFragment extends ListFragment implements LoaderCallbacks<Cursor>, ItemEventListener
{
  private static final String TAG = "WLEpisodeFragment";

  public static final String ARG_PG_ID = "pg_id";
  public static final String ARG_PG_NAME = "pg_name";

  private WLEpisodeAdapter mAdapter;
  private NetImageLoader mImageLoader;

  private Handler mBackgroundHandler;
  private Uri mQueryUri;

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

    HandlerThread handlerThread = new HandlerThread(TAG);
    handlerThread.start();

    mBackgroundHandler = new Handler(handlerThread.getLooper());

    TTContextHolder activity = (TTContextHolder)getActivity();
    TanktopContext context = activity.getContext();
    mImageLoader = new NetImageLoader(context, new Handler());
    mAdapter = new WLEpisodeAdapter(context, this, mImageLoader);
    setListAdapter(mAdapter);

    getActivity().setTitle(getArguments().getString(ARG_PG_NAME));

    setRetainInstance(true);
    setHasOptionsMenu(false);
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

  @Override
  public void onDestroy()
  {
    mImageLoader.onDestroy();
    super.onDestroy();
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle args)
  {
    long pg_id = args.getLong(ARG_PG_ID);
    mQueryUri = TanktopContentProvider.WATCHLIST_CONTENT_URI.buildUpon().appendPath(Long.toString(pg_id)).appendPath("episodes").build();
    return new CursorLoader(getActivity(),
        mQueryUri,
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

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Cursor cursor = (Cursor) getListAdapter().getItem(position);

    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(cursor.getString(QUERY.COL_URL))));
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

  public void onDeleteRequest(final long id)
  {
    Log.d(TAG, "onDeleteRequest " + id);
    mBackgroundHandler.post(new Runnable()
    {
      public void run()
      {
        ContentResolver cr = getActivity().getContentResolver();

        Uri uri = ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, id);
        int deleted = cr.delete(uri, null, null);
        Log.d(TAG, "deleted " + deleted);

        // Hmm, our query URI is a bit different to what we just deleted so we notify it
        // ourselves.
        cr.notifyChange(mQueryUri, null);
      }
    });
  }
}
