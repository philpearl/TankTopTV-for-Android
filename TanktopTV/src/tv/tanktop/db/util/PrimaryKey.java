package tv.tanktop.db.util;

/**
 * A primary key.
 * <p>
 * We don't expect this to be used externally
 */
public class PrimaryKey extends IntColumn
{
  public PrimaryKey(String name)
  {
    super(name);
  }

  @Override
  protected void qualify(StringBuilder sb)
  {
    // We use AUTOINCREMENT, which ensures that our IDs will form a
    // monotonically-increasing sequence.
    sb.append(" PRIMARY KEY AUTOINCREMENT");
  }

}
