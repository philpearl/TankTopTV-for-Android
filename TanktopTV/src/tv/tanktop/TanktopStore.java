package tv.tanktop;

import android.content.Context;
import android.content.SharedPreferences;

public class TanktopStore
{
  private static final String TAG = "TanktopStore";

  private static final String KEY_USER_NAME = "user_name";
  private static final String KEY_PASSWORD = "password";

  private final SharedPreferences mPreferences;

  public TanktopStore(TanktopContext context)
  {
    mPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
  }

  public String getUserName()
  {
    return mPreferences.getString(KEY_USER_NAME, null);
  }

  public String getPassword()
  {
    return mPreferences.getString(KEY_PASSWORD, null);
  }

  public void setCredentials(String username, String password)
  {
    mPreferences.edit()
    .putString(KEY_USER_NAME, username)
    .putString(KEY_PASSWORD, password)
    .commit();
  }
}
