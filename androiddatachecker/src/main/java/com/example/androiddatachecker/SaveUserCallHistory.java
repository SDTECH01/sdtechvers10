package com.example.androiddatachecker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


public class SaveUserCallHistory extends AppCompatActivity {


    private static String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};

    private static ContextWrapper context;
    protected String uuid_user;


    ///////////////////le context de l'application///////////////////
    public SaveUserCallHistory(ContextWrapper context,String uuid_user) {
        this.context = context;
        this.uuid_user = uuid_user;
    }


    protected void SaveUserCallHistories () {
        JSONArray jsonArray = new JSONArray();
        int i=0;
        if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_CALL_LOG}, 10);
        } else {

            //if (requiredPermissions=="Manifest.permission.READ_CALL_LOG" &&requiredPermissions=="android.permission.READ_CONTACTS"){
            // String where = CallLog.Calls.DATE+">="+Where();
            //String where = CallLog.Calls.DATE+">="+Where()+" AND '15214875954124'";
            String where = "date >="+Where()+" AND date > '"+lastInsert()+"'";
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, where, null, null);
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int datAp = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);


            while (cursor.moveToNext()) {

                Date date = new Date();
                String formatted = new SimpleDateFormat("dd/MM/yyyy").format(date);

                Date dat = new Date(cursor.getLong(datAp));
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(dat);
                Date heur = new Date(cursor.getLong(datAp));
                String formattedHeure = new SimpleDateFormat("HH:mm").format(heur);
                //jArray.put(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                try {
                    //jsonObject.put()
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id_user",cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    jsonObject.put("id_call",cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    jsonObject.put("call_duration",cursor.getString(duration));
                    jsonObject.put("correspondant_number",cursor.getString(number));
                    jsonObject.put("correspondant_name",findNameByNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))));
                    jsonObject.put("type_call",AppelType(cursor.getString(type)));
                    jsonObject.put("call_dat",formattedDate);
                    jsonObject.put("call_heure",formattedHeure);
                    jsonObject.put("dat_ins_call_history",formatted);
                    jsonObject.put("acitf","acitf");
                    jsonArray.put(i,jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                i +=1;
                /*InsertData(uuid_user,
                        cursor.getColumnIndex(CallLog.Calls.NUMBER),
                        cursor.getString(duration),
                        cursor.getString(number),
                        findNameByNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))),
                        AppelType(cursor.getString(type)),
                        formattedDate,
                        formattedHeure,
                        formatted,
                        "acitf");*/
            }

            cursor.close();


        }

        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        EnvoiJson(uuid_user, String.valueOf(jsonArray),"callHistory");

        SaveUserMessage saveUserMessage = new SaveUserMessage(context,uuid_user);
        saveUserMessage.SaveUserMessages();
    }

    private String AppelType(String type) {
        String typAppel =null;
        switch (type) {
            case  "1":
                typAppel= "Incoming";
                break;
            case  "2":
                typAppel= "Outgoing";
                break;
            case  "3":
                typAppel= "Mixed";
                break;
            case  "4":
                typAppel= "Voice";
                break;
            case  "5":
                typAppel= "Rejected";
                break;
            case  "6":
                typAppel= "Blocked";
                break;
            case  "7":
                typAppel= "Extra";
            default:
                typAppel= "Unknown";
                break;
        }
        return typAppel;
    }

    public int getOutgoingDuration () {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {

            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE, null, null);

            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String callDuration = cursor.getString(duration);
                sum += Integer.parseInt(callDuration);
            }

            cursor.close();
        }
        return sum;
    }

    public int getIncomingDuration() {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {

            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE, null, null);

            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String callDuration = cursor.getString(duration);
                sum += Integer.parseInt(callDuration);
            }

            cursor.close();

        }
        return sum;
    }

    public int getTotalDuration () {
        int sum = 0;
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {


            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String callDuration = cursor.getString(duration);
                sum += Integer.parseInt(callDuration);
            }

            cursor.close();
        }
        return sum;
    }

    protected void InsertData ( final String id_user, final int id_call, final String call_duration,
                                final String correspondant_number, final String correspondant_name,
                                final String type_call, final String call_dat,
                                final String call_heure, final String dat_ins_call_history, final String etat){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_user", id_user));
                nameValuePairs.add(new BasicNameValuePair("id_call", Integer.toString(id_call)));
                nameValuePairs.add(new BasicNameValuePair("call_duration", call_duration));
                nameValuePairs.add(new BasicNameValuePair("correspondant_number", correspondant_number));
                nameValuePairs.add(new BasicNameValuePair("correspondant_name", correspondant_name));
                nameValuePairs.add(new BasicNameValuePair("type_call", type_call));
                nameValuePairs.add(new BasicNameValuePair("call_dat", call_dat));
                nameValuePairs.add(new BasicNameValuePair("call_heure", call_heure));
                nameValuePairs.add(new BasicNameValuePair("dat_ins_call_history", dat_ins_call_history));
                nameValuePairs.add(new BasicNameValuePair("etat", etat));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveUserCallHistor/");
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

        sendPostReqAsyncTask.execute(id_user, Integer.toString(id_call), call_duration, correspondant_number,
                correspondant_name, type_call, call_dat,
                call_heure, dat_ins_call_history,
                etat);
    }

    private String findNameByNumber(final String phoneNumber){
        ContentResolver cr = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null);
        if (cursor == null) {
            return null;
        }

        String contactName = null;

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return (contactName == null) ? phoneNumber : contactName;
    }

    protected long Where(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        Date result = cal.getTime();
        String formatString= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(result);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date formatTodate = null;
        try {
            formatTodate = sdf.parse(formatString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = formatTodate.getTime();
        return millis;
    }

    /***************************************************************/
    public void EnvoiJson(final String userNumber,final String contenu, final String type){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_user", userNumber));
                nameValuePairs.add(new BasicNameValuePair("contenu", contenu));
                nameValuePairs.add(new BasicNameValuePair("type", type));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveJson/");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //HttpResponse httpResponse = httpClient.execute(httpPost);
                    httpClient.execute(httpPost);

                    //HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {
                //super.onPostExecute(result);
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(userNumber, contenu, type);
    }
    /************************************************************/
    private long currentDate(){
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);
        long todayMillis = cal.getTimeInMillis();
        return todayMillis;
    }

    /******************************obtain last CallLog registered line************************************/
    class getLastRegisteredDate extends AsyncTask<String, Void, String> {
        String resultid = "";
        InputStream isrid = null;
        @Override
        protected String doInBackground(String... params) {

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://smart-data-tech.com/dev/API/v1/getLastIdCall/index.php?phone="+uuid_user); //YOUR PHP SCRIPT ADDRESS
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();

                isrid = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());

            }


            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(isrid, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                isrid.close();

                resultid = sb.toString().trim();

            } catch (Exception e) {
                Log.e("log_tag", "Error  converting result " + e.toString());
            }
            return resultid;
        }

        @Override
        protected void onPostExecute(String results) {


        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    /*****************************************end last callLog registered search***************************/
    private long lastInsert(){
        /*****getting last registered line in database*****/

        AsyncTask getLastIdcall = new getLastRegisteredDate().execute();
        String getlast =null;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long milliseconds=0;

        try {
            getlast = getLastIdcall.get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("votre warning est ","ici "+getlast+" valeur "+getlast.trim().equals("0")+" et " + (getlast=="0"));

        try {
            // if (getlast.toString().equals(0))
            if (getlast.trim().equals("0"))
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -3);
                Date result = cal.getTime();
                String formatString= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(result);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date formatTodate = null;
                try {
                    formatTodate = sdf.parse(formatString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                milliseconds = formatTodate.getTime();
                //return millis;

               /* Date result = new Date();
                String formatString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(result);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date formatTodate = null;
                try {
                    formatTodate = sdf.parse(formatString);
                    milliseconds = formatTodate.getTime();
                    Log.i("votre temps est ","ici "+milliseconds+" "+formatTodate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }else {
                Date d = f.parse(getlast);
                milliseconds = d.getTime();
                Log.i("votre temps est ","ici "+milliseconds+" "+d);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
}


