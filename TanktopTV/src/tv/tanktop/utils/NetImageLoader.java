package tv.tanktop.utils;

import java.io.IOException;

import tv.tanktop.TanktopContext;
import tv.tanktop.net.HttpLayer;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;

public class NetImageLoader extends AbstractImageLoader<String>
{
  private static final String TAG = "NetImageLoader";
  private final TanktopContext mContext;
  private final HttpLayer mHttpLayer;

  public NetImageLoader(TanktopContext context,
      Handler uiHandler)
  {
    this(context, new HandlerThread(TAG), uiHandler);
  }

  public NetImageLoader(TanktopContext context, HandlerThread handlerThread,
      Handler uiHandler)
  {
    super(context, handlerThread, uiHandler);
    mContext = context;
    mHttpLayer = mContext.newHttpLayer();
  }

  @Override
  public void onDestroy()
  {
    mHttpLayer.onDestroy();
    super.onDestroy();
  }

  @Override
  protected Drawable obtainImage(String id) throws IOException
  {
    return mHttpLayer.getImageCached(id);
  }
}
