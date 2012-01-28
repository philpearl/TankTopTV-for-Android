package tv.tanktop.db.util;

/**
 * A DB column
 */
public abstract class Column
{
  protected String mName;
  protected boolean mHasDefault;
  protected boolean mUnique;
  protected boolean mNotNull;

  public Column(String name)
  {
    mName = name;
  }

  /**
   * Called if the column should be unique
   * @return this.
   */
  public Column unique()
  {
    mUnique = true;
    return this;
  }

  /**
   * Called if the column cannot be null
   * @return this
   */
  public Column notNull()
  {
    mNotNull = true;
    return this;
  }

  protected abstract String getTypeName();

  public void appendCreateString(StringBuilder sb)
  {
    sb.append(mName).append(" ").append(getTypeName());
    qualify(sb);
  }

  protected void qualify(StringBuilder sb)
  {
    if (mNotNull)
    {
      sb.append(" NOT NULL");
    }
    if (mHasDefault)
    {
      sb.append(" DEFAULT ");
      appendDefault(sb);
    }
    if (mUnique)
    {
      sb.append(" UNIQUE");
    }
  }

  /*
   * Append the default value for the column
   */
  abstract protected void appendDefault(StringBuilder sb);
}
