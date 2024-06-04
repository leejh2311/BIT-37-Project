package com.example.example.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.example.R;
import java.util.List;

public class List_Adapter extends RecyclerView.Adapter<List_Adapter.ViewHolder> {

    private List<ListFragment.PackageItem> itemDataList;
    private Context context;

    public List_Adapter(List<ListFragment.PackageItem> itemDataList, Context context) {
        this.itemDataList = itemDataList;
        this.context = context;
    }

    public void setItems(List<ListFragment.PackageItem> items) {
        itemDataList = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListFragment.PackageItem itemData = itemDataList.get(position);

        String imageName = itemData.getImageName();  // getImageAddress() 대신 getImageName() 사용
        String imageUrl = generateImageUrl(imageName);  // generateImageUrl 함수를 활용하여 이미지 URL 생성

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imageView);

        holder.sectionTextView.setText(itemData.getSectionText());
    }

    // generateImageUrl 메서드 정의
    private String generateImageUrl(String imageName) {
        String serverBaseUrl = context.getString(R.string.server_base_url); // 서버 기본 URL을 가져옴
        return serverBaseUrl + "/image/" + imageName; // 이미지 경로를 서버 기본 URL과 결합하여 URL 생성
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView sectionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.section_image_view);
            sectionTextView = itemView.findViewById(R.id.section_text);
        }
    }
}