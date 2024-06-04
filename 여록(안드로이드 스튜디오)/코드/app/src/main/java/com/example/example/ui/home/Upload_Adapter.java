package com.example.example.ui.home;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.example.R;

import java.util.ArrayList;

public class Upload_Adapter extends RecyclerView.Adapter<Upload_Adapter.ViewHolder> {
    private ArrayList<Uri> mData = new ArrayList<>();
    private Context mContext;

    public Upload_Adapter(ArrayList<Uri> uriList, Context context) {
        mData = uriList;
        mContext = context;
    }


    public ArrayList<Uri> getSelectedImageUris() {
        return mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.uploadimage_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = mData.get(position);
        Glide.with(mContext)
                .load(imageUri)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
