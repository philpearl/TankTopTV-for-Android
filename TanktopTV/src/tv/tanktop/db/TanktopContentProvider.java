package tv.tanktop.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Not strictly necessary to have a content provider as we are within our own
 * app, but it makes some stuff easier I think
 */
public class TanktopContentProvider extends ContentProvider
{
  private static final String TAG = "TanktopContentProvider";
  public static final String AUTHORITY = "tv.tanktop.provider";

  public static final Uri WATCHLIST_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).path("/watchlist").build();

  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private static final int MATCH_WATCHLIST = 1;
  private static final int MATCH_WATCHLIST_ID = 2;
  static
  {
    sUriMatcher.addURI(AUTHORITY, "watchlist", MATCH_WATCHLIST);
    sUriMatcher.addURI(AUTHORITY, "watchlist/#", MATCH_WATCHLIST_ID);
  }

  private DBOpenHelper mOpenHelper;

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs)
  {
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);
    selection = modifySelectionForUri(match, uri, selection);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    try
    {
      return db.delete(table, selection, selectionArgs);
    }
    finally
    {
      db.close();
    }
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
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values)
  {
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    try
    {
      long id = db.insert(table, null, values);

      return ContentUris.withAppendedId(uri, id);
    }
    finally
    {
      db.close();
    }
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
    try
    {
      return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }
    finally
    {
      db.close();
    }
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs)
  {
    int match = sUriMatcher.match(uri);
    String table = getTableForUri(match, uri);
    selection = modifySelectionForUri(match, uri, selection);

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    try
    {
      return db.update(table, values, selection, selectionArgs);
    }
    finally
    {
      db.close();
    }
  }

  private String getTableForUri(int match, Uri uri)
  {
    switch (match)
    {
    case MATCH_WATCHLIST_ID:
    case MATCH_WATCHLIST:
      return DBDefinition.WatchListTable.NAME;

    default:
      throw new IllegalArgumentException("Uri not recognised " + uri);
    }
  }

  private String modifySelectionForUri(int match, Uri uri, String selection)
  {
    switch (match)
    {
    case MATCH_WATCHLIST_ID:
      // Alter query to ensure the ID is selected
      if (selection != null)
      {
        return "(" + BaseColumns._ID + " = " + uri.getLastPathSegment() + ") AND (" + selection + ")";
      }
      return BaseColumns._ID + " = " + uri.getLastPathSegment();

    default:
      return selection;
    }

  }
}
