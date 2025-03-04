package com.espressif.espblufi.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.espressif.espblufi.R;
import com.espressif.espblufi.app.BlufiApp;
import com.espressif.espblufi.app.BlufiLog;
import com.espressif.espblufi.constants.BlufiConstants;
import com.espressif.espblufi.constants.SettingsConstants;
import com.espressif.espblufi.db.RecordEntity;
import com.espressif.espblufi.db.RecordProvider;
import com.espressif.espblufi.task.NwManager;
import com.espressif.espblufi.util.DialogUtils;
import com.espressif.espblufi.util.FileUtils;
import com.espressif.espblufi.util.LooperEvent;
import com.espressif.espblufi.util.LooperManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivity extends AppCompatActivity {
    private static final int MENU_SETTINGS = 0x01;
    private final BlufiLog mLog = new BlufiLog(getClass());

    private List<ScanResult> mBleList;
    private DeviceAdapter mBleAdapter;
    private RecordAdapter mRecordAdapter;

    private Map<String, ScanResult> mDeviceMap;
    private ScanCallback mScanCallback;
    private String mBlufiFilter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable scanCheckTask = new Runnable() {
        @Override
        public void run() {
            stopScan();
            scan();
        }
    };
    private boolean isResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        NwManager.INSTANCE.init();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLog.d("onResume");
        isResume = true;
        EventBus.getDefault().register(this);
        LooperManager.INSTANCE.start(2000);
        requestPermission();
        updateRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLog.d("onPause");
        isResume = false;
        EventBus.getDefault().unregister(this);
        LooperManager.INSTANCE.stop();
        mBleList.clear();
        mBleAdapter.setList(mBleList);
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        mScanCallback = null;
        mDeviceMap.clear();
        NwManager.INSTANCE.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_SETTINGS, 0, R.string.main_menu_settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == MENU_SETTINGS) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // 配网记录
        RecyclerView mRvRecord = findViewById(R.id.rv_record);
        mRvRecord.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecordAdapter = new RecordAdapter();
        mRvRecord.setAdapter(mRecordAdapter);
        mRecordAdapter.setEmptyView(R.layout.item_empty);

        // 设备列表
        RecyclerView mRvDevice = findViewById(R.id.rv_device);
        mRvDevice.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBleList = new LinkedList<>();
        mBleAdapter = new DeviceAdapter();
        mRvDevice.setAdapter(mBleAdapter);

        mDeviceMap = new HashMap<>();
        mScanCallback = new ScanCallback(this);
    }

    private void initListener() {
        if (mBleAdapter != null) {
            mBleAdapter.setOnItemClickListener((adapter, view, position) -> gotoDevice(mBleAdapter.getItem(position).getDevice()));
        }
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted(data -> {
                    scan();
                })
                .onDenied(data -> {
                    ToastUtils.showLong("扫描设备需要定位权限!");
                })
                .start();
    }

    /**
     * 开始扫描
     */
    private void scan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (!adapter.isEnabled() || scanner == null) {
            Toast.makeText(this, R.string.main_bt_disable_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check location enable
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean locationEnable = locationManager != null && LocationManagerCompat.isLocationEnabled(locationManager);
            if (!locationEnable) {
                Toast.makeText(this, R.string.main_location_disable_msg, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mDeviceMap.clear();
        mBleList.clear();
        mBleAdapter.notifyDataSetChanged();
        mBlufiFilter = (String) BlufiApp.getInstance().settingsGet(SettingsConstants.PREF_SETTINGS_KEY_BLE_PREFIX,
                BlufiConstants.BLUFI_PREFIX);

        mLog.d("Start scan ble");
        scanner.startScan(null,
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(),
                mScanCallback);

        // 超时1分钟没有设备回调, 重新扫描
        handler.postDelayed(scanCheckTask, 60 * 1000);
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(mScanCallback);
        }
        mLog.d("Stop scan ble");

        handler.removeCallbacks(scanCheckTask);
    }

    private void resetCheckTick() {
        handler.removeCallbacks(scanCheckTask);
        if (isResume) {
            handler.postDelayed(scanCheckTask, 60 * 1000);
        }
    }

    private void updateRecord() {
        List<RecordEntity> allRecord = RecordProvider.INSTANCE.getAllRecord();
        mLog.d("更新配网记录");
        if (mRecordAdapter != null) {
            mRecordAdapter.setList(allRecord);
        }
    }

    @Subscribe
    public void onLooperEvent(LooperEvent event) {
        onIntervalScanUpdate();
    }

    /**
     * 刷新adapter数据
     */
    private void onIntervalScanUpdate() {
        List<ScanResult> devices = new ArrayList<>(mDeviceMap.values());
        Collections.sort(devices, (dev1, dev2) -> {
            Integer rssi1 = dev1.getRssi();
            Integer rssi2 = dev2.getRssi();
            return rssi2.compareTo(rssi1);
        });
        mDeviceMap.clear();
        mBleList.clear();
        mBleList.addAll(devices);
        runOnUiThread(() -> mBleAdapter.setList(mBleList));
        NwManager.INSTANCE.executor(mBleList);
        updateRecord();
    }

    private void gotoDevice(BluetoothDevice device) {
//        Intent intent = new Intent(MainActivity.this, BlufiActivity.class);
//        intent.putExtra(BlufiConstants.KEY_BLE_DEVICE, device);
//        startActivityForResult(intent, REQUEST_BLUFI);
//
//        mDeviceMap.clear();
//        mBleList.clear();
//        mBleAdapter.notifyDataSetChanged();
    }

    /**
     * 扫描回调
     */
    private static class ScanCallback extends android.bluetooth.le.ScanCallback {
        private WeakReference<MainActivity> aty;


        public ScanCallback(MainActivity activity) {
            aty = new WeakReference<>(activity);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                onLeScan(result);
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            onLeScan(result);
        }

        private void onLeScan(ScanResult scanResult) {
//            Log.d("ScanResult", "ID: " + scanResult.getDevice().getName() + ", mac: " + scanResult.getDevice().getAddress());
            MainActivity activity = aty.get();
            if (activity != null) {
                String name = scanResult.getDevice().getName();
                String filter = activity.mBlufiFilter.trim();
                if (!TextUtils.isEmpty(filter)) {
                    if (name == null || !name.startsWith(activity.mBlufiFilter)) {
                        return;
                    }
                }

                activity.mDeviceMap.put(scanResult.getDevice().getAddress(), scanResult);
                activity.resetCheckTick();
            }
        }
    }
}
