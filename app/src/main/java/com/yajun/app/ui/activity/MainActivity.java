package com.yajun.app.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yajun.app.R;
import com.yajun.app.SmoothListView.SmoothListView;
import com.yajun.app.common.GlobalScreenshot;
import com.yajun.app.module.BluetoothInfo;
import com.yajun.app.ui.adapter.TravelingAdapter;
import com.yajun.app.util.BitmapUtil;
import com.yajun.app.util.LogUtil;
import com.yajun.app.util.SmartBarUtils;
import com.yajun.app.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static android.support.design.R.id.info;

public class MainActivity extends AppCompatActivity implements SmoothListView.ISmoothListViewListener {

    private static final int REQUEST_OPEN = 0X001;
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    @BindView(R.id.txt_title)
    TextView mTitle;
    @BindView(R.id.txt_open)
    TextView mOpen;
    @BindView(R.id.txt_refresh)
    TextView mRefresh;
    @BindView(R.id.txt_share_screen)
    TextView mShare;

    @BindView(R.id.listView)
    SmoothListView smoothListView;

    TravelingAdapter mAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    private String[] mBluetoothActions = {
            "ACTION_STATE_CHANGED", // 蓝牙状态值发生改变
            "ACTION_SCAN_MODE_CHANGED", // 蓝牙扫描状态(SCAN_MODE)发生改变
            "ACTION_DISCOVERY_STARTED", // 蓝牙扫描过程开始
            "ACTION_DISCOVERY_FINISHED", //蓝牙扫描过程结束
            "ACTION_LOCAL_NAME_CHANGED", //蓝牙设备Name发生改变
            "ACTION_REQUEST_DISCOVERABLE", // 请求用户选择是否使该蓝牙能被扫描
            "ACTION_REQUEST_ENABLE", //请求用户选择是否打开蓝牙
            "ACTION_FOUND", //蓝牙扫描时，扫描到任一远程蓝牙设备时，会发送此广播
    };

