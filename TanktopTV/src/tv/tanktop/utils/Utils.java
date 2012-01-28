package tv.tanktop.utils;

import java.util.Collection;

public class Utils
{
  /**
   * Join items in the collection using the supplied separator.
   * @param sb
   * @param collection
   * @param sep
   */
  public static void join(StringBuilder sb, Collection<String> collection, String sep)
  {
    int count = collection.size();
    for (String item : collection)
    {
      sb.append(item);
      count--;
      if (count != 0)
      {
        sb.append(sep);
      }
    }
  }
}
