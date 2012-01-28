package tv.tanktop;

import android.app.Activity;
import android.os.Bundle;

public class TanktopTVActivity extends Activity
{
  private TanktopContext mContext;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    mContext = new TanktopContext(this);

    setContentView(R.layout.main);

    new RefreshWatchlistTast(mContext).execute();
  }
}