    // 蓝牙广播接收器
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //扫描到了任一蓝牙设备
            // int   BOND_BONDED       表明蓝牙已经绑定
            // int   BOND_BONDING     表明蓝牙正在绑定过程中 ， bounding
            // int   BOND_NONE           表明没有绑定
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                LogUtil.e( "### BT BluetoothDevice.ACTION_FOUND ##");
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(btDevice != null){
                    LogUtil.e( "Name : " + btDevice.getName() + " Address: " + btDevice.getAddress());
                    if(mAdapter.getData().indexOf(btDevice) == -1){ // 防止重复添加
                        mAdapter.getData().add(btDevice);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                LogUtil.e( "### BT ACTION_BOND_STATE_CHANGED ##");
                int cur_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                int previous_bond_state = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE);
                LogUtil.e(  "### cur_bond_state ##" + cur_bond_state + " ~~ previous_bond_state" + previous_bond_state);
                switch (cur_bond_state) {
                    case BluetoothDevice.BOND_BONDING:
                        LogUtil.e("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        LogUtil.e("BlueToothTestActivity", "完成配对");
//                        connect(device);//连接设备
                        break;
                    case BluetoothDevice.BOND_NONE:
                        LogUtil.e("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartBarUtils.hide(getWindow().getDecorView());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initBluetooth();

        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        List<BluetoothDevice> travelingList = new ArrayList<>();
        mAdapter = new TravelingAdapter(this, travelingList);
        smoothListView.setAdapter(mAdapter);
    }

    /**
     * http://blog.csdn.net/qinjuning/article/details/7726093
     */
    private void initBluetooth(){
        registerBluetoothReceiver(new String[]{BluetoothDevice.ACTION_FOUND,BluetoothDevice.ACTION_BOND_STATE_CHANGED});
        // 获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 判断是否存在蓝牙功能
        if(mBluetoothAdapter == null){
            // 该设备不支持蓝牙
            return;
        }
        // 获取本地蓝牙信息
        String name = mBluetoothAdapter.getName();
        String address = mBluetoothAdapter.getAddress();
        int state = mBluetoothAdapter.getState();
        LogUtil.e("蓝牙信息 name:" + name +",address:" + address + ",state:" + state);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothListView.stopRefresh();
                smoothListView.setRefreshTime("刚刚");
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothListView.stopLoadMore();
            }
        }, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.txt_share_screen)
    public void onShareScreen(){
//        screenshot();
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        final GlobalScreenshot screenshot = new GlobalScreenshot(this);
        Bitmap btm = screenshot.takeScreenshot(getWindow().getDecorView(), new Runnable() {
            @Override
            public void run() {

            }
        }, false, false);
        if(btm != null){
            BitmapUtil.compressBmpToFile(btm,new File(Environment.getDownloadCacheDirectory().getAbsolutePath() + File.separator + "temp.jpg"));
        }
    }

    public void screenshot(){
        final Point size = new Point();
        size.x = 1080;//最终截屏图片的大小，可以和屏幕不一样大
        size.y = 1920;
        final String surfaceClassName ;
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        mShare.post(new Runnable() {
            @Override
            public void run() {
                try {
                    // Android提供了两个截屏方法Surface. screenshot和SurfaceControl. screenshot，
                    // 这两个API是隐藏的，客户端没有权限调用，
                    // 即使通过反射也得不到bitmap，我们可以使用adb命令 启动一个进程，
                    // 让该进程调用该API就可以得到bitmap了，然后通过socket把数据发送到PC即可。
                    Bitmap btm = (Bitmap) Class.forName(surfaceClassName)
                            .getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE})
                            .invoke(null, new Object[]{Integer.valueOf(size.x), Integer.valueOf(size.y)});
                    if(btm != null){
                        BitmapUtil.compressBmpToFile(btm,new File(Environment.getDownloadCacheDirectory().getAbsolutePath() + File.separator + "temp.jpg"));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @OnClick(R.id.txt_open)
    public void onOpenBt(){
        //判断蓝牙是否打开
        if(mBluetoothAdapter.isEnabled()){
            // 关闭蓝牙
            boolean isClose = mBluetoothAdapter.disable();
            LogUtil.e("蓝牙关闭状态:" + isClose);
        } else {
            // 打开蓝牙
            boolean isOpened = mBluetoothAdapter.enable();
            LogUtil.e("蓝牙打开状态:" + isOpened);
//            //未打开蓝牙，才需要打开蓝牙 ,调用系统API打开
//            if(!mBluetoothAdapter.isEnabled()){
//                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(intent,REQUEST_OPEN);
//            }
        }
//        int state = mBluetoothAdapter.getState();
//        switch (state){
//            case BluetoothAdapter.STATE_ON: // 蓝牙已经打开
//                LogUtil.e("蓝牙已经打开");
//                break;
//            case BluetoothAdapter.STATE_TURNING_ON: // 蓝牙处于打开过程中
//                LogUtil.e("蓝牙正在打开");
//                break;
//            case BluetoothAdapter.STATE_OFF: // 蓝牙已经关闭
//                LogUtil.e("蓝牙已经关闭");
//                break;
//            case BluetoothAdapter.STATE_TURNING_OFF: // 蓝牙处于关闭过程中
//                LogUtil.e("蓝牙正在关闭");
//                break;
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_OPEN == requestCode){
            if(resultCode == RESULT_CANCELED){
                ToastUtil.s("请求取消");
            }else if(resultCode == RESULT_OK){
                ToastUtil.s("请求成功");
            }
        }
    }

    // 扫描蓝牙设备
    @OnClick(R.id.txt_refresh)
    public void onRefreshBt(){
        if(!mBluetoothAdapter.isEnabled()){
            ToastUtil.s("蓝牙设备没有打开，请打开设备");
            return;
        }
        // 判断蓝牙是否正在处于扫描过程中
        if(mBluetoothAdapter.isDiscovering()){
            // 取消扫描过程
            mBluetoothAdapter.cancelDiscovery();
            ToastUtil.s("蓝牙取消扫描过程");
        } else {
            // 扫描蓝牙设备
            mAdapter.clearAll();
            mAdapter.notifyDataSetChanged();
            mBluetoothAdapter.startDiscovery();
            ToastUtil.s("扫描蓝牙设备");
        }
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(int position){
        // 判断蓝牙是否正在处于扫描过程中
        if(mBluetoothAdapter.isDiscovering()){
            // 取消扫描过程
            mBluetoothAdapter.cancelDiscovery();
            ToastUtil.s("蓝牙取消扫描过程");
        }
        connect(mAdapter.getItem(position - 1));
    }

    private void connect(BluetoothDevice bluetoothDevice) {
        UUID uuid = UUID.fromString(SPP_UUID);
        // 验证蓝牙设备MAC地址是否有效。所有设备地址的英文字母必须大写，48位
        if(BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress())){
            BluetoothSocket socket;
            try {
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                if(!socket.isConnected()){
                    LogUtil.e("蓝牙连接中......");
                    socket.connect();
                }
            } catch (Exception e) {
                try {
                    LogUtil.e("trying fallback...");
                    socket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                    socket.connect();
                    LogUtil.e("Connected");
                }
                catch (Exception e2) {
                    LogUtil.e("Couldn't establish Bluetooth connection!");
                }
            }
        }else {
            ToastUtil.s("MAC地址无效");
        }
    }

    public void registerBluetoothReceiver(String[] actionArray) {
        IntentFilter filter = new IntentFilter();
        for (String action : actionArray) {
            filter.addAction(action);
        }
        registerReceiver(mBluetoothReceiver, filter);
    }

    public void unregisterBluetoothReceiver() {
        if (mBluetoothReceiver != null) {
            try {
                unregisterReceiver(mBluetoothReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBluetoothReceiver();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
