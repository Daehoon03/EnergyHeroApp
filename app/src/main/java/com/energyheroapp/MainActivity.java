package com.energyheroapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.energyheroapp.adapter.PhotoListAdapter;
import com.energyheroapp.helper.AuthHelper;
import com.energyheroapp.helper.NetHelper;
import com.energyheroapp.model.PhotoDataList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {
    private Context mContext;
    private ArrayList<PhotoDataList> photoDataList = new ArrayList<PhotoDataList>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mContext = this;

        GridView gv = (GridView) findViewById(R.id.GridView);
        photoDataList = getPhotoList();
        gv.setAdapter(new PhotoListAdapter(mContext, photoDataList));

        Button btnPicture = (Button) findViewById(R.id.pictureButton);

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (AuthHelper.getAccessToken() == null) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, HeroShotActivity.class);
                    startActivity(intent);
                }

                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 임시로 사용할 파일의 경로를 생성
                String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, TAKE_CAMERA);*/
            }
        });
    }

    private ArrayList<PhotoDataList> getPhotoList() {
        final ArrayList<PhotoDataList> photoDataList = new ArrayList<PhotoDataList>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String r = NetHelper.SendRESTRequest(getBaseContext(), String.format(
                        "http://"
                                + AuthHelper.getDomainHostName()
                                + "/Mobile/ListHeroShot"
                ), true);
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(r);
                    Log.i(MainActivity.class.getName(), "Number of entries "
                            + jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PhotoDataList pd = SetPhotoData(jsonObject);
                        photoDataList.add(pd);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return photoDataList;
    }
    private PhotoDataList SetPhotoData(JSONObject jsonObject)
            throws JSONException {
        PhotoDataList pd = new PhotoDataList();
        pd.PhotoId = jsonObject.getString("HeroShotId");
        pd.PhotoUrl = jsonObject.getString("PhotoUrl");

        return pd;
    }

    private void parsePhotoDataList(String result)
    {


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
