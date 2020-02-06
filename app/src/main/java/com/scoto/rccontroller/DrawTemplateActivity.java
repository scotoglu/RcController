package com.scoto.rccontroller;
/**/

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scoto.rccontroller.CustomDialogs.CustomDialogAddButton;
import com.scoto.rccontroller.CustomDialogs.CustomDialogAddTemplate;
import com.scoto.rccontroller.CustomDialogs.CustomDialogPairedDeviceList;
import com.scoto.rccontroller.Database.AppDatabase;
import com.scoto.rccontroller.Modal.EntityModal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DrawTemplateActivity extends AppCompatActivity implements CustomDialogAddTemplate.DialogTemplateNameListener,
        CustomDialogAddButton.DialogAddButtonListener,
        CustomDialogPairedDeviceList.DialogCustomPairedDeviceListListener {


    /*------------------*/
    private Button button;
    private View decoderView;
    private static final String TAG = "DrawTemplateActivity";
    private int viewId = -1;
    private String data;
    private int _xDelta;
    private int _yDelta;
    private String passedAddress = null;
    private String savedTemplate = null;
    private String templateName;
    private ImageView stateOfConnection;
    //    /*BTConnectClient properties*/
    private boolean mConnected = true;
    private ProgressDialog mProgressDialog;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private AppCompatActivity mCurrentActivity = null;
    private String mAddress = null;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Queue<String> stringQueue = new LinkedList<>();
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BTConnectionClient btConnectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_template);


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

        Intent intent = getIntent();
        passedAddress = intent.getStringExtra("DEVICE_ADDRESS");
        savedTemplate = intent.getStringExtra("TEMPLATE_NAME");

        templateName = savedTemplate;

        RelativeLayout relativeLayout = findViewById(R.id.workLayout);

        if (savedTemplate != null) {
            AppDatabase appDatabase = AppDatabase.getDatabase(DrawTemplateActivity.this);
            List<EntityModal> templateList = appDatabase.viewDao().getTemplate(savedTemplate);
            for (EntityModal entityModal : templateList) {
                Button tmpBtn = new Button(getApplicationContext());
                tmpBtn.setX(entityModal.getViewCoordinateX());
                tmpBtn.setY(entityModal.getViewCoordinateY());
                tmpBtn.setText(entityModal.getViewData());
                tmpBtn.setId(View.generateViewId());
                tmpBtn.setOnTouchListener(new ChoiceTouchListener());
                relativeLayout.addView(tmpBtn);
            }
        } else {
            addTemplateName();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("ApplicationShared", MODE_PRIVATE);
        String address = sharedPreferences.getString("DeviceAddress", "null");
        Log.d(TAG, "onCreate: Passed Address" + passedAddress);
        if (passedAddress == null) {
            passedAddress = address;
        }

        if (passedAddress != null && btConnectionClient == null) {
            /*Connection Thread */
            btConnectionClient = new BTConnectionClient(this);
            btConnectionClient.execute();
        } else {
            Toast.makeText(this, "There is no connection, work without connection...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: Back Pressed...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Called...");
        btConnectionClient.disconnect();
    }


    /*------------------------------------------------------------------------------------*/
    //At the activity start, opens a dialog and asks the template name.
    public void addTemplateName() {
        openTemplateDialog();
    }

    public void openTemplateDialog() {
        CustomDialogAddTemplate dialogTemplateName = new CustomDialogAddTemplate(this);
        dialogTemplateName.show(getSupportFragmentManager(), "Example Dialog");
    }

    @Override
    public void templateText(String tmpName) {
        Log.d(TAG, "templateText: " + tmpName);
        templateName = tmpName;

    }
    /*---------------------------------------------------------------------------------------*/

    public void openDeviceList() {
        CustomDialogPairedDeviceList customDialogPairedDeviceList = new CustomDialogPairedDeviceList(this);
        customDialogPairedDeviceList.show(getSupportFragmentManager(), "Example Diaalog");
    }

    @Override
    public void pairedDevice(String address) {
        mAddress = address;
        Log.d(TAG, "pairedDevice: mAddress" + mAddress);
    }

    /*---------------------------------------------------------------------------------------*/
    /*When press the Add, opens dialog for adding new button..*/
    public void addButton(View view) {
        Log.d(TAG, "addButton: Called");
        openDialog();
    }

    public void openDialog() {
        CustomDialogAddButton dialogAddButton = new CustomDialogAddButton(this);
        dialogAddButton.show(getSupportFragmentManager(), "Example Dialog");
    }

    @Override
    public void applyText(String txtBtn, String btnColor, String viewIcon) {

        Log.d(TAG, "applyText: Button Text" + txtBtn);
        Log.d(TAG, "applyText: Button Color" + btnColor);
        Log.d(TAG, "applyText: Button Icon" + viewIcon);

        RelativeLayout relativeLayout = findViewById(R.id.workLayout);
        Button newBtn = new Button(getApplicationContext());
        newBtn.setText(txtBtn);
        newBtn.setId(View.generateViewId());
        newBtn = setColor(btnColor, newBtn);
        newBtn = setIcon(viewIcon, newBtn);

        newBtn.setOnTouchListener(new ChoiceTouchListener());
        relativeLayout.addView(newBtn);

    }
    /*---------------------------------------------------------------------------------------*/

    public void deleteButton(View view) {
        Log.d(TAG, "deleteButton: Called");
        RelativeLayout relativeLayout = findViewById(R.id.workLayout);

        if (viewId != -1) {
            View deletedView = findViewById(viewId);
            relativeLayout.removeView(deletedView);
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    /*---------------------------------------------------------------------------------------*/
    public void clearButton(View view) {
        Log.d(TAG, "clearButton: Called");
        RelativeLayout relativeLayout = findViewById(R.id.workLayout);
        relativeLayout.removeAllViews();
    }

    /*---------------------------------------------------------------------------------------*/
    public void saveButton(View view) {
        Log.d(TAG, "saveButton: Called");
        Log.d(TAG, "saveButton: Template Name: " + templateName);

        RelativeLayout relativeLayout = findViewById(R.id.workLayout);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        if (templateName == null) {
            templateName = "Default";
        }
        int countOfView = relativeLayout.getChildCount();//Return number of view on layout

        if (countOfView <= 0) {
            Toast.makeText(this, getString(R.string.no_view), Toast.LENGTH_SHORT).show();
        } else {

            AppDatabase appDatabase = AppDatabase.getDatabase(DrawTemplateActivity.this);

            List<EntityModal> isAddedListBeforeCheck = appDatabase.viewDao().getTemplate(templateName);


            /*Updating template...*/
            if (isAddedListBeforeCheck.size() > 0) {
//                /*template added before*/
//                for (int i = 0; i < isAddedListBeforeCheck.size(); i++) {
//
//                    Button b = (Button) relativeLayout.getChildAt(i);//gets buttons on layout
//                    if (b.getX()==isAddedListBeforeCheck.get(i).getViewCoordinateX()
//                            && b.getY()== isAddedListBeforeCheck.get(i).getViewCoordinateY()
//                            && b.getText().toString()==isAddedListBeforeCheck.get(i).getViewData()){
//                        Log.d(TAG, "saveButton: View Already exist on database");
//                    }else{
//                        EntityModal entityModal = new EntityModal(
//                                templateName, b.getX(), b.getY(), b.getText().toString(), date.toString()//creates a object
//                        );
//                        appDatabase.viewDao().insertTemplate(entityModal);//insert into database
//                    }
//
//
//                }
//
//                Toast.makeText(this, "Successfully updated.", Toast.LENGTH_SHORT).show();

            } else {
                /*not added before*/
                for (int i = 0; i < countOfView; i++) {

                    Button b = (Button) relativeLayout.getChildAt(i);//gets buttons on layout
                    EntityModal entityModal = new EntityModal(
                            templateName, b.getX(), b.getY(), b.getText().toString(), date.toString()//creates a object
                    );
                    appDatabase.viewDao().insertTemplate(entityModal);//insert into database
                }

                List<EntityModal> checkIsAdded = appDatabase.viewDao().getTemplate(templateName);
                if (checkIsAdded.size() > 0) {
                    Toast.makeText(this, getString(R.string.added_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.added_unsuccess), Toast.LENGTH_SHORT).show();
                }

            }

        }


    }

    /*---------------------------------------------------------------------------------------*/

    private Button setColor(String btnColor, Button btn) {
        switch (btnColor) {
            case "Red":
                btn.setBackgroundColor(Color.rgb(255, 0, 0));//ContextCompat.getColor(getApplicationContext(), R.color.red_btn)
                break;
            case "Green":
                btn.setBackgroundColor(Color.rgb(0, 255, 0));
                break;
            case "Blue":
                btn.setBackgroundColor(Color.rgb(0, 0, 255));
            case "Choose Color":
                break;
        }
        return btn;
    }

    private Button setIcon(String btnIcon, Button btn) {
        switch (btnIcon) {
            case "Right":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.left_t));
                break;
            case "Left":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.right_t));
                break;
            case "Backward":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.down));
                break;
            case "Forward":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up));
                break;
            case "ON":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.on));
                break;
            case "OFF":
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.off));
                break;
            default:
                return btn;
        }
        return btn;
    }

    /*---------------------------------------------------------------------------------------*/

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

    /*-------------------------------Manage Drag and Drop Process START--------------------------------*/
    private class ChoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int X = (int) motionEvent.getRawX();
            final int Y = (int) motionEvent.getRawY();

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    viewId = view.getId();
                    Button temp = findViewById(viewId);
                    Log.d(TAG, "onTouch: Text " + temp.getText());
                    data = temp.getText().toString();
                    Log.d(TAG, "onTouch: data " + data);

                    //sends the data to working thread...
                    btConnectionClient.transmitData(data);
                    break;
                case MotionEvent.ACTION_POINTER_UP:

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    //layoutParams.rightMargin = -250;
                    //layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;

            }
            return true;
        }
    }
    /*-------------------------------Manage Drag and Droğ Process END---------------------------------*/
    /*-------------------------------------------------------------------------------------------------*/

    private class BTConnectionClient extends AsyncTask<Void, Void, Void> {

        String sendData;


        BTConnectionClient(AppCompatActivity activity) {
            Log.d(TAG, "BTConnectClient: Constructor Active");
            sendData = data;
            mCurrentActivity = activity;
            mAddress = passedAddress;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mCurrentActivity);
            mProgressDialog.setMessage("Connecting...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (mBluetoothSocket == null || !mConnected) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();
                }
            } catch (IOException e) {
                mConnected = false;
                Log.d(TAG, "doInBackground: " + e.getMessage());
            }
            new Thread(keepConnectedAndSend).start();
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!mConnected) {

                Log.d(TAG, "onPostExecute: Connection Failed. Is it a SPP Bluetooth running a server? Try again.");

            } else {

                Toast.makeText(mCurrentActivity, "Connected...", Toast.LENGTH_SHORT).show();

                // sendData();
            }
            mProgressDialog.dismiss();
        }

        public void disconnect() {
            if (mBluetoothSocket != null) //If the btSocket is busy
            {
                try {
                    mBluetoothSocket.close(); //close connection
                } catch (IOException e) {
                    Log.d(TAG, "disconnect: Error");
                }
            }

            Log.d(TAG, "disconnect: Disconnected");
        }


        public void transmitData(String data) {
            Log.d(TAG, "transmitData: Active");
            addDataToQueue(data);
        }


        private void addDataToQueue(String data) {
            Log.d(TAG, "addDataToQueue: Active...");
            lock.lock();
            stringQueue.add(data);
            condition.signal();
            lock.unlock();
        }

        private Runnable keepConnectedAndSend = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Runnable");
                if (!mBluetoothSocket.isConnected()) {
                    Log.d(TAG, "run: Socket is null...");
                    //  System.exit(1);
                }
                OutputStream out = null;
                while (true) {

                    lock.lock();
                    while (stringQueue.isEmpty()) {
                        try {
                            condition.await();
                        } catch (InterruptedException ex) {
                            Log.e(TAG, "run: ", ex);
                        }
                    }

                    String stringData = stringQueue.poll();
                    lock.unlock();
                    try {
                        out = mBluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        Log.d("Write data", "Bug BEFORE data was sent");
                    }
                    try {
                        out.write(stringData.getBytes());
                        if (!mBluetoothSocket.isConnected()) {
                            Log.d(TAG, "run: mBluetoothSocket is not connected...");
                        }
                        Log.d("SentFromBackground", "Success");
                    } catch (IOException ex) {
                        Log.d("Write data", "Bug AFTER data was sent");
                    }

                }


            }
        };
    }

    /*-------------------------------------------------------------------------------------------------*/
}
