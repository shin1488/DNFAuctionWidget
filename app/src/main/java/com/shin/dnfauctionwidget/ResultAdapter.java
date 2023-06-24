package com.shin.dnfauctionwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private List<Item> itemList;

    public ResultAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName;
        private CardView itemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.recycler_item_image);
            itemName = itemView.findViewById(R.id.recycler_item_name);
            itemCard = itemView.findViewById(R.id.recycler_item_card);

            itemCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        // 로컬 저장소에 변수 저장을 위한 SharedPreferences 객체 생성
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("SavedItem", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.clear();

                        // 변수 값 저장
                        editor.putString("itemId", itemList.get(position).getItemId());
                        editor.putString("itemName", itemList.get(position).getItemName());

                        // 변경 사항을 적용
                        editor.apply();

                        //액티비티 종료
                       ((Activity) itemView.getContext()).finish();
                }
            }});
        }

        public void bind(Item item) {
            // 이미지 로드
            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(item.getImageUrl())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            itemImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // 이미지 로드가 취소되거나 삭제된 경우 처리할 내용
                        }
                    });
            // 텍스트 설정
            itemName.setText(item.getItemName());
        }
    }
}
