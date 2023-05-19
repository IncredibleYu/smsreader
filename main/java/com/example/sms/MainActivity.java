package com.example.sms;

import static android.Manifest.permission.READ_SMS;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter sa;
    private List<Map<String, Object>> data;
    public static final int REQ_CODE_CONTACT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        readSMS();
    }

    private void initView() {
        // Get ListView
        mListView = (ListView) findViewById(R.id.listView);
        data = new ArrayList<Map<String, Object>>();
        //Set Adapter
        sa = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"names", "message"}, new int[]{android.R.id.text1,
                android.R.id.text2});
        mListView.setAdapter(sa);
    }

    /**
     * check permission
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //Haven't gotten permission yet

            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_SMS}, REQ_CODE_CONTACT);
        } else {
            query();
        }
    }

    /**
     * Click to read SMS

     */
    public void readSMS() {
        checkSMSPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Determine whether the user has, or has not, agreed to obtain SMS authorization
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_CONTACT && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Get permission to read SMS messages
            query();
        } else {
            Toast.makeText(this, "SMS permission not obtained", Toast.LENGTH_SHORT).show();
        }
    }

    private void query() {

        //Read all SMS messages
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "body", "date", "type"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int _id;
            String address;
            String body;
            String date;
            int type;
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                _id = cursor.getInt(0);
                address = cursor.getString(1);
                body = cursor.getString(2);
                date = cursor.getString(3);
                long timestamp = Long.parseLong(date);
                Date curDate = new Date(timestamp);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format(curDate);
                type = cursor.getInt(4);
                map.put("names", "Address: " + address + "\nDate: " + formattedDate + "\nBody: " + body);

                Log.i("test", "_id=" + _id + " address=" + address + " body=" + body + " date=" + formattedDate + " type=" + type);
                data.add(map);
                //Notify the adapter of changes
                sa.notifyDataSetChanged();
            }
        }
    }
}

