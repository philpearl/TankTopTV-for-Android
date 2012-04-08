package tv.tanktop.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import tv.tanktop.TanktopContext;
import tv.tanktop.utils.HttpUrlCache;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HttpLayer
{
  private static final String TAG = "HttpLayer";

  public static final int CONNECT_TIMEOUT = 60000;
  public static final int READ_TIMEOUT = 60000;

  private final TanktopContext mContext;
  private final AndroidHttpClient mHttpClient;
  private final HttpContext mHttpContext;
  private final CookieStore mCookieStore;
  private final String mBaseUrl;

  private final JSONResponseHandler mResponseHandler = new JSONResponseHandler();

  public HttpLayer(TanktopContext context)
  {
    mContext = context;
    mBaseUrl = mContext.getBaseUrl();
    mHttpClient = mContext.getHttpClient();
    mHttpContext = new BasicHttpContext();
    mCookieStore = new BasicCookieStore(); // Doesn't persist
    mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
  }

  public void onDestroy()
  {
    mHttpClient.close();
  }

  /**
   * Log into the tanktop server
   * @param userName
   * @param password
   * @throws IOException
   */
  public void login(String userName, String password) throws IOException
  {
    HttpPost post = new HttpPost(mBaseUrl + "/api/v1/login");

    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(3);

    params.add(new BasicNameValuePair("user_name", userName));
    params.add(new BasicNameValuePair("password", password));
    params.add(new BasicNameValuePair("login", "Login"));

    post.setEntity(new UrlEncodedFormEntity(params));

    // No need to return a response as the session is set up in the cookie
    ResponseHandler<Void> handler = new ResponseHandler<Void>()
    {
      public Void handleResponse(HttpResponse response)
          throws ClientProtocolException, IOException
      {
        StatusLine statusLine = response.getStatusLine();
        Log.d(TAG, "Have response " + statusLine);
        if (statusLine.getStatusCode() > 299)
        {
          throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        return null;
      }
    };

    Log.i(TAG, "send post " + post.getURI());
    mHttpClient.execute(post, handler, mHttpContext);
  }

  public JSONObject getWatchList() throws ClientProtocolException, IOException
  {
    HttpGet get = new HttpGet(mBaseUrl + "/api/v1/watchlist");

    return mHttpClient.execute(get, mResponseHandler, mHttpContext);
  }

  public void markEpisodeSeen(long episodeId) throws ClientProtocolException, IOException
  {
    HttpPost post = new HttpPost(mBaseUrl + "/seen");

    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(3);

    params.add(new BasicNameValuePair("id", String.valueOf(episodeId)));
    params.add(new BasicNameValuePair("json", "true"));

    try
    {
      post.setEntity(new UrlEncodedFormEntity(params));
    }
    catch (UnsupportedEncodingException e)
    {
      // Nah, won't happen
    }

    mHttpClient.execute(post, mResponseHandler, mHttpContext);
  }

  public void removeFromWatchlist(long progId) throws ClientProtocolException, IOException
  {
    HttpPost post = new HttpPost(mBaseUrl + "/unfavourite");

    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(3);

    params.add(new BasicNameValuePair("id", String.valueOf(progId)));
    params.add(new BasicNameValuePair("json", "true"));

    try
    {
      post.setEntity(new UrlEncodedFormEntity(params));
    }
    catch (UnsupportedEncodingException e)
    {
      // Nah, won't happen
    }

    mHttpClient.execute(post, mResponseHandler, mHttpContext);
  }

  public Drawable getImage(String url) throws ClientProtocolException, IOException
  {
    Log.i(TAG, "get " + url);
    HttpGet get = new HttpGet(url);

    ResponseHandler<Drawable> responseHandler = new ResponseHandler<Drawable>()
    {
      public Drawable handleResponse(HttpResponse response)
          throws ClientProtocolException, IOException
      {
        StatusLine statusLine = response.getStatusLine();
        Log.d(TAG, "Have response " + statusLine);
        if (statusLine.getStatusCode() > 299)
        {
          throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return BitmapDrawable.createFromStream(response.getEntity().getContent(), "dummy.png");
      }
    };

    return mHttpClient.execute(get, responseHandler);
  }

  public Drawable getImageCached(final String url) throws ClientProtocolException, IOException
  {
    Log.i(TAG, "get " + url);

    File cacheDir = mContext.getExternalCacheDir();
    if (cacheDir == null)
    {
      cacheDir = mContext.getCacheDir();
    }

    final File cacheFile = new File(cacheDir, HttpUrlCache.UrlToCacheFileName(url));
    if (cacheFile.exists())
    {
      Log.d(TAG, "load from " + cacheFile.getAbsolutePath());
      return BitmapDrawable.createFromPath(cacheFile.getAbsolutePath());
    }

    HttpGet get = new HttpGet(url);

    ResponseHandler<Drawable> responseHandler = new ResponseHandler<Drawable>()
    {
      public Drawable handleResponse(HttpResponse response)
          throws ClientProtocolException, IOException
      {
        StatusLine statusLine = response.getStatusLine();
        Log.d(TAG, "Have response " + statusLine);
        if (statusLine.getStatusCode() > 299)
        {
          throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        OutputStream os = new FileOutputStream(cacheFile);
        try
        {
          response.getEntity().writeTo(os);
        }
        finally
        {
          os.close();
        }

        return BitmapDrawable.createFromPath(cacheFile.getAbsolutePath());
      }
    };

    return mHttpClient.execute(get, responseHandler);
  }

}
