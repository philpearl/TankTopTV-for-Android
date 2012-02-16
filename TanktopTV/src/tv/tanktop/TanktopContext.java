package tv.tanktop;

import tv.tanktop.net.HttpLayer;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.http.AndroidHttpClient;

public class TanktopContext extends ContextWrapper
{
  private String mBaseUrl;
  private HttpLayer mHttpLayer;
  private TanktopStore mStore;

  public TanktopContext(Context base)
  {
    super(base);

    mBaseUrl = "http://192.168.1.16:8080";
  }

  public String getBaseUrl()
  {
    return mBaseUrl;
  }

  public AndroidHttpClient getHttpClient()
  {
    return AndroidHttpClient.newInstance("TankTopTV - android");
  }

  public synchronized HttpLayer getHttpLayer()
  {
    if (mHttpLayer == null)
    {
      mHttpLayer = new HttpLayer(this);
    }
    return mHttpLayer;
  }

  public HttpLayer newHttpLayer()
  {
    return new HttpLayer(this);
  }

  public synchronized TanktopStore getStore()
  {
    if (mStore == null)
    {
      mStore = new TanktopStore(this);
    }
    return mStore;
  }

}
