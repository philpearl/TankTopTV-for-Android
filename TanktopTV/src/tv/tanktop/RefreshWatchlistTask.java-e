package tv.tanktop;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.tanktop.db.DBDefinition.WatchListTable;
import tv.tanktop.db.TanktopContentProvider;
import tv.tanktop.net.HttpLayer;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

// TODO: Threadedness
public class RefreshWatchlistTask extends AsyncTask<String, Void, Void>
{
  private static final String TAG = "RefreshWatchList";

  private final TanktopContext mContext;
  private final TanktopStore mStore;

  public RefreshWatchlistTask(TanktopContext context)
  {
    mContext = context;

    mStore = mContext.getStore();
  }

  @Override
  protected Void doInBackground(String... params)
  {
    Log.d(TAG, "doInBackground");
    // New HTTP layer for each call
    HttpLayer httpLayer = mContext.getHttpLayer();
    try
    {
      httpLayer.login(mStore.getUserName(), mStore.getPassword());

      JSONObject watchlist = httpLayer.getWatchList();

      Log.d(TAG, watchlist.toString(2));

      JSONArray array = watchlist.getJSONArray("watchlist");

      ContentResolver cr = mContext.getContentResolver();
      ContentValues values = new ContentValues(5);

      // Mark items as untouched
      values.put(WatchListTable.COL_TOUCHED, 0);
      cr.update(TanktopContentProvider.WATCHLIST_CONTENT_URI, values, null, null);

      values.put(WatchListTable.COL_TOUCHED, 1);
      for (int index = 0; index < array.length(); index++)
      {
        JSONObject prog = array.getJSONObject(index);

        // For now we try to avoid duplicate progs
        long id = prog.getLong("id");
        values.put(WatchListTable.COL_PROGRAMME_ID, id);
        values.put(WatchListTable.COL_PROGRAMME_NAME, prog.getString("name"));
        values.put(WatchListTable.COL_IMAGE, prog.getString("image"));
        values.put(WatchListTable.COL_SYNOPSIS, prog.getString("synopsis"));

        // We've also got a list of episodes of the programme in the watchlist
        // prog.getJSONArray("episodes")

        int updated = cr.update(ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_CONTENT_URI, id), values, null, null);
        Log.d(TAG, "updated " + updated);
        if (updated == 0)
        {
          Uri uri = cr.insert(TanktopContentProvider.WATCHLIST_CONTENT_URI, values);
          Log.d(TAG, "uri " + uri);
        }
      }

      // Delete anything that's still untouched
      int deleted = cr.delete(TanktopContentProvider.WATCHLIST_CONTENT_URI, WatchListTable.COL_TOUCHED + "=0", null);
      Log.d(TAG, "deleted " + deleted);

      cr.notifyChange(TanktopContentProvider.WATCHLIST_CONTENT_URI, null);
    }
    catch (HttpResponseException e)
    {
      Log.e(TAG, "Error getting watchlist", e);
    }
    catch (IOException e)
    {
      Log.e(TAG, "Error getting watchlist", e);
    }
    catch (JSONException e)
    {
      Log.e(TAG, "Error getting watchlist", e);
    }
    finally
    {
      httpLayer.onDestroy();
    }

    return null;
  }
}
