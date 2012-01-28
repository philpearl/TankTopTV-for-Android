package tv.tanktop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper
{
  public DBOpenHelper(Context context)
  {
    super(context, DBDefinition.NAME, null, DBDefinition.VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL(DBDefinition.WatchListTable.TABLE.getCreateString());
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    if (newVersion < oldVersion)
    {
      // Can't go back
      db.execSQL(DBDefinition.WatchListTable.TABLE.getDropString());
      onCreate(db);
    }
    else
    {
      throw new UnsupportedOperationException("Implement DB upgrade");
    }
  }
}
