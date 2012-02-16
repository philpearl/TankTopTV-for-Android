package tv.tanktop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TanktopTVActivity extends FragmentActivity implements TTContextHolder
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

  public TanktopContext getContext()
  {
    return mContext;
  }

  @Override
  protected void onDestroy()
  {
    if ((isFinishing()) && (mRefreshWatchlistTask != null))
    {
      mRefreshWatchlistTask.cancel(false);
    }
    super.onDestroy();
  }
}
