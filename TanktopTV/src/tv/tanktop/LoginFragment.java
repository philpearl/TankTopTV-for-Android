package tv.tanktop;

import tv.tanktop.service.SyncService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginFragment extends Fragment
{
  private static final String TAG = "LoginFragment";

  private EditText mUserName;
  private EditText mPassword;
  private Button mLoginButton;
  private ProgressBar mProgressBar;

  private LoginReceiver mLoginReceiver;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    setHasOptionsMenu(false);

    mLoginReceiver = new LoginReceiver(new Handler());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.login, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    Log.d(TAG, "onViewCreated");
    super.onViewCreated(view, savedInstanceState);
    mUserName = (EditText) view.findViewById(R.id.username);
    mPassword = (EditText) view.findViewById(R.id.password);
    mLoginButton = (Button) view.findViewById(R.id.loginButton);
    mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

    mProgressBar.setVisibility(View.INVISIBLE);

    mLoginButton.setOnClickListener(mLoginClickListener);
  }

  private final OnClickListener mLoginClickListener = new OnClickListener()
  {
    public void onClick(View v)
    {
      Log.d(TAG, "onClick");
      v.setEnabled(false);
      mUserName.setEnabled(false);
      mPassword.setEnabled(false);

      getActivity().startService(new Intent(getActivity(), SyncService.class)
      .setAction(SyncService.ACTION_LOGIN)
      .putExtra(SyncService.EXTRA_USERNAME, mUserName.getText().toString())
      .putExtra(SyncService.EXTRA_PASSWORD, mPassword.getText().toString())
      .putExtra(SyncService.EXTRA_RESULT_RECEIVER, mLoginReceiver)
      );

      mProgressBar.setVisibility(View.VISIBLE);
    }
  };

  class LoginReceiver extends ResultReceiver
  {
    public LoginReceiver(Handler handler)
    {
      super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
      Log.d(TAG, "onReceiveResult " + resultCode);
      // Clear progress
      mProgressBar.setVisibility(View.INVISIBLE);

      if (resultCode == SyncService.RESULT_OK)
      {
        ((TanktopTVActivity) getActivity()).onLoginSuccess();
      }
      else
      {
        // TODO: indicate failure to user
        mUserName.setEnabled(true);
        mPassword.setEnabled(true);
        mLoginButton.setEnabled(true);
      }
    }
  }
}
