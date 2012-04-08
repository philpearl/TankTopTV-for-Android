package tv.tanktop;

import tv.tanktop.sync.SyncService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TanktopTVActivity extends FragmentActivity implements TTContextHolder
{
  private TanktopContext mContext;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    mContext = new TanktopContext(this);

    setContentView(R.layout.main);

    if (savedInstanceState == null)
    {
      startService(new Intent(this, SyncService.class));
    }
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
