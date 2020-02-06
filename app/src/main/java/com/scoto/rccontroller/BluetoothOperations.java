package com.scoto.rccontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothOperations {

    private static final String TAG = "BluetoothOperations";
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    public BluetoothOperations() {
        Log.d(TAG, "BluetoothOperations: Constructor Acctive...");
    }

    public ArrayList getPairedDevices(){
        ArrayList<String> pairedDevices = new ArrayList<>();
        Set<BluetoothDevice> mBluetoothDevices = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothDevices.size()>0){
            for (BluetoothDevice device : mBluetoothDevices){
                pairedDevices.add(device.getName()+"\n"+device.getAddress());
            }
            return pairedDevices;
        }else{
            Log.d(TAG, "getPairedDevices: No paired devices");
            return pairedDevices;
        }
    }


}
