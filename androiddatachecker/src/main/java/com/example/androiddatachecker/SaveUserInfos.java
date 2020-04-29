package com.example.androiddatachecker;

import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class SaveUserInfos extends AppCompatActivity {

    protected static ContextWrapper context;

    protected String uuid_user;

    public SaveUserInfos(ContextWrapper context, String uuid_user) {
        this.context = context;
        this.uuid_user = uuid_user;
    }

    public boolean getStarter() {

        SaveUserCheckData saveUserCheckData = new SaveUserCheckData(context,uuid_user);
        try {
            saveUserCheckData.SaveUserCheckDatas();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}

