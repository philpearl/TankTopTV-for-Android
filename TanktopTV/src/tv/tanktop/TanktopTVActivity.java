package tv.tanktop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TanktopTVActivity extends FragmentActivity
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
      new RefreshWatchlistTast(mContext).execute();
    }
  }
}
