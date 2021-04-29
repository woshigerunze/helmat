package com.acfm.ble_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.fragment.Fragment1;
import com.acfm.ble_transform.fragment.Fragment2;
import com.acfm.ble_transform.fragment.Fragment3;
import com.acfm.ble_transform.fragment.Fragment4;
import com.acfm.ble_transform.reprater_fra.re_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RepeaterActivity extends Activity {
    private re_Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_repeater);
            fragment = new re_Fragment();
            FragmentManager fm = getFragmentManager();
            final FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.repeater_fragment,fragment,"a").commitAllowingStateLoss();
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                    FragmentManager fm = getFragmentManager();
//                    // 开启Fragment事务
//                    FragmentTransaction transaction = fm.beginTransaction();
//
//                    transaction.commit();
//                }
//            });

        }


    }
