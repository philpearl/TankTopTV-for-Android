package tv.tanktop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class WLEpisodeActivity extends FragmentActivity implements TTContextHolder
{
  private TanktopContext mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    mContext = new TanktopContext(this);

    WLEpisodeFragment fragment = new WLEpisodeFragment();
    fragment.setArguments(getIntent().getExtras());

    getSupportFragmentManager()
    .beginTransaction()
    .add(android.R.id.content, fragment)
    .commit();
  }

  public TanktopContext getContext()
  {
    return mContext;
  }

}
