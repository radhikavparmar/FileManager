package rvp.fm.filemanager;

/**
 * Created by radhikaparmar on 05/09/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameViewHolder> {
    private static final int CLICK = 0;
    private static final int LONG_CLICK = 2;
    private ArrayList<ModelClass> mArrayList;


    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex, int whichClick);

    }

    public NameAdapter(ArrayList<ModelClass> arrayList, ListItemClickListener listener) {
        mOnClickListener = listener;
        this.mArrayList = arrayList;


    }

    public class NameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public TextView nameTextView;
        public ImageView imageView;

        // public CheckBox select;

        public NameViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.name_textView_in_card);
            imageView = (ImageView) view.findViewById(R.id.imageView_in_cardview);

            // select = (CheckBox) view.findViewById(R.id.checkBox_in_card);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onListItemClick(clickedPosition, CLICK);
        }

        @Override
        public boolean onLongClick(View view) {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onListItemClick(clickedPosition, LONG_CLICK);
            return true;
        }
    }

    @Override
    public NameAdapter.NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.names_cardview;
        boolean shouldAttachToParentImmediately = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        NameViewHolder viewHolder = new NameViewHolder(movieView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NameAdapter.NameViewHolder holder, int position) {
        holder.nameTextView.setText(mArrayList.get(position).getNameOfFolder());
        if (mArrayList.get(position).getTypeOfFolder() == false) {
            holder.imageView.setImageResource(R.drawable.ic_insert_drive_file_black_36dp);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_folder_black_36dp);
        }
        if (mArrayList.get(position).getSelected() == false) {

            holder.itemView.setBackgroundColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#8c9eff"));
        }

    }


    @Override
    public int getItemCount() {

        return mArrayList.size();

    }

    //method to access in activity after updating selection
    public ArrayList<ModelClass> getArrayList() {
        return mArrayList;
    }

}
