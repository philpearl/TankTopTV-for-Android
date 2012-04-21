package tv.tanktop;

import tv.tanktop.service.SyncService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class TanktopTVActivity extends FragmentActivity implements TTContextHolder
{
  private static final String TAG = "TanktopTVActivity";
  private TanktopContext mContext;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    mContext = new TanktopContext(this);

    setContentView(R.layout.main);

    if (savedInstanceState == null)
    {
      TanktopStore store = mContext.getStore();
      boolean needLogin = (store.getPassword() == null);

      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.add(R.id.fragment1, needLogin ? new LoginFragment() : new WatchListFragment());
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.commit();

      if (!needLogin)
      {
        sync();
      }
    }
  }

  private void sync()
  {
    startService(new Intent(this, SyncService.class).setAction(SyncService.ACTION_SYNC));
  }

  public void onLoginSuccess()
  {
    Log.d(TAG, "onLoginSuccess");
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragment1, new WatchListFragment());
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    ft.commit();

    sync();
  }

  public TanktopContext getContext()
  {
    return mContext;
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
  }
}
