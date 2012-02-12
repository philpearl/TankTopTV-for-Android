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
  protected Drawable obtainImage(String id) throws IOException
  {
    // TODO: Cache to file system!
    return mHttpLayer.getImage(id);
  }
}
