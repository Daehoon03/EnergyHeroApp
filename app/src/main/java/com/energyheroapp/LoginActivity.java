package com.energyheroapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.energyheroapp.helper.AuthHelper;
import com.energyheroapp.helper.HTTPRequestHelper;

import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 다ㅣ훈03 on 2014-08-18.
 */
public class LoginActivity extends Activity {
    protected ProgressDialog dialog;
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView btnSubmit = (TextView) findViewById(R.id.buttonSubmit);

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    final EditText userName = (EditText) findViewById(R.id.editTextUserName);
                    final EditText password = (EditText) findViewById(R.id.editTextPassword);

                    if (userName.getText().length() == 0) {
                        Toast.makeText(LoginActivity.this,
                                "ID를 입력해주세요.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else if (password.getText().length() == 0) {
                        Toast.makeText(LoginActivity.this,
                                "비밀번호를 입력해주세요.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        dialog = ProgressDialog.show(LoginActivity.this, "로그인",
                                "잠시만 기다려주세요", true, true);
                        hideKeyboard();
                        logIn();
                    }
                } catch (Exception e) {
                    Log.e("Login Acti", e.getMessage());
                }
            }
        });


    }

    private void logIn() {
        final ResponseHandler<String> responseHandler = HTTPRequestHelper
                .getResponseHandlerInstance(this.loginHandler);

        new Thread() {
            public void run() {
                HashMap<String, String> params = new HashMap<String, String>();
                final EditText userName = (EditText) findViewById(R.id.editTextUserName);
                final EditText password = (EditText) findViewById(R.id.editTextPassword);

                params.put("grant_type", "password");
                params.put("username", userName.getText().toString());
                params.put("password", password.getText().toString());

                HTTPRequestHelper helper = new HTTPRequestHelper(
                        responseHandler);
                helper.performPost(HTTPRequestHelper.MIME_FORM_ENCODED,
                        "http://" + AuthHelper.getDomainHostName() + "/Token",
                        null, null, null, params);

            }
        }.start();
    }

    private final Handler loginHandler = new Handler() {
        public void handleMessage(final Message msg) {
            String result = msg.getData().getString("RESPONSE");

            Log.i("loginHandler handleMessage", result);

            try {
                JSONObject o = new JSONObject(result);
                dialog.dismiss();
                if (o.has("access_token")) {
                    AuthHelper.setAccessToken(o.getString("access_token"));
                    AuthHelper.setUserName(o.getString("userName"));
                    AuthHelper.setTokenType(o.getString("token_type"));
                    AuthHelper.setWhenExpires(o.getString(".expires"));
                    Toast.makeText(LoginActivity.this,
                            "Welcome! " + o.getString("userName"),
                            Toast.LENGTH_SHORT).show();
                    // 사용자 사진 등 더 많은 데이터를 가져오도록 개선
                    AuthHelper.getUserPhotoAsync(LoginActivity.this);

                    Intent mIntent = new Intent(getBaseContext(),
                            HeroShotActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(mIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Sorry! Your information is incorrect.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // {"access_token":"---dUdscJ3Rp--",
            // "token_type":"bearer","expires_in":1209599,"userName":"edkim"
            // ,".issued":"Wed, 19 Feb 2014 07:02:39 GMT",".expires":"Wed, 05 Mar 2014 07:02:39 GMT"}

        }
    };


    protected void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
