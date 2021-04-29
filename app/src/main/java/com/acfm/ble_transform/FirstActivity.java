package com.acfm.ble_transform;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FirstActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);


        //TextView buletext =(TextView)findViewById(R.id.textbule);
        ImageView first =(ImageView)findViewById(R.id.first) ;
        ImageView second =(ImageView)findViewById(R.id.second) ;
        ImageView third =(ImageView)findViewById(R.id.thrid) ;
//        buletext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, ZigBeeActivity.class);
                startActivity(intent);
            }
        });
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, RepeaterActivity.class);
                startActivity(intent);
            }
        });
        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstActivity.this, SafetyHelmetActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
