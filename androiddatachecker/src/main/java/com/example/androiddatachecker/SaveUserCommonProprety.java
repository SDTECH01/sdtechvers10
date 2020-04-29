package com.example.androiddatachecker;

import android.Manifest;

import android.content.Context;
import android.content.ContextWrapper;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaveUserCommonProprety extends ActivityCompat implements LocationListener {


    Location location; // location
    private double longitude, latitude;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    protected String mprovider;
    ////Le context à utiliser
    private static ContextWrapper context;
    protected String uuid_user;
    //////////////////heure///////////////////
    SimpleDateFormat heuref = new SimpleDateFormat("HH:mm");
    String heureFormatter = heuref.format(new Date());
    ////////////////date/////////////////////
    SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy");
    String dateFormatter = datef.format(new Date());

    ////Le constructeur de la classe, il doit prendre la context puis le retourner
    public SaveUserCommonProprety(ContextWrapper context, String uuid_user) {
        this.context = context;
        this.uuid_user = uuid_user;
    }

    public boolean SaveUserCommonPropreties() {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, (LocationListener) this);


            if (location != null) {

                /*InsertData(uuid_user,getBatteryPercentage(),location.getLongitude(),location.getLatitude(),dateFormatter,heureFormatter,"liberty1","liberty2","liberty3",
                        "liberty4","liberty5","liberty6","liberty7",dateFormatter,"actif");*/
                //onLocationChanged(location);
                InsertData(uuid_user, getBatteryPercentage(), getLongitudeGps(location), getLatitudeGps(location), dateFormatter, heureFormatter, "liberty1", "liberty2", "liberty3",
                        "liberty4", "liberty5", "liberty6", "liberty7", dateFormatter, "actif");
            } else {
                try {
                    Thread.sleep(3000);
                    InsertData(uuid_user, getBatteryPercentage(), getLongitudeGps(location), getLatitudeGps(location), dateFormatter, heureFormatter, "liberty1", "liberty2", "liberty3",
                            "liberty4", "liberty5", "liberty6", "liberty7", dateFormatter, "actif");
                    //onLocationChanged(location);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
return true;
    }


    private void InsertData(final String id_user, final int level_battery, final double longitude, final double latitude, final String dat_ins_proprety,
                            final String heure_proprety, final String liberty1, final String liberty2,
                            final String liberty3, final String liberty4, final String liberty5, final String liberty6, final String liberty7,
                            final String last_update, final String etat) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                /*String NameHolder = name;
                String EmailHolder = email;*/

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_user", id_user));
                nameValuePairs.add(new BasicNameValuePair("level_battery", Integer.toString(level_battery)));
                nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
                nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
                nameValuePairs.add(new BasicNameValuePair("dat_ins_proprety", dat_ins_proprety));
                nameValuePairs.add(new BasicNameValuePair("heure_proprety", heure_proprety));
                nameValuePairs.add(new BasicNameValuePair("liberty1", liberty1));
                nameValuePairs.add(new BasicNameValuePair("liberty2", liberty2));
                nameValuePairs.add(new BasicNameValuePair("liberty3", liberty3));
                nameValuePairs.add(new BasicNameValuePair("liberty4", liberty4));
                nameValuePairs.add(new BasicNameValuePair("liberty5", liberty5));
                nameValuePairs.add(new BasicNameValuePair("liberty6", liberty6));
                nameValuePairs.add(new BasicNameValuePair("liberty7", liberty7));
                nameValuePairs.add(new BasicNameValuePair("last_update", last_update));
                nameValuePairs.add(new BasicNameValuePair("etat", etat));


                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveUserCommonPropret/");

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(id_user, Integer.toString(level_battery), Double.toString(longitude), Double.toString(latitude),
                dat_ins_proprety, heure_proprety, liberty1, liberty2, liberty3, liberty4, liberty5, liberty6, liberty7,
                last_update, etat);
    }

    public int getBatteryPercentage() {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }


    @Override
    public void onLocationChanged(Location location) {

        getLatitudeGps(location);
        getLongitudeGps(location);


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private double getLongitudeGps(Location location) {
        this.location = location;
        return location.getLongitude();
    }

    private double getLatitudeGps(Location location) {
        this.location = location;
        return location.getLatitude();
    }
}
