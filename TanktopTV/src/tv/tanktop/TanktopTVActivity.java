package tv.tanktop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TanktopTVActivity extends FragmentActivity
{
  private TanktopContext mContext;
  private RefreshWatchlistTask mRefreshWatchlistTask;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    mContext = new TanktopContext(this);

    setContentView(R.layout.main);

    if (savedInstanceState == null)
    {
      mRefreshWatchlistTask = new RefreshWatchlistTask(mContext);
      mRefreshWatchlistTask.execute();
    }
  }

  @Override
  protected void onDestroy()
  {
    if (isFinishing())
    {
      mRefreshWatchlistTask.cancel(false);
    }
    super.onDestroy();
  }
}
