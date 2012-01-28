package tv.tanktop.db.util;

import java.util.ArrayList;
import java.util.Arrays;

import tv.tanktop.utils.Utils;
import android.util.Log;

/**
 * Class representing a DB index over a set of columns.
 */
public class Index
{
  private static final String TAG = "DBIndex";
  private String mName;
  private ArrayList<String> mColumnNames = new ArrayList<String>(1);
  private String mTableName;

  /**
   * Constructor
   * @param name The name for the new index.
   * @param tableName The name of the (existing) table to index.
   * @param columns The list of columns to index over.  Can also be specified
   *        by calling addColumn on the created object.
   */
  public Index(String name, String tableName, String... columns)
  {
    mName = name;
    mTableName = tableName;
    mColumnNames.addAll(Arrays.asList(columns));
  }

  /**
   * Add a column to the index by name.
   * @param columnName The name of column to add.
   * @return this - a self-reference so columns can be added in a declarative
   * style.
   */
  public Index addColumn(String columnName)
  {
    mColumnNames.add(columnName);
    return this;
  }

  /**
   * Get a string suitable for submitting to SQL to create the index
   * @return A string containing the create index command.
   */
  public String getCreateString()
  {
    Log.d(TAG,"Building create string");
    StringBuilder sb = new StringBuilder();

    sb.append("CREATE INDEX ");
    sb.append(mName);
    sb.append(" ON ");
    sb.append(mTableName);
    sb.append(" (");
    Utils.join(sb, mColumnNames, ",");
    sb.append(");");

    String sql = sb.toString();
    Log.v(TAG, sql);
    return sql;
  }

  /**
   * @return An SQL command to drop the table
   */
  public String getDropString()
  {
    return new StringBuilder("DROP INDEX IF EXISTS ").append(mName).toString();
  }
}
