package com.example.androiddatachecker;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

public class SaveUserPhoneNumber extends AppCompatActivity {

    private static ContextWrapper context;
    private int numberContact=0;
    protected String uuid_user;

    /* les permissions*/
    int PERMISSION_ALL = 1;

    final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    ////Le constructeur de la classe, il doit prendre la context puis le retourner
    public SaveUserPhoneNumber(ContextWrapper context,String uuid_user){
        this.context = context;
        this.uuid_user = uuid_user;
    }
    // GPSTrackers local = new GPSTrackers(context);

    public void SaveUserPhoneNumbers() {
        checkPermission(PERMISSIONS.toString());
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        SimpleDateFormat heuref = new SimpleDateFormat("HH:mm");
        String heureFormatter = heuref.format(new Date());

        ////////////////date/////////////////////
        SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatter = datef.format(new Date());

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {

                        numberContact += 1;
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        /*InsertData(uuid_user,id,phoneNo,name,dateFormatter,heureFormatter,"type","groupe",
                                "local","actif","date user phone");*/

                    }

                    pCur.close();
                }
            }
            InsertData(uuid_user, "0", Integer.toString(numberContact), "nom", dateFormatter, heureFormatter, "type", "groupe",
                    "local", "actif", "date user phone");
        }
        if (cur != null) {
            cur.close();
        }
        SaveUserCallHistory saveUserCallHistory = new SaveUserCallHistory(context, uuid_user);
        saveUserCallHistory.SaveUserCallHistories();

        /*}else {
            Log.w("Permission denied","check it");
            }*/
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

    private void InsertData ( final String id_user, final String id_phone_number, final String phone_number,
                              final String number_name, final String dat_ins_number,
                              final String img_number,final String type_number,final String groupe_number,
                              final String local_number,final String etat_user_phone_number, final String dat_user_phone_number){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_user",id_user));
                nameValuePairs.add(new BasicNameValuePair("id_phone_number",id_phone_number));
                nameValuePairs.add(new BasicNameValuePair("phone_number", phone_number));
                nameValuePairs.add(new BasicNameValuePair("number_name", number_name));
                nameValuePairs.add(new BasicNameValuePair("dat_ins_number", dat_ins_number));
                nameValuePairs.add(new BasicNameValuePair("img_number", img_number));
                nameValuePairs.add(new BasicNameValuePair("type_number", type_number));
                nameValuePairs.add(new BasicNameValuePair("groupe_number", groupe_number));
                nameValuePairs.add(new BasicNameValuePair("local_number", local_number));
                nameValuePairs.add(new BasicNameValuePair("etat_user_phone_number", etat_user_phone_number));
                nameValuePairs.add(new BasicNameValuePair("dat_user_phone_number", dat_user_phone_number));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost("http://smart-data-tech.com/dev/API/v1/saveUserPhoneNumber/");
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

        sendPostReqAsyncTask.execute(id_user, id_phone_number, phone_number, number_name,
                dat_ins_number, img_number, type_number,groupe_number,local_number,etat_user_phone_number,dat_user_phone_number);

    }

}