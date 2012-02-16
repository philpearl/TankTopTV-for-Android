package tv.tanktop.db;

import tv.tanktop.db.DBDefinition.WatchListEpisodeTable;
import tv.tanktop.db.DBDefinition.WatchListTable;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Not strictly necessary to have a content provider as we are within our own
 * app, but it makes some stuff easier I think
 */
public class TanktopContentProvider extends ContentProvider
{
  private static final String TAG = "TanktopContentProvider";
  public static final String AUTHORITY = "tv.tanktop.provider";

  public static final Uri WATCHLIST_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).path("/watchlist").build();
  public static final Uri WATCHLIST_EPISODE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).path("/watchlist/episodes").build();

  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final int MATCH_WATCHLIST = 1;
  private static final int MATCH_WATCHLIST_ID = 2;
  private static final int MATCH_WATCHLIST_ID_EPISODES = 3; //TODO implement this match
  private static final int MATCH_WATCHLIST_EPISODES = 4; //TODO implement this match
  private static final int MATCH_WATCHLIST_EPISODES_ID = 5; //TODO implement this match
  static
  {
    sUriMatcher.addURI(AUTHORITY, "watchlist", MATCH_WATCHLIST);
    sUriMatcher.addURI(AUTHORITY, "watchlist/#", MATCH_WATCHLIST_ID);
    sUriMatcher.addURI(AUTHORITY, "watchlist/#/episodes", MATCH_WATCHLIST_ID_EPISODES);
    sUriMatcher.addURI(AUTHORITY, "watchlist/episodes", MATCH_WATCHLIST_EPISODES);
    sUriMatcher.addURI(AUTHORITY, "watchlist/episodes/#", MATCH_WATCHLIST_EPISODES_ID);
  }

  private DBOpenHelper mOpenHelper;

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs)
  {

    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);
    selection = modifySelectionForUri(match, uri, selection);

    Log.d(TAG, "delete " + table + " " + selection);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    return db.delete(table, selection, selectionArgs);
  }

  @Override
  public String getType(Uri uri)
  {
    int match = sUriMatcher.match(uri);
    switch (match)
    {
    case MATCH_WATCHLIST:
      return "vnd.android.cursor.dir/watchlist";
    case MATCH_WATCHLIST_ID:
      return "vnd.android.cursor.item/watchlist";
    case MATCH_WATCHLIST_EPISODES:
      return "vnd.android.cursor.dir/watchlistepisode";
    case MATCH_WATCHLIST_ID_EPISODES:
      return "vnd.android.cursor.dir/watchlistepisode";
    case MATCH_WATCHLIST_EPISODES_ID:
      return "vnd.android.cursor.item/watchlistepisode";
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values)
  {
    Log.d(TAG, "Insert " + uri);
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    long id = db.insert(table, null, values);

    return ContentUris.withAppendedId(uri, id);
  }

  @Override
  public boolean onCreate()
  {
    mOpenHelper = new DBOpenHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder)
  {
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);
    selection = modifySelectionForUri(match, uri, selection);

    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    Log.d(TAG, "query " + table + " " + selection);
    Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    Log.d(TAG, "Cursor " + cursor.getCount());

    // We set the notification URI here.  We don't kick it ourselves though - we leave that
    // to the code that makes changes
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs)
  {
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);
    selection = modifySelectionForUri(match, uri, selection);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    return db.update(table, values, selection, selectionArgs);
  }

  private String getTableForUri(int match, Uri uri)
  {
    switch (match)
    {
    case MATCH_WATCHLIST_ID:
    case MATCH_WATCHLIST:
      return DBDefinition.WatchListTable.NAME;

    case MATCH_WATCHLIST_EPISODES:
    case MATCH_WATCHLIST_ID_EPISODES:
    case MATCH_WATCHLIST_EPISODES_ID:
      return DBDefinition.WatchListEpisodeTable.NAME;

    default:
      throw new IllegalArgumentException("Uri not recognised " + uri);
    }
  }

  private String modifySelectionForUri(int match, Uri uri, String selection)
  {
    StringBuilder sb = new StringBuilder();

    if ((match == MATCH_WATCHLIST) || (match == MATCH_WATCHLIST_EPISODES))
    {
      // No modification
      return selection;
    }

    if (selection != null)
    {
      sb.append("(");
    }

    switch (match)
    {
    case MATCH_WATCHLIST_ID:
      // Alter query to ensure the ID is selected
      sb.append(WatchListTable.COL_PROGRAMME_ID).append(" = ").append(uri.getLastPathSegment());
      break;

    case MATCH_WATCHLIST_ID_EPISODES:
      // Episodes for the given programme
      sb.append(WatchListEpisodeTable.COL_PROGRAMME_ID).append(" = ").append(uri.getPathSegments().get(1));
      break;

    case MATCH_WATCHLIST_EPISODES_ID:
      sb.append(WatchListEpisodeTable.COL_EPISODE_ID).append(" = ").append(uri.getLastPathSegment());
      break;
    }

    if (selection != null)
    {
      sb.append(") AND (").append(selection).append(")");
    }

    return sb.toString();
  }
}
