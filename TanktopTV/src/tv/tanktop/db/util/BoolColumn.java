package tv.tanktop.db.util;

/**
 * A Boolean column
 */
public class BoolColumn extends TypedColumn<Boolean>
{

  public BoolColumn(String name, Boolean defaultVal)
  {
    super(name, defaultVal);
  }

  public BoolColumn(String name)
  {
    super(name);
  }

  @Override
  protected String getTypeName()
  {
    return "BOOLEAN";
  }

  @Override
  protected void appendDefault(StringBuilder sb)
  {
    sb.append(mDefault ? "1" : "0");
  }
}
