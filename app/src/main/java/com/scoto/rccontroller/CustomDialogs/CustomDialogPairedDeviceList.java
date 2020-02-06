package com.scoto.rccontroller.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.scoto.rccontroller.BluetoothOperations;
import com.scoto.rccontroller.R;

import java.util.List;

public class CustomDialogPairedDeviceList extends AppCompatDialogFragment {
    private static final String TAG = "CustomDialogPairedDevic";
    private Context context;
    private DialogCustomPairedDeviceListListener listener;
    private String mAddress;

    public CustomDialogPairedDeviceList(Context context) {
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BluetoothOperations bluetoothOperations = new BluetoothOperations();
        final List<String> pairedList = bluetoothOperations.getPairedDevices();

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, pairedList);

        Log.d(TAG, "onCreateDialog: " + pairedList.size());


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.paired_device_list, null);

        final ListView listView = view.findViewById(R.id.deviceList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: Clicked Item..." + pairedList.get(i));
                String info = pairedList.get(i);
                String address = info.substring(info.length() - 17);
                mAddress = address;

            }
        });

        //    builder.setView(view).setTitle(getResources().getString(R.string.pairedDevice));

        builder.setView(view)
                .setTitle(getResources().getString(R.string.pairedDevice))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.pairedDevice("Dialog Screen.....");
                    }
                });


        return builder.create();
    }

    public interface DialogCustomPairedDeviceListListener {
        void pairedDevice(String address);
    }
}
