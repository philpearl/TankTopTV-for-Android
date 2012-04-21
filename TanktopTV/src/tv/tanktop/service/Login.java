package tv.tanktop.service;

import java.io.IOException;

import tv.tanktop.TanktopContext;
import tv.tanktop.net.HttpLayer;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

public class Login
{
  private static final String TAG = "Login";
  private final TanktopContext mContext;

  public Login(TanktopContext context)
  {
    mContext = context;
  }

  public void run(Intent intent)
  {
    HttpLayer httpLayer = mContext.newHttpLayer();

    String username = intent.getStringExtra(SyncService.EXTRA_USERNAME);
    String password = intent.getStringExtra(SyncService.EXTRA_PASSWORD);
    ResultReceiver resultReceiver = intent.getParcelableExtra(SyncService.EXTRA_RESULT_RECEIVER);
    try
    {
      httpLayer.login(username, password);
      mContext.getStore().setCredentials(username, password);
      resultReceiver.send(SyncService.RESULT_OK, null);
    }
    catch (IOException e)
    {
      Log.e(TAG, "Login failed", e);
      resultReceiver.send(SyncService.RESULT_FAILED, null);
    }
    finally
    {
      httpLayer.onDestroy();
    }
  }
}
