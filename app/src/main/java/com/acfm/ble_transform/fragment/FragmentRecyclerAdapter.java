package com.acfm.ble_transform.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.SafetyHatInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentRecyclerAdapter extends RecyclerView.Adapter<FragmentRecyclerAdapter.MyViewHolder> {

    SqliteDao sqliteDao;
    List<JSONObject> list = new ArrayList<>();

    private Integer id;
    private Context context;


    public FragmentRecyclerAdapter(Context context,Integer id){
        this.context = context;
        this.id = id;
        sqliteDao = new SqliteDao(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context,R.layout.recycler_item,null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        JSONObject jsonObject = null;
        holder.imageButton.setImageResource(R.drawable.safety_hat);

        final Integer positionId = id + position;
        //传mac及数据
        try {
            jsonObject = sqliteDao.findById(positionId);
            list.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject == null){
            holder.imageButton.setBackgroundColor(Color.parseColor("#848484"));

        }else{
            try {

                Long time = jsonObject.getLong("time");
                System.out.println(time);
                time = time/(1000*60);
                Long currentTime = System.currentTimeMillis();
                currentTime = currentTime/(1000*60);
                Date date = new Date();
                date.setTime(time);
                System.out.println(new SimpleDateFormat().format(date));
                System.out.println("时间：：：：：：：：：：：：：：：：："+time);
                if((currentTime - time) > 10){
                    holder.imageButton.setBackgroundColor(Color.parseColor("#DF013A"));
                }else{
                    holder.imageButton.setBackgroundColor(Color.parseColor("#4DB376"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> s  = new ArrayList<>();

                 /*if(positionId==64||positionId==128||positionId==192||positionId==256)
                {
                    if(list.size()<64)
                    {
                        Toast.makeText(context, "未初始化"+positionId, Toast.LENGTH_SHORT).show();
                    }
                    else if(list.get(63)==null)
                    {
                        Toast.makeText(context, "无数据"+positionId, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            Intent intent = new Intent(context, SafetyHatInfo.class);
                            JSONObject jsonObject2 = list.get(positionId%64 - 1);
                            intent.putExtra("temperature",jsonObject2.getString("temperature"));

                            intent.putExtra("high",list.get(positionId%64 - 1).getString("high"));
                            intent.putExtra("power",list.get(positionId%64 - 1).getString("power"));

                            intent.putExtra("time",list.get(positionId%64 - 1).getLong("time"));
                            intent.putExtra("status",list.get(positionId%64 - 1).getString("status"));

                            intent.putExtra("humidity",list.get(positionId%64 - 1).getString("humidity"));

                            intent.putExtra("Mac",list.get(positionId%64 - 1).getString("Mac"));
                            intent.putExtra("hatId",list.get(positionId%64 - 1).getString("hatId"));
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }*/
                if(((positionId-1)%64)>(list.size()-1))
                 {
                     Toast.makeText(context, "未初始化"+positionId, Toast.LENGTH_SHORT).show();
                 }
                    else if(list.get((positionId-1)%64) == null){
                    Toast.makeText(context, "无数据"+positionId, Toast.LENGTH_SHORT).show();
                }else{

                    /*
                    jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac",cursor.getString(cursor.getColumnIndex("MAC")));
            jsonObject.put("hatId",cursor.getString(cursor.getColumnIndex("hatId")));

                     */
                    try {
                        Intent intent = new Intent(context, SafetyHatInfo.class);
                        JSONObject jsonObject2 = list.get((positionId-1)%64);
                        intent.putExtra("temperature",jsonObject2.getString("temperature"));

                        intent.putExtra("high",jsonObject2.getString("high"));
                        intent.putExtra("power",jsonObject2.getString("power"));

                        intent.putExtra("time",jsonObject2.getLong("time"));
                        intent.putExtra("status",jsonObject2.getString("status") );

                        intent.putExtra("humidity",jsonObject2.getString("humidity"));

                        intent.putExtra("Mac",jsonObject2.getString("Mac"));
                        intent.putExtra("hatId",jsonObject2.getString("hatId"));
                       context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });

        holder.textView.setText("安全帽"+positionId);
        //设置mac
    }

    @Override
    public int getItemCount() {
        return 64;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageButton imageButton;
        private TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.ib_item);
            textView = itemView.findViewById(R.id.tv_item);

        }
    }

    public interface OnItemClickListener{
        public void OnItemClick(View view,Integer id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
