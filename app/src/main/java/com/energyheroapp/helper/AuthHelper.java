package com.energyheroapp.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AuthHelper {

    private static String accessToken;
    private static String userName;
    private static String tokenType;
    private static long tokenTime;
    private static String domainHostName =  "www.energyhero.kr";
    private static Bitmap userPic;
    private static DateTime whenExpires;

    public static Bitmap getUserPic() {
        return userPic;
    }

    private static void setUserPic(Bitmap userPic) {
        AuthHelper.userPic = userPic;
    }

    public static String getAccessToken() {

        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getUserName() {

        return userName;
    }

    public static void setWhenExpires(String when) {
        String space = " ";
        String format1 = "yyyy-MM-dd", format2 = "M/d/yyyy", format3 = "EEE, dd MMM yyyy";
        DateTimeFormatter df = DateTimeFormat
                .forPattern(format1);
        try {
            whenExpires = df.parseDateTime(when.split(space)[0]);
        } catch (Exception e) {
            try {
                df = DateTimeFormat
                        .forPattern(format2);
                whenExpires = df.parseDateTime(when.split(space)[0]);
            } catch (Exception e2) {
                //java.lang.IllegalArgumentException: Invalid format: "Sat, 26 Jul 2014 13:53:53 GMT"

                String y = DateTime.now().getYear() + "";
                String p = when.split(y)[0].trim() + space + y;

                Date date = null;
                try {
                    date = new SimpleDateFormat(format3, Locale.ENGLISH).parse(p);
                } catch (ParseException e1) {
                    Log.e("SimpleDateFormat", e1.getMessage());
                }
                whenExpires = new DateTime(date.getTime());
            }
        }

        try {
            NetHelper.SendRESTRequest(null, "http://" + AuthHelper.getDomainHostName()
                    + "/Crawl/WriteValue/"
                    + getUserName() + "::" + when);
        } catch (Exception e) {
        }

        //7/25/2014 2:35:21 PM +00:00
    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static String getTokenType() {

        return tokenType;
    }

    public static void getAuthorizationHeader(HashMap<String, String> headers) {
        checkExpires();
        if (tokenType != null && accessToken != null)
            headers.put("Authorization", tokenType + " " + accessToken);
    }

    private static void checkExpires() {
        DateTime dt = DateTime.now();
        if (dt.plusDays(1).isAfter(whenExpires)) {
            tokenType = null;
            accessToken = null;
        }
    }

    public static void getAuthorizationHeader(HttpPost httpPost) {
        checkExpires();
        if (tokenType != null && accessToken != null)
            httpPost.addHeader("Authorization", tokenType + " " + accessToken);
    }

    public static void getAuthorizationHeader(HttpGet httpGet) {
        checkExpires();
        if (tokenType != null && accessToken != null)
            httpGet.addHeader("Authorization", tokenType + " " + accessToken);
    }

    public static void setTokenType(String string) {
        tokenType = string;
    }

    public static String getDomainHostName() {

        return domainHostName;
    }

    public static void setTokenTime(long parseLong) {
        tokenTime = parseLong;
    }

    public static long getTokenTime() {
        return tokenTime;
    }

/*
    public static ArrayList<NotificationResult> getListNoti() {
        return listNoti;
    }

    public static void setListNoti(ArrayList<NotificationResult> listNoti) {
        AuthHelper.listNoti = listNoti;
    }

    public static void clearListNoti() {
        AuthHelper.listNoti.clear();
    }

    public static void addNoti(NotificationResult n) {
        AuthHelper.listNoti.add(n);
    }

    public static int getUnreadNotiCount() {
        int i = 0;

        for (NotificationResult n : listNoti) {
            if (!n.IsRead)
                i++;
        }

        return i;
    }
*/

    public static void getUserPhotoAsync(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String val = "-";

                try {
                    val = NetHelper.GetPhotoUrl();
                } catch (Exception e) {
                    //http://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare
                    /*Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();*/
                }

                return val;
            }

            @Override
            protected void onPostExecute(String msg) {
                try {
                    if (msg.length() > 0) {
                        String url = NetHelper.getSafeImageUrl(msg);
                        Bitmap bitmap = BitmapFactory
                                .decodeStream((InputStream) new URL(url)
                                        .getContent());
                        AuthHelper.setUserPic(bitmap);
                    }
                } catch (Exception e) {
                    /*Caused by: java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()*/
                    //http://stackoverflow.com/questions/3875184/cant-create-handler-inside-thread-that-has-not-called-looper-prepare
                    /*Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();*/
                }
            }
        }.execute(null, null, null);

    }
}
