package tv.tanktop.sync;

import tv.tanktop.TanktopContext;
import android.app.IntentService;
import android.content.Intent;

public class SyncService extends IntentService
{
  private static final String TAG = "SyncService";

  private TanktopContext mContext;
  private Sync mSync;

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
  }

  @Override
  protected void onHandleIntent(Intent intent)
  {
    mSync.run();
  }
}
