package tv.tanktop.db.util;

/**
 * Integer column
 */
public class IntColumn extends TypedColumn<Integer>
{
  public IntColumn(String name)
  {
    super(name);
  }

  public IntColumn(String name, int defaultVal)
  {
    super(name);
  }

  @Override
  protected String getTypeName()
  {
    return "INTEGER";
  }
}
