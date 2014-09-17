package com.energyheroapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.energyheroapp.R;
import com.energyheroapp.helper.AuthHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView btn_upload = (TextView) findViewById(R.id.buttonSubmit);

        btn_upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                uploadTest();
            }
        });
    }

    public void uploadTest() {
        final EditText userName = (EditText) findViewById(R.id.TestUser);
        try
        {

            HttpClient client = new DefaultHttpClient();

            HashMap<String, String> headers = new HashMap<String, String>();
            AuthHelper.getAuthorizationHeader(headers);

            String postURL = "http://" + AuthHelper.getDomainHostName() + "/Mobile/UploadTest";
            HttpPost post = new HttpPost(postURL);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            FormBodyPart bodyPart=new FormBodyPart("TestText", new StringBody(userName.getText().toString()));
            reqEntity.addPart(bodyPart);
            post.setEntity(reqEntity);

            post.addHeader("Authorization", headers.get("Authorization"));

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();

            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(resEntity.getContent(), "utf-8")
            );
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufreader.readLine()) != null) {
                sb.append(line + "\n");
            }
            String json = sb.toString();
            JSONObject jObj = new JSONObject(json);
            /*String json = bufreader.readLine();*/
            String photoid = jObj.getString("PhotoId");
            String msg = jObj.getString("Msg");
            String photourl = jObj.getString("PhotoUrl");
            if (resEntity != null)
            {
                Log.i("RESPONSE", photoid + photourl + msg);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
