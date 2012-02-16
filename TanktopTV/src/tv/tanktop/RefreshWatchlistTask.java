package tv.tanktop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.tanktop.db.DBDefinition.WatchListEpisodeTable;
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
  private final SimpleDateFormat mExpiryDateFormat;

  public RefreshWatchlistTask(TanktopContext context)
  {
    mContext = context;

    mStore = mContext.getStore();

    // TODO: probably want a GMT locale somehow??
    mExpiryDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
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
      ContentValues values = new ContentValues(7);

      // Mark items as untouched
      values.put(WatchListTable.COL_TOUCHED, 0);
      cr.update(TanktopContentProvider.WATCHLIST_CONTENT_URI, values, null, null);
      cr.update(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, values, null, null);

      for (int index = 0; index < array.length(); index++)
      {
        values.clear();
        JSONObject prog = array.getJSONObject(index);

        // For now we try to avoid duplicate progs
        long id = prog.getLong("id");
        values.put(WatchListTable.COL_TOUCHED, 1);
        values.put(WatchListTable.COL_PROGRAMME_ID, id);
        values.put(WatchListTable.COL_PROGRAMME_NAME, prog.getString("name"));
        values.put(WatchListTable.COL_IMAGE, prog.getString("image"));
        values.put(WatchListTable.COL_SYNOPSIS, prog.getString("synopsis"));

        // We've also got a list of episodes of the programme in the watchlist
        JSONArray episodes = prog.getJSONArray("episodes");
        String expires = episodes.getJSONObject(0).getString("expires");
        values.put(WatchListTable.COL_EXPIRES, parseExpiry(expires));

        values.put(WatchListTable.COL_EPISODE_COUNT, episodes.length());

        int updated = cr.update(ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_CONTENT_URI, id), values, null, null);
        Log.d(TAG, "updated " + updated);
        if (updated == 0)
        {
          Uri uri = cr.insert(TanktopContentProvider.WATCHLIST_CONTENT_URI, values);
          Log.d(TAG, "uri " + uri);
        }

        for (int jndex = 0; jndex < episodes.length(); jndex++)
        {
          JSONObject ep = episodes.getJSONObject(jndex);
          values.clear();
          values.put(WatchListEpisodeTable.COL_TOUCHED, 1);
          long ep_id = ep.getLong("id");
          values.put(WatchListEpisodeTable.COL_EPISODE_ID, ep_id);
          values.put(WatchListEpisodeTable.COL_PROGRAMME_ID, id);
          values.put(WatchListEpisodeTable.COL_EPISODE_NAME, ep.getString("name"));
          values.put(WatchListEpisodeTable.COL_IMAGE, ep.getString("image"));
          values.put(WatchListEpisodeTable.COL_URL, ep.getString("url"));
          values.put(WatchListEpisodeTable.COL_SYNOPSIS, ep.getString("synopsis"));

          expires = episodes.getJSONObject(0).getString("expires");
          values.put(WatchListEpisodeTable.COL_EXPIRES, parseExpiry(expires));

          updated = cr.update(ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, ep_id), values, null, null);
          Log.d(TAG, "ep updated " + updated);
          if (updated == 0)
          {
            Uri uri = cr.insert(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, values);
            Log.d(TAG, "uri " + uri);
          }

        }
      }

      // Delete anything that's still untouched
      int deleted = cr.delete(TanktopContentProvider.WATCHLIST_CONTENT_URI, WatchListTable.COL_TOUCHED + "=0", null);
      Log.d(TAG, "deleted " + deleted);
      deleted = cr.delete(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, WatchListTable.COL_TOUCHED + "=0", null);
      Log.d(TAG, "eps deleted " + deleted);

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

  private long parseExpiry(String expiry)
  {
    try
    {
      return mExpiryDateFormat.parse(expiry).getTime();
    }
    catch (ParseException e)
    {
      return 0;
    }
  }
}
