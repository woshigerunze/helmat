package com.acfm.ble_transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.acfm.ble_transform.SQLiteUtil.SQLiteHelper;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 特别说明：HC_BLE助手是广州汇承信息科技有限公司独自研发的手机APP，方便用户调试08蓝牙模块。
 * 本软件只能支持安卓版本4.3并且有蓝牙4.0的手机使用。
 * 另外对于自家的05、06模块，要使用另外一套蓝牙2.0的手机APP，用户可以在汇承官方网的下载中心自行下载。
 * 本软件提供代码和注释，免费给购买汇承08模块的用户学习和研究，但不能用于商业开发，最终解析权在广州汇承信息科技有限公司。
 * **/

/**
 * @Description:  TODO<Ble_Activity实现连接BLE,发送和接受BLE的数据>
 */
public class Ble_Activity extends Activity implements OnClickListener {

	private final static String TAG = Ble_Activity.class.getSimpleName();

	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";;
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";

	SqliteDao sqliteDao =new SqliteDao(Ble_Activity.this);
	//蓝牙连接状态
	private boolean mConnected = false;
	private String status = "disconnected";
	//蓝牙名字
	private String mDeviceName;
	//蓝牙地址
	private String mDeviceAddress;
	//蓝牙信号值
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	//蓝牙service,负责后台的蓝牙服务
	private static BluetoothLeService mBluetoothLeService;
	//文本框，显示接受的内容
	private TextView rev_tv, connect_state;
	//发送按钮
	private Button send_btn;
	//文本编辑框
	private EditText send_et;
	private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//蓝牙特征值
	public SQLiteHelper database =new SQLiteHelper(Ble_Activity.this);
	public SQLiteDatabase db;
	public int Message_count=0;
	public String Message_cache;

	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler()
	{
		// 2.重写消息处理函数
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 判断发送的消息
				case 1:
				{
					// 更新View
					String state = msg.getData().getString("connect_state");
					connect_state.setText(state);

					break;
				}

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db=database.getWritableDatabase();
		setContentView(R.layout.first_layout);

		ImageView first =(ImageView)findViewById(R.id.first) ;
		ImageView second =(ImageView)findViewById(R.id.second) ;
		ImageView third =(ImageView)findViewById(R.id.thrid) ;

		first.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Ble_Activity.this, ZigBeeActivity.class);
				startActivity(intent);
			}
		});
		second.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Ble_Activity.this, RepeaterActivity.class);
				startActivity(intent);
			}
		});
		third.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Ble_Activity.this, SafetyHelmetActivity.class);
				startActivity(intent);
			}
		});


		b = getIntent().getExtras();
		//从意图获取显示的蓝牙信息
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);

		/* 启动蓝牙service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		gattServiceIntent.putExtra(Ble_Activity.EXTRAS_DEVICE_ADDRESS,mDeviceAddress);
		//startService(gattServiceIntent);
		bindService(gattServiceIntent,mServiceConnection,Context.BIND_AUTO_CREATE);
		init();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//解除广播接收器
     unregisterReceiver(mGattUpdateReceiver);
		unbindService(mServiceConnection);
//		mBluetoothLeService = null;
	}

	// Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
	@Override
	protected void onResume()
	{
		super.onResume();
		//绑定广播接收器
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{
			//根据蓝牙地址，建立连接
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	/**
	 * @Title: init
	 * @Description: TODO(初始化UI控件)
	 * @param
	 * @return void
	 * @throws
	 */
	private void init()
	{
		rev_sv = (ScrollView) this.findViewById(R.id.rev_sv);
		rev_tv = (TextView) this.findViewById(R.id.rev_tv);
		connect_state = (TextView) this.findViewById(R.id.connect_state);
		send_btn = (Button) this.findViewById(R.id.send_btn);
		send_et = (EditText) this.findViewById(R.id.send_et);
		connect_state.setText(status);
		//send_btn.setOnClickListener(this);

	}

	/* BluetoothLeService绑定的回调函数 */
	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName componentName,
									   IBinder service)
		{
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize())
			{
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// 根据蓝牙地址，连接设备
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
		}

	};

	/**
	 * 广播接收器，负责接收BluetoothLeService类发送的数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
			{
				mConnected = true;
				status = "connected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
					.equals(action))
			{
				mConnected = false;
				status = "disconnected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
					.equals(action))
			{
				// Show all the supported services and characteristics on the
				// user interface.
				//获取设备的所有蓝牙服务
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
			{
				//处理发送过来的数据
				displayData(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
				System.out.println("BroadcastReceiver onData:"
						+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};

	/* 更新连接状态 */
	private void updateConnectionState(String status)
	{
		Message msg = new Message();
		msg.what = 1;
		Bundle b = new Bundle();
		b.putString("connect_state", status);
		msg.setData(b);
		//将连接状态更新的UI的textview上
		myHandler.sendMessage(msg);
		System.out.println("connect_state:" + status);

	}

	/* 意图过滤器 */
	private static IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * @Title: displayData
	 * @Description: TODO(接收到的数据在scrollview上显示)
	 */
	private synchronized void displayData(String rev_string)
	{
		if(rev_string.length() < 4)
		{
			Message_cache += rev_string;
			return;
		}
		else if(rev_string.charAt(2) == '#'&&rev_string.charAt(3) == '#'){


				if(Message_cache == null){
					Message_cache = rev_string;
					return;
				}
				StringBuilder stringBuilder = new StringBuilder(Message_cache);
				ArrayList<String> strings	= Utils.splitDataframe(stringBuilder);//先把多个数据帧分开
				for(int i=1;i<strings.size();i++)
				{
					System.out.println(strings.get(1).length());
					System.out.println("rev:"+i + strings.get(i));
				}

				for(int i=1;i<strings.size();i++)
				{
					StringBuilder temp=new StringBuilder(strings.get(i));
					ArrayList<String> res=Utils.analysisDataframe(temp);//把某个数据帧分割
					char okOrErr = res.get(6).charAt(4);
					if (okOrErr == 'E'){
						continue;
					}

					JSONObject jsonObject = null;
					try {
						jsonObject = Utils.parse(res,sqliteDao);
					} catch (JSONException e) {
						e.printStackTrace();
					}

						/*ArrayList<String> after_res=Utils.dataframeToSQLite(res);//把分割得到的type和payload返回
						ContentValues values = new ContentValues();
						values.put("messageType",after_res.get(0));
						values.put("payLoad",after_res.get(1));
						db.insert("messageTypeAndpayLoad",null,values);

						 */
				}
//				for(int i=1;i<strings.size();i++) {
//					ContentValues values = new ContentValues();
//					values.put("MacAddress", mDeviceAddress);
//					values.put("Content", strings.get(i));
//					db.insert("BT_information", null, values);
//				}
				//Message_count=0;
				//Message_cache=null;
			Message_cache = rev_string;
			}
		else{
			Message_cache+=rev_string;
		}
		/*Message_count++;

		rev_str += rev_string;

		 */
		//Message_cache = "##RX-CH(1)-CNT(178) 2A01-(04)-06C31C0000340D00000000008B-CRC(OK)-PktRssi(-76.5388)-RSSI(-111.0)-TICK(1601076)";

		//runOnUiThread(new Runnable()
		//{
			//@Override
			//public void run()
			//{

				//rev_tv.setText(rev_str);
				//rev_sv.scrollTo(0, rev_tv.getMeasuredHeight());

			//}
		//});
	}






	/**
	 * @Title: displayGattServices
	 * @Description: TODO(处理蓝牙服务)
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices)
	{

		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown_service";
		String unknownCharaString = "unknown_characteristic";

		// 服务数据,可扩展下拉列表的第一级数据
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// 特征数据（隶属于某一级服务下面的特征值集合）
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// 部分层次，所有特征值集合
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices)
		{

			// 获取服务列表
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();

			// 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

			gattServiceData.add(currentServiceData);

			System.out.println("Service uuid:" + uuid);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

			// 从当前循环所指向的服务中读取特征值列表
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			// 对于当前循环所指向的服务中的每一个特征值
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
			{
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT))
				{
					// 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							mBluetoothLeService
									.readCharacteristic(gattCharacteristic);
						}
					}, 200);

					// 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					mBluetoothLeService.setCharacteristicNotification(
							gattCharacteristic, true);
					target_chara = gattCharacteristic;
					// 设置数据内容
					// 往蓝牙模块写入数据
					// mBluetoothLeService.writeCharacteristic(gattCharacteristic);
				}
				List<BluetoothGattDescriptor> descriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor descriptor : descriptors)
				{
					System.out.println("---descriptor UUID:"
							+ descriptor.getUuid());
					// 获取特征值的描述
					mBluetoothLeService.getCharacteristicDescriptor(descriptor);
					// mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
					// true);
				}

				gattCharacteristicGroupData.add(currentCharaData);
			}
			// 按先后顺序，分层次放入特征值集合中，只有特征值
			mGattCharacteristics.add(charas);
			// 构件第二级扩展列表（服务下面的特征值）
			gattCharacteristicData.add(gattCharacteristicGroupData);

		}

	}
	/**
	 * 将数据分包
	 *
	 * **/
	public int[] dataSeparate(int len)
	{
		int[] lens = new int[2];
		lens[0]=len/20;
		lens[1]=len-20*lens[0];
		return lens;
	}



	/*
	 * 发送按键的响应事件，主要发送文本框的数据
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		byte[] buff = send_et.getText().toString().getBytes();
		int len = buff.length;
		int[] lens = dataSeparate(len);
		for(int i =0;i<lens[0];i++)
		{
			String str = new String(buff, 20*i, 20);
			target_chara.setValue(str);//只能一次发送20字节，所以这里要分包发送
			//调用蓝牙服务的写特征值方法实现发送数据
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
		if(lens[1]!=0)
		{
			String str = new String(buff, 20*lens[0], lens[1]);
			target_chara.setValue(str);
			//调用蓝牙服务的写特征值方法实现发送数据
			mBluetoothLeService.writeCharacteristic(target_chara);

		}
	}

}
