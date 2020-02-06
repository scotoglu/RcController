package com.scoto.rccontroller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBluetoothOps, btnDrawTemplate, btnSavedTemplate;
    private static final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private static int REQUEST_ENABLE_BT = 1;

    private View decoderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Hide navigation bar and status bar...*/
        decoderView = getWindow().getDecorView();
        decoderView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    decoderView.setSystemUiVisibility(hideSystembars());
                }
            }
        });


        btnBluetoothOps = findViewById(R.id.btnBluetooth);
        btnDrawTemplate = findViewById(R.id.btnDrawTemplate);
        btnSavedTemplate = findViewById(R.id.btnSavedTemps);

        btnBluetoothOps.setOnClickListener(this);
        btnDrawTemplate.setOnClickListener(this);
        btnSavedTemplate.setOnClickListener(this);

        /*if app will work on Emulator, comment below if-else statement,if you don't, cause a error
        Emulator has no Bluetooth features.
        */

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.no_bluetooth_adapter), Toast.LENGTH_SHORT).show();
        }
        if (mBluetoothAdapter.isEnabled()) {

        } else {
            Intent enabledIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabledIntent, REQUEST_ENABLE_BT);
        }

    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying...");
        super.onDestroy();
        final SharedPreferences sharedPreferences = this.getSharedPreferences("ApplicationShared", MODE_PRIVATE);

        if (sharedPreferences.getString("DeviceAddress", "Null") != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("DeviceAddress");
        }
        Log.d(TAG, "onDestroy: Destroying...");
        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            Toast.makeText(this,getResources().getString(R.string.disable_bt), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: Back Button pressed...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBluetooth:
                Intent bluetoothIntent = new Intent(this, PairedDeviceActivity.class);
                startActivity(bluetoothIntent);
                break;
            case R.id.btnDrawTemplate:
                Intent drawTemplateIntent = new Intent(this, DrawTemplateActivity.class);
                startActivity(drawTemplateIntent);
                break;
            case R.id.btnSavedTemps:
                Intent savedTempsIntent = new Intent(this, SavedTemplateActivity.class);
                startActivity(savedTempsIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getResources().getString(R.string.result_ok), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.result_canceled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*Hide navigation bar and status bar...*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decoderView.setSystemUiVisibility(hideSystembars());
        }
    }

    /*To hide n. bar and s. bar, used flags...*/
    private int hideSystembars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

}
