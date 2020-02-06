package com.scoto.rccontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.scoto.rccontroller.Services.BTConnectService;

import java.util.ArrayList;

public class PairedDeviceActivity extends AppCompatActivity {

    private static final String TAG = "PairedDeviceActivity";
    private ListView listView;
    private BluetoothOperations mBluetoothOperations;
    private ArrayAdapter mAdapter;
    private String mAddress;
    private SharedPreferences sharedPreferences;
    private View decoderView;
   // BTConnectService btConnectService;

    //boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_device);
        decoderView = getWindow().getDecorView();
        decoderView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    decoderView.setSystemUiVisibility(hideSystembars());
                }
            }
        });

        mBluetoothOperations = new BluetoothOperations();
        final ArrayList<String> pairedDevices = mBluetoothOperations.getPairedDevices();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedDevices);
        listView = findViewById(R.id.pairedDevices);

        listView.setAdapter(mAdapter);
        sharedPreferences = this.getSharedPreferences("ApplicationShared", MODE_PRIVATE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = pairedDevices.get(i).toString();

                final String address = info.substring((info.length() - 17));
                mAddress = address;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DeviceAddress", address);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(),DrawTemplateActivity.class);
                intent.putExtra("DEVICE_ADDRESS",address);
                startActivity(intent);
                //btConnectService.getAddress(address);

                /*Connectio Client will be here*/
//                BTConnect btConnect = new BTConnect(PairedDeviceActivity.this,address);
//                btConnect.execute();

            }
        });

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        intent = new Intent(this, BTConnectService.class);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unbindService(connection);
//        mBound = false;
//    }

//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            BTConnectService.LocalBinder localBinder = (BTConnectService.LocalBinder) iBinder;
//            btConnectService = localBinder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBound = false;
//        }
//    };

    //Hides the status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decoderView.setSystemUiVisibility(hideSystembars());
        }
    }

    private int hideSystembars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}
