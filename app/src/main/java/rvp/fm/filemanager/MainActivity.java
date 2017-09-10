package rvp.fm.filemanager;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.string.no;


public class MainActivity extends AppCompatActivity implements NameAdapter.ListItemClickListener {

    public Boolean is_action_mode = false, is_fragment_on = false;
    private SparseBooleanArray selectedItems;
    int counter = 0;
    Toolbar mToolbar;
    ArrayList<ModelClass> mArrayList;
    private RecyclerView mRecyclerView;
    private NameAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView noItemTextView;

    private static final int CLICK = 0;
    private static final int LONG_CLICK = 2;
    private static final int INTENT_CONSTANT = 3;
    private static final String KEY_PATH = "path_";
    String mRoot;
    SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        noItemTextView = (TextView) findViewById(R.id.no_listitem_in_main);
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        selectedItems = new SparseBooleanArray();
        mToolbar = (Toolbar) findViewById(R.id.toolbar_in_main);
        setSupportActionBar(mToolbar);
        new FetchInBackgroundThread().execute();

    }

    @Override
    public void onListItemClick(int clickedItemIndex, int whichClick) {

        if (whichClick == CLICK) {
            if (mArrayList.get(clickedItemIndex).getTypeOfFolder() == false && is_action_mode == false) {
                String path = mArrayList.get(clickedItemIndex).getPath();
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(path);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                //String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                String ext = fileExt(path);
                String type = mime.getMimeTypeFromExtension(ext);
                intent.setDataAndType(Uri.fromFile(file), type);
                PackageManager manager = getApplicationContext().getPackageManager();
                List<ResolveInfo> info = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (info.isEmpty())
                    Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
                else
                    startActivity(intent);

            }
            if (mArrayList.get(clickedItemIndex).getTypeOfFolder() == true && is_action_mode == false) {

                is_fragment_on = true;
                Bundle bundle = new Bundle();
                bundle.putString("BUNDLE_KEY_PATH", mArrayList.get(clickedItemIndex).getPath());
                FolderFragment folderFragment = new FolderFragment();
                folderFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter, R.animator.exit);
                transaction.add(R.id.coordinatorLayout_in_main, folderFragment, "KEY_FRAGMENT");
                transaction.commit();

            }
            if (is_action_mode == true) {
                if (selectedItems.get(clickedItemIndex, false)) {
                    selectedItems.delete(clickedItemIndex);
                    mArrayList.get(clickedItemIndex).setSelected(false);
                    mAdapter.notifyItemChanged(clickedItemIndex);
                    //v.setBackgroundColor(Color.WHITE);
                    counter--;
                    mToolbar.setTitle(counter + " item/s selected");


                } else {
                    selectedItems.put(clickedItemIndex, true);
                    mArrayList.get(clickedItemIndex).setSelected(true);
                    //v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mAdapter.notifyItemChanged(clickedItemIndex);
                    counter++;
                    mToolbar.setTitle(counter + " item/s selected");
                }
            }


        }
        if (whichClick == LONG_CLICK) {

            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.menu_action_mode);
            mToolbar.setTitle(counter + " item/s selected");
            is_action_mode = true;

        }

    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    private class FetchInBackgroundThread extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            mRoot = mSharedPreferences.getString(KEY_PATH, root.getPath());
            root = new File(mRoot);
            File[] files = root.listFiles();

            if (files != null) {

                mArrayList = new ArrayList<ModelClass>();
                for (File f : files) {
                    ModelClass object = new ModelClass();
                    if (f.isDirectory()) {
                        object.setNameOfFolder(f.getName());
                        object.setTypeOfFolder(true);
                        object.setPath(f.getPath());
                        object.setSelected(false);
                        mArrayList.add(object);

                    } else {
                        object.setNameOfFolder(f.getName());
                        object.setTypeOfFolder(false);
                        object.setPath(f.getPath());
                        object.setSelected(false);
                        mArrayList.add(object);
                    }


                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //set it in recycler view
            mAdapter = new NameAdapter(mArrayList, MainActivity.this);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (mArrayList == null) {
                    noItemTextView.setVisibility(View.VISIBLE);

                } else if (mArrayList.size() == 0) {
                   /*
                   * Repetitive for Rotation and refreshing
                   * */
                    noItemTextView.setVisibility(View.VISIBLE);
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_in_main);

                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    //mAdapter = new NameAdapter(mArrayList, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);

                } else {
                    noItemTextView.setVisibility(View.INVISIBLE);
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_in_main);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                }
            } else {
                if (mArrayList == null) {
                    noItemTextView.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();

                } else if (mArrayList.size() == 0) {
                    noItemTextView.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();

                } else {
                    noItemTextView.setVisibility(View.INVISIBLE);
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_in_main);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);


                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemThatWasSelected = item.getItemId();

        if (menuItemThatWasSelected == R.id.item_refresh) {
            {
                if (mArrayList != null) {
                    mArrayList.clear();
                    new FetchInBackgroundThread().execute();
                    return true;
                }
            }
        }
        if (menuItemThatWasSelected == R.id.item_settings) {
            if (mArrayList != null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout_in_main), R.string.settings, Snackbar.LENGTH_LONG).setAction(rvp.fm.filemanager.R.string.ok, null);
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                intentSettings.putExtra("SEND_PATH", mRoot);
                startActivityForResult(intentSettings, INTENT_CONSTANT);
                return true;
            }
        }
        if (menuItemThatWasSelected == R.id.item_delete) {

            if (counter != 0) {
                new AlertDialog.Builder(MainActivity.this).setTitle(R.string.delete_items).setMessage(R.string.delete_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            /*
                             * continue with deletion of file or folders
                             */
                                for (int i = 0; i < selectedItems.size(); i++) {
                                    int position = selectedItems.keyAt(i);
                                    deleteRecursive(new File(mArrayList.get(position).getPath()));
                                }
                                mArrayList.clear();
                                mAdapter = new NameAdapter(mArrayList, MainActivity.this);
                                mRecyclerView.invalidate();
                                new FetchInBackgroundThread().execute();
                                mToolbar.getMenu().clear();
                                mToolbar.inflateMenu(R.menu.main);
                                is_action_mode = false; //ActionMode is finished
                                mToolbar.setTitle("File Manager");
                                counter = 0;
                                selectedItems = new SparseBooleanArray();


                            }
                        }).setNegativeButton(no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // remove CAB

                        mArrayList = new ArrayList<ModelClass>();
                        mToolbar.getMenu().clear();
                        mToolbar.inflateMenu(R.menu.main);
                        is_action_mode = false; //ActionMode is finished
                        selectedItems = new SparseBooleanArray();
                        new FetchInBackgroundThread().execute();
                        mToolbar.setTitle("File Manager");
                        counter = 0;


                    }
                }).setIcon(android.R.drawable.ic_delete).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CONSTANT) {
            if (resultCode == INTENT_CONSTANT) {
                mRoot = data.getStringExtra("FROM_SETTINGS");
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(KEY_PATH, mRoot);
                editor.commit();
                new FetchInBackgroundThread().execute();

            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (is_action_mode) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                // handle your back button code here
                mToolbar.getMenu().clear();
                mToolbar.inflateMenu(R.menu.main);
                is_action_mode = false; //ActionMode is finished
                mArrayList.clear();
                new FetchInBackgroundThread().execute();
                mToolbar.setTitle("File Manager");
                selectedItems = new SparseBooleanArray();
                counter = 0;
                return true; // consumes the back key event
            }
        }
        if (is_fragment_on) {

            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("KEY_FRAGMENT")).commit();
            is_fragment_on = false;
            return true; // consumes the back key event
        }
        return super.dispatchKeyEvent(event);
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
}
