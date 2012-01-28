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
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class RefreshWatchlistTast extends AsyncTask<String, Void, Void>
{
  private static final String TAG = "RefreshWatchList";

  private final TanktopContext mContext;
  private final HttpLayer mHttpLayer;
  private final TanktopStore mStore;

  public RefreshWatchlistTast(TanktopContext context)
  {
    mContext = context;
    mHttpLayer = mContext.getHttpLayer();
    mStore = mContext.getStore();
  }

  @Override
  protected Void doInBackground(String... params)
  {
    Log.d(TAG, "doInBackground");
    try
    {
      mHttpLayer.login(mStore.getUserName(), mStore.getPassword());

      JSONObject watchlist = mHttpLayer.getWatchList();

      Log.d(TAG, watchlist.toString(2));

      JSONArray array = watchlist.getJSONArray("watchlist");

      ContentResolver cr = mContext.getContentResolver();
      ContentValues values = new ContentValues(4);

      // Clear out all the old items
      cr.delete(TanktopContentProvider.WATCHLIST_CONTENT_URI, "1=1",null);

      long lastId = -1;
      for (int index = 0; index < array.length(); index++)
      {
        JSONArray item = array.getJSONArray(index);
        JSONObject prog = item.getJSONObject(0);

        // For now we try to avoid duplicate progs
        long id = prog.getLong("id");
        if (id != lastId)
        {
          lastId = id;
          values.put(WatchListTable.COL_PROGRAMME_ID, id);
          values.put(WatchListTable.COL_PROGRAMME_NAME, prog.getString("name"));
          values.put(WatchListTable.COL_IMAGE, prog.getString("image"));
          values.put(WatchListTable.COL_SYNOPSIS, prog.getString("synopsis"));

          Uri uri = cr.insert(TanktopContentProvider.WATCHLIST_CONTENT_URI, values);
          Log.d(TAG, "uri " + uri);
        }
      }

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

    return null;
  }
}
