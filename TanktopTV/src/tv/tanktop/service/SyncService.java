package tv.tanktop.service;

import tv.tanktop.TanktopContext;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SyncService extends IntentService
{
  private static final String TAG = "SyncService";

  public static final String AUTHORITY = "tv.tanktop";
  public static final String ACTION_SYNC = AUTHORITY + ".SYNC";
  public static final String ACTION_LOGIN = AUTHORITY + ".LOGIN";

  public static final String EXTRA_USERNAME = "username";
  public static final String EXTRA_PASSWORD = "password";
  public static final String EXTRA_RESULT_RECEIVER = "result_receiver";

  public static final int RESULT_OK = 0;
  public static final int RESULT_FAILED = 1;

  private TanktopContext mContext;
  private Sync mSync;
  private Login mLogin;

  public SyncService()
  {
    super(TAG);
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    mContext = new TanktopContext(getBaseContext());
    mSync = new Sync(mContext);
    mLogin = new Login(mContext);
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    Log.d(TAG, "onHandleIntent " + intent);
    String action = intent.getAction();
    if (ACTION_SYNC.equals(action))
    {
      mSync.run();
    }
    else if (ACTION_LOGIN.equals(action))
    {
      mLogin.run(intent);
    }
  }
}
