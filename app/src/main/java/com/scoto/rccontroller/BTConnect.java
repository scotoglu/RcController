package com.scoto.rccontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BTConnect extends AsyncTask<Void,Void,Void>{

    private boolean mConnected = true;
    private ProgressDialog mProgressDialog;
    private BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSocket mBluetoothSocket = null;
    private AppCompatActivity mCurrentActivity = null;
    private String mAddress = null;

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//ID

    BTConnect(AppCompatActivity activity, String address) {
        mCurrentActivity = activity;
        mAddress = address;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mCurrentActivity, "Connecting...", "Please wait!!!");  //show a progress dialog
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
        return null;
    }

    @Override
    protected void onPostExecute(Void result) { //after the doInBackground, it checks if everything went fine

        super.onPostExecute(result);

        if (!mConnected) {

            message("Connection Failed. Is it a SPP Bluetooth running a server? Try again.");

        } else {
            message("Connected.");

            mCurrentActivity.finish();

        }
        mProgressDialog.dismiss();
    }


    public void disconnect() {
        if (mBluetoothSocket != null) //If the btSocket is busy
        {
            try {
                mBluetoothSocket.close(); //close connection
            } catch (IOException e) {
                message("Error");
            }
        }

        //message("Disconnected");
        Log.d(TAG, "disconnect: Disconnnected...");
        mCurrentActivity.finish();
    }


    private void message(String s) {
        Toast.makeText(mCurrentActivity.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
