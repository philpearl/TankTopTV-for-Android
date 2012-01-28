package tv.tanktop.db.util;

public class BlobColumn extends Column
{
  public BlobColumn(String name)
  {
    super(name);
  }

  @Override
  protected String getTypeName()
  {
    return "BLOB";
  }

  @Override
  protected void appendDefault(StringBuilder sb)
  {
    // No default for a blob
    throw new UnsupportedOperationException();
  }
}
