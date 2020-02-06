package com.scoto.rccontroller.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.scoto.rccontroller.R;

public class CustomDialogAddTemplate extends AppCompatDialogFragment {

    private Context context;
    private EditText addTemplateName;
    private DialogTemplateNameListener listener;

    public CustomDialogAddTemplate( Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_template_layout, null);
        builder.setView(view)
                .setTitle("Add Template Name")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String templateName = addTemplateName.getText().toString();
                        if (templateName.isEmpty() || templateName.equals("") || templateName == null || templateName.equals(" ")){
                            Toast.makeText(context, "Template Name Required...", Toast.LENGTH_SHORT).show();
                        }else{
                            listener.templateText(templateName);
                        }

                    }
                });

        addTemplateName = view.findViewById(R.id.addTemplateName);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CustomDialogAddTemplate.DialogTemplateNameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogAddButtonListener");
        }
    }

    public interface DialogTemplateNameListener {
        void templateText(String tmpName);
    }
}
