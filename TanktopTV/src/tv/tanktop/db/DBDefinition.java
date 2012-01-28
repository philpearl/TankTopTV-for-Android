package tv.tanktop.db;

import tv.tanktop.db.util.IntColumn;
import tv.tanktop.db.util.PrimaryKey;
import tv.tanktop.db.util.Table;
import tv.tanktop.db.util.TextColumn;
import android.provider.BaseColumns;

public final class DBDefinition
{
  public static final String NAME = "tanktop";
  public static final int VERSION = 1;

  public static final class WatchListTable implements BaseColumns
  {
    public static final String NAME = "watchlist";

    public static final String COL_PROGRAMME_ID = "prog_id";
    public static final String COL_PROGRAMME_NAME = "prog_name";
    public static final String COL_IMAGE = "image";
    public static final String COL_SYNOPSIS = "synopsis";


    public static final Table TABLE = new Table(NAME)
    .addColumn(new PrimaryKey(_ID))
    .addColumn(new IntColumn(COL_PROGRAMME_ID).notNull().unique())
    .addColumn(new TextColumn(COL_PROGRAMME_NAME).notNull().unique())
    .addColumn(new TextColumn(COL_IMAGE))
    .addColumn(new TextColumn(COL_SYNOPSIS))
    ;

  }
}
