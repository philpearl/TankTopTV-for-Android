package tv.tanktop.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONResponseHandler implements
                                ResponseHandler<JSONObject>
{
  private static final String TAG = "JSONResponseHandler";

  public JSONObject handleResponse(HttpResponse response)
      throws ClientProtocolException, IOException
  {
    StatusLine statusLine = response.getStatusLine();
    Log.d(TAG, "Have response " + statusLine);
    if (statusLine.getStatusCode() > 299)
    {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }

    try
    {
      return new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
    }
    catch (ParseException e)
    {
      Log.e(TAG, "Exception retrieving data", e);
      throw (IOException)(new IOException().initCause(e));
    }
    catch (JSONException e)
    {
      Log.e(TAG, "Exception retrieving data", e);
      throw (IOException)(new IOException().initCause(e));
    }
  }

}
