package com.acfm.ble_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Repeaterinfo extends AppCompatActivity {
    private Bundle repeater;
    private TextView re_peaterid1, re_temperate1,re_time1,re_high1,re_worktime1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeaterinfo);
        repeater=getIntent().getExtras();
        String repeaterid = repeater.getString("repeaterId");

        String temperate = repeater.getString("temperature");
        String worktime = repeater.getString("worktime");
        String high = repeater.getString("high");
        Long time = repeater.getLong("time");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));

        re_peaterid1 = (TextView)findViewById(R.id.repeaterId);
        re_temperate1= (TextView)findViewById(R.id.re_temperate);
        re_worktime1 = (TextView)findViewById(R.id.re_worktime);
        re_time1 = (TextView)findViewById(R.id.re_time);
        re_high1 = (TextView)findViewById(R.id.re_high);
        re_peaterid1.setText("中继器id:"+repeaterid);
        re_temperate1.setText(" 温度:"+temperate+"摄氏度");
        re_worktime1.setText(" 工作时间:"+worktime);
        re_time1.setText(" 接收时间:"+sd);
        re_high1.setText(" 相对高度:"+high);
    }
}