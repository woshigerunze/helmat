package com.acfm.ble_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SafetyHatInfo extends AppCompatActivity {
        private Bundle hat;
    private TextView hatid1, mac1,status1,temperate1,humidity1,power1,time1,high1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_hat_info);
        hat=getIntent().getExtras();
        String hatid = hat.getString("hatId");
        String mac = hat.getString("Mac");
        String status = hat.getString("status");
        String temperate = hat.getString("temperature");
        String humidity = hat.getString("humidity");
        String power = hat.getString("power");
        Long time = hat.getLong("time");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        //对时间转换
        long totalMilliSeconds = System.currentTimeMillis();
        long totalSeconds = totalMilliSeconds / 1000;
        //求出现在的秒
         long currentSecond = totalSeconds % 60;

        //求出现在的分
        long totalMinutes = totalSeconds / 60;
        long currentMinute = totalMinutes % 60;
        //求出现在的小时
            long totalHour = totalMinutes / 60;
            long currentHour = totalHour % 24;
            String currenttime= currentHour +":"+currentMinute+":"+currentSecond;




 System.out.println("总毫秒为： " + totalMilliSeconds);

        String high = hat.getString("high");
        hatid1 = (TextView)findViewById(R.id.hat_id);
        mac1 = (TextView)findViewById(R.id.hat_mac);
        status1 = (TextView)findViewById(R.id.status);
        temperate1= (TextView)findViewById(R.id.temperate);
        humidity1 = (TextView)findViewById(R.id.humidity);
        power1 = (TextView)findViewById(R.id.power);
        time1 = (TextView)findViewById(R.id.time);
        high1 = (TextView)findViewById(R.id.high);
        hatid1.setText("安全帽id:"+hatid);
        mac1.setText(" MAC地址:"+mac);
        status1.setText(" 状态:"+status);
        temperate1.setText(" 温度:"+temperate+"摄氏度");
        humidity1.setText(" 湿度:"+humidity);
        power1.setText(" 电量:"+power);
        time1.setText(" 接收时间:"+sd);
        high1.setText(" 相对高度:"+high);

    }
}