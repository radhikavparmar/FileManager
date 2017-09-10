package rvp.fm.filemanager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by radhikaparmar on 09/09/17.
 */

public class FolderFragment extends Fragment {

    ArrayList<String> arrayList;
    ListView listView;
    String mPath;
    File displayFile;
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mPath = getArguments().getString("BUNDLE_KEY_PATH");
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getActivity().findViewById(R.id.listview_in_fragment);
        displayFile = new File(mPath);
        File[] files = displayFile.listFiles();
        arrayList = new ArrayList<>();
        if (files != null) {
            Log.e("Fragment ", "Arraylistcreated");
            for (File f : files) {
                arrayList.add(f.getName());
            }
        }
        if(files.length==0){
            arrayList.add("No files or folder");

        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

    }
}
