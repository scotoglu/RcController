package com.scoto.rccontroller.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.scoto.rccontroller.R;

public class CustomDialogAddButton extends AppCompatDialogFragment  {
    final private String TAG = "DialogAddButton";
    private EditText editText;
    private DialogAddButtonListener listener;
    private Spinner spinner1, spinner2;
    private String tempSelected;
    private Context context;
    private String selectedIcon;

    public CustomDialogAddButton(Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_addbutton, null);

        spinner1 = view.findViewById(R.id.spinner);
        spinner2 = view.findViewById(R.id.spinner_icon);

        builder.setView(view)
                .setTitle("Add Button")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtButton = editText.getText().toString();
                        listener.applyText(txtButton, tempSelected, selectedIcon);


                    }
                });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.button_color, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(onItemSelectedListener);








        ArrayAdapter<CharSequence> iconAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.button_icons, android.R.layout.simple_spinner_item);

        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(iconAdapter);

        spinner2.setOnItemSelectedListener(onItemSelectedListenerIcon);









        editText = view.findViewById(R.id.addButtonFromLayout);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAddButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogAddButtonListener");
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            tempSelected = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    AdapterView.OnItemSelectedListener onItemSelectedListenerIcon = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selectedIcon = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public interface DialogAddButtonListener {
        void applyText(String txtBtn, String btnColor, String viewIcon);
    }
}
