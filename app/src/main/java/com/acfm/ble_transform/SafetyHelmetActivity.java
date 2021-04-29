package com.acfm.ble_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.acfm.ble_transform.fragment.Fragment1;
import com.acfm.ble_transform.fragment.Fragment2;
import com.acfm.ble_transform.fragment.Fragment3;
import com.acfm.ble_transform.fragment.Fragment4;

public class SafetyHelmetActivity extends Activity {

    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_helmet);
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        FragmentManager fm = getFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();

        radioGroup = findViewById(R.id.rg_home);

        radioButton1 = findViewById(R.id.rb1);
        radioButton2 = findViewById(R.id.rb2);
        radioButton3 = findViewById(R.id.rb3);
        radioButton4 = findViewById(R.id.rb4);
        transaction.add(R.id.fragment,fragment1,"a").commitAllowingStateLoss();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentManager fm = getFragmentManager();
                // 开启Fragment事务
                FragmentTransaction transaction = fm.beginTransaction();
                switch (i){
                    case R.id.rb1:
                        transaction.replace(R.id.fragment,fragment1);
                        break;
                    case R.id.rb2:
                        transaction.replace(R.id.fragment,fragment2);
                        break;
                    case R.id.rb3:
                        transaction.replace(R.id.fragment,fragment3);
                        break;
                    case R.id.rb4:
                        transaction.replace(R.id.fragment,fragment4);
                        break;
                }
                transaction.commit();
            }
        });

    }
}