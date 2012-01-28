package tv.tanktop.db.util;

import android.database.DatabaseUtils;

/**
 * A text column
 */
public class TextColumn extends TypedColumn<String>
{
  public TextColumn(String name)
  {
    super(name);
  }

  public TextColumn(String name, String defaultVal)
  {
    super(name, DatabaseUtils.sqlEscapeString(defaultVal));
  }

  @Override
  protected String getTypeName()
  {
    return "TEXT";
  }
}
