package tv.tanktop.db.util;

import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public class Table
{
  private static final String TAG = "Table";
  private String mName;
  private HashMap<String,Column> mColumns = new HashMap<String,Column>();
  private String mTableConstraint;

  /**
   * Constructor
   * @param name - the name of the database table
   */
  public Table(String name)
  {
    mName = name;
  }

  /**
   * Add a column to the table
   * @param column - a DBColumn to add to the table
   * @return this - reference to this table
   */
  public Table addColumn(Column column)
  {
    mColumns.put(column.mName, column);
    return this;
  }

  /**
   * Set the table constraint string
   * @param tableConstraint - the table constraint clause to be inserted in the column list
   * @return
   */
  public Table setTableConstraint(String tableConstraint)
  {
    mTableConstraint = tableConstraint;
    return this;
  }

  /**
   * Get a string suitable for submitting to SQL to create the table
   * @return A string containing the create table command.
   */
  public String getCreateString()
  {
    Log.d(TAG,"Building create string");
    StringBuilder sb = new StringBuilder();

    sb.append("CREATE TABLE ");
    sb.append(mName);
    sb.append(" (");

    /*
     * Get the strings for declaring each column
     */
    Iterator<Column> iter = mColumns.values().iterator();
    while (iter.hasNext())
    {
      Column col = iter.next();

      col.appendCreateString(sb);
      if (iter.hasNext())
      {
        sb.append(",");
      }
    }

    if (mTableConstraint != null)
    {
      sb.append(" ").append(mTableConstraint);
    }
    sb.append(");");

    String createTableSql = sb.toString();
    Log.v(TAG, createTableSql);

    return createTableSql;
  }

  /**
   * @return An SQL command to drop the table
   */
  public String getDropString()
  {
    return new StringBuilder("DROP TABLE IF EXISTS ").append(mName).toString();
  }
}
