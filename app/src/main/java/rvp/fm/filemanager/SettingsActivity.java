package rvp.fm.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    CardView mCardView;

    TextView defaultFolderTextview;
    TextView dialogTextview;
    Button dialogButton, selectButton;
    String selectedDefault = "", selectedDefaultPath, nameForTextview;
    private static final int CUSTOM_DIALOG = 0;
    private static final int INTENT_CONSTANT = 3;
    private static final String KEY_SETTINGS = "settings_";
    private static final String KEY_NAME = "name_";
    ListView listView;
    File root, currentFolder;
    private List<String> fileList = new ArrayList<String>();
    private Bundle mExtras;
    SharedPreferences mSharedPreferences;
    Intent intentBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dialogButton = (Button) findViewById(R.id.button_in_dialog);
        selectButton = (Button) findViewById(R.id.button_select_in_dialog);
        defaultFolderTextview = (TextView) findViewById(R.id.sub_textView_in_settingscard);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        intentBack = new Intent();
        mCardView = (CardView) findViewById(R.id.card_in_settingscard);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(CUSTOM_DIALOG);
            }
        });
        mExtras = getIntent().getExtras();
        if (mExtras != null) {
            String rootString = getIntent().getStringExtra("SEND_PATH");
            root = new File(rootString);

            /*
             *    Remembers intent value on rotation
             */
            defaultFolderTextview.setText(mSharedPreferences.getString(KEY_NAME, root.getName()));
            intentBack.putExtra("FROM_SETTINGS", mSharedPreferences.getString(KEY_SETTINGS, root.getPath()));
            setResult(INTENT_CONSTANT, intentBack);
        } else {
            root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            defaultFolderTextview.setText(root.getName());
        }
        //currentFolder = root;
        currentFolder = new File(mSharedPreferences.getString(KEY_SETTINGS, root.getPath()));


    }


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;

        switch (id) {
            case CUSTOM_DIALOG:
                dialog = new Dialog(SettingsActivity.this);
                dialog.setContentView(R.layout.dialog_layout);
                dialog.setTitle("Select default folder");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                dialogTextview = (TextView) dialog.findViewById(R.id.textview_in_dialog);
                dialogButton = (Button) dialog.findViewById(R.id.button_in_dialog);
                selectButton = (Button) dialog.findViewById(R.id.button_select_in_dialog);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListDir(currentFolder.getParentFile());
                    }
                });
                selectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(SettingsActivity.this, dialogTextview.getText() + " is selected",
                                Toast.LENGTH_LONG).show();
                        dismissDialog(CUSTOM_DIALOG);
                        defaultFolderTextview.setText(nameForTextview);

                        /*
                         *  Remembers selection on rotation
                         */
                        SharedPreferences.Editor editorSettings = mSharedPreferences.edit();
                        editorSettings.putString(KEY_NAME, nameForTextview);
                        editorSettings.putString(KEY_SETTINGS, selectedDefaultPath);
                        editorSettings.commit();
                        intentBack.putExtra("FROM_SETTINGS", selectedDefaultPath);
                        setResult(INTENT_CONSTANT, intentBack);
                    }
                });
                listView = (ListView) dialog.findViewById(R.id.listview_in_dialog);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File(fileList.get(position));
                        if (selected.isDirectory()) {
                            selectedDefault = selected.getName();
                            selectedDefaultPath = selected.getPath();
                            ListDir(selected);
                        } else {
                        }
                    }
                });

                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG:
                ListDir(currentFolder);
                break;
        }
    }

    void ListDir(File f) {
        File base = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (f.equals(base)) {
            dialogButton.setEnabled(false);
        } else {
            dialogButton.setEnabled(true);
        }


        currentFolder = f;
        dialogTextview.setText(f.getPath());
        nameForTextview = f.getName();
        File[] files = f.listFiles();

        fileList.clear();
        for (File file : files) {
            fileList.add(file.getPath());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(directoryList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ON-Pause","on PAUSE");
    }
}
