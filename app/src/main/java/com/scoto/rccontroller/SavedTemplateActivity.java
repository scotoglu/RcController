package com.scoto.rccontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.scoto.rccontroller.Database.AppDatabase;
import com.scoto.rccontroller.Modal.EntityModal;

import java.util.ArrayList;
import java.util.List;

public class SavedTemplateActivity extends AppCompatActivity {
    private static final String TAG = "SavedTemplateActivity";
    private ListView listViewSaved;
    private ArrayAdapter<String> mArrayAdapter;
    private View decoderView;
    private AppDatabase mAppDatabase;
    final private static String TEMPLATE_NAME = "TEMPLATE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_template);
        decoderView = getWindow().getDecorView();
        decoderView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    decoderView.setSystemUiVisibility(hideSystembars());
                }
            }
        });

        mAppDatabase = AppDatabase.getDatabase(SavedTemplateActivity.this);

        final ArrayList<String> templateList = new ArrayList<>();
        List<EntityModal> entityModals = mAppDatabase.viewDao().getAll();

        for (int i = 0; i < entityModals.size(); i++) {
            templateList.add(entityModals.get(i).getTemplateName());
        }

        listViewSaved = findViewById(R.id.savedTemplates);
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, templateList);
        listViewSaved.setAdapter(mArrayAdapter);
        listViewSaved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String templateName = templateList.get(i);
                Intent savedIntent = new Intent(SavedTemplateActivity.this, DrawTemplateActivity.class);
                savedIntent.putExtra(TEMPLATE_NAME, templateName.toString());
                startActivity(savedIntent);
            }
        });

    }

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
