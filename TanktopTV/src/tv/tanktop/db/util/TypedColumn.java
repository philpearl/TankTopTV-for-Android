package tv.tanktop.db.util;

import android.database.DatabaseUtils;

public abstract class TypedColumn<T> extends Column
{
  protected final T mDefault;
  protected String mStringDefault;

  public TypedColumn(String name)
  {
    super(name);
    mDefault = null;
  }

  public TypedColumn(String name, T defaultVal)
  {
    super(name);
    mHasDefault = true;
    mDefault = defaultVal;
  }

  TypedColumn<T> setDefault(String stringDefault)
  {
    mHasDefault = true;
    mStringDefault = DatabaseUtils.sqlEscapeString(stringDefault);
    return this;
  }

  @Override
  protected void appendDefault(StringBuilder sb)
  {
    if (mStringDefault != null)
    {
      sb.append(mStringDefault);
    }
    else
    {
      sb.append(mDefault);
    }
  }
}
