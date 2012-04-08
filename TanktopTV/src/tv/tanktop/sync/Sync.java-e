package tv.tanktop.sync;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.tanktop.TanktopContext;
import tv.tanktop.TanktopStore;
import tv.tanktop.db.DBDefinition.WatchListEpisodeTable;
import tv.tanktop.db.DBDefinition.WatchListTable;
import tv.tanktop.db.TanktopContentProvider;
import tv.tanktop.net.HttpLayer;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Sync
{
  private static final String TAG = "Sync";
  private final TanktopContext mContext;
  private final SimpleDateFormat mExpiryDateFormat;

  public Sync(TanktopContext context)
  {
    mContext = context;
    // TODO: probably want a GMT locale somehow??
    mExpiryDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
  }

  public void run()
  {
    HttpLayer httpLayer = mContext.newHttpLayer();
    TanktopStore store = mContext.getStore();

    // Login
    // Find episodes to mark watched
    // Find programmes to unfavourite
    // update the DB

    try
    {
      httpLayer.login(store.getUserName(), store.getPassword());

      ContentResolver cr = mContext.getContentResolver();
      ContentValues values = new ContentValues(7);

      // Look for episodes marked as watched
      Cursor cursor = cr.query(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI),
          new String[] {
            WatchListEpisodeTable.COL_EPISODE_ID
          },
          WatchListEpisodeTable.COL_DELETED + " = 1", null, null);
      try
      {
        while (cursor.moveToNext())
        {
          httpLayer.markEpisodeSeen(cursor.getLong(0));
        }
      }
      finally
      {
        cursor.close();
      }

      // Look for programmes to unfavourite
      cursor = cr.query(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_CONTENT_URI),
          new String[] {
            WatchListTable.COL_PROGRAMME_ID
          },
          WatchListTable.COL_DELETED + " = 1", null, null);
      try
      {
        while (cursor.moveToNext())
        {
          httpLayer.removeFromWatchlist(cursor.getLong(0));
        }
      }
      finally
      {
        cursor.close();
      }

      // Pull down changes to the watchlist
      JSONObject watchlist = httpLayer.getWatchList();

      Log.d(TAG, watchlist.toString(2));

      JSONArray array = watchlist.getJSONArray("watchlist");

      // Mark items as untouched
      values.put(WatchListTable.COL_TOUCHED, 0);
      cr.update(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_CONTENT_URI), values, null, null);
      cr.update(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI), values, null, null);

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

        Uri uri = TanktopContentProvider.addCallerIsSyncParam(ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_CONTENT_URI, id));
        int updated = cr.update(uri, values, null, null);
        Log.d(TAG, "updated " + updated);
        if (updated == 0)
        {
          Uri newuri = cr.insert(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_CONTENT_URI), values);
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

          updated = cr.update(TanktopContentProvider.addCallerIsSyncParam(ContentUris.withAppendedId(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI, ep_id)), values, null, null);
          Log.d(TAG, "ep updated " + updated);
          if (updated == 0)
          {
            Uri newuri = cr.insert(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI), values);
            Log.d(TAG, "uri " + uri);
          }
        }
      }

      // Delete anything that's still untouched
      int deleted = cr.delete(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_CONTENT_URI), WatchListTable.COL_TOUCHED + "=0", null);
      Log.d(TAG, "deleted " + deleted);
      deleted = cr.delete(TanktopContentProvider.addCallerIsSyncParam(TanktopContentProvider.WATCHLIST_EPISODE_CONTENT_URI), WatchListTable.COL_TOUCHED + "=0", null);
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
