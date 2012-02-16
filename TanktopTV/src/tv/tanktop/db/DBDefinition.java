package tv.tanktop.db;

import tv.tanktop.db.util.BoolColumn;
import tv.tanktop.db.util.ForeignKey;
import tv.tanktop.db.util.IntColumn;
import tv.tanktop.db.util.PrimaryKey;
import tv.tanktop.db.util.Table;
import tv.tanktop.db.util.TextColumn;
import android.provider.BaseColumns;

public final class DBDefinition
{
  public static final String NAME = "tanktop";
  public static final int VERSION = 3;

  public static final class WatchListTable implements BaseColumns
  {
    public static final String NAME = "watchlist";

    public static final String COL_PROGRAMME_ID = BaseColumns._ID;
    public static final String COL_PROGRAMME_NAME = "prog_name";
    public static final String COL_IMAGE = "image";
    public static final String COL_SYNOPSIS = "synopsis";
    public static final String COL_TOUCHED = "touched";
    public static final String COL_EXPIRES = "expires";
    public static final String COL_EPISODE_COUNT = "ep_count";

    public static final Table TABLE = new Table(NAME)
    .addColumn(new PrimaryKey(COL_PROGRAMME_ID))
    .addColumn(new TextColumn(COL_PROGRAMME_NAME).notNull().unique())
    .addColumn(new TextColumn(COL_IMAGE))
    .addColumn(new TextColumn(COL_SYNOPSIS))
    .addColumn(new BoolColumn(COL_TOUCHED))
    .addColumn(new IntColumn(COL_EXPIRES))
    .addColumn(new IntColumn(COL_EPISODE_COUNT))
    ;
  }

  public static final class WatchListEpisodeTable implements BaseColumns
  {
    public static final String NAME = "watchlist_eps";

    public static final String COL_EPISODE_ID = BaseColumns._ID;
    public static final String COL_PROGRAMME_ID = "pg_id";
    public static final String COL_EPISODE_NAME = "ep_name";
    public static final String COL_IMAGE = "image";
    public static final String COL_SYNOPSIS = "synopsis";
    public static final String COL_TOUCHED = "touched";
    public static final String COL_EXPIRES = "expires";
    public static final String COL_URL = "url";

    public static final Table TABLE = new Table(NAME)
    .addColumn(new PrimaryKey(COL_EPISODE_ID))
    .addColumn(new ForeignKey(COL_PROGRAMME_ID, WatchListTable.NAME))
    .addColumn(new TextColumn(COL_EPISODE_NAME))
    .addColumn(new TextColumn(COL_IMAGE))
    .addColumn(new TextColumn(COL_URL))
    .addColumn(new TextColumn(COL_SYNOPSIS))
    .addColumn(new BoolColumn(COL_TOUCHED))
    .addColumn(new IntColumn(COL_EXPIRES))
    ;
  }
}
