package com.scoto.rccontroller;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;

public class PairedDeviceActivity extends AppCompatActivity {

    private static final String TAG = "PairedDeviceActivity";
    private ListView listView;
    private BluetoothOperations mBluetoothOperations;
    private ArrayAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    private View decoderView;

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

        //Store device address in local storage.
        sharedPreferences = this.getSharedPreferences("ApplicationShared", MODE_PRIVATE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = pairedDevices.get(i).toString();

                final String address = info.substring((info.length() - 17));//17 device address mac address length.

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DeviceAddress", address);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(),DrawTemplateActivity.class);
                intent.putExtra("DEVICE_ADDRESS",address);
                startActivity(intent);

            }
        });

    }

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
