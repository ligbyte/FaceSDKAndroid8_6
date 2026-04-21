package com.baidu.idl.face.main.activity.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.idl.facesdkdemo.R;


public class FaceRecyclerAdapter extends RecyclerView.Adapter<FaceRecyclerAdapter.ViewHolder>
        implements View.OnClickListener {

    private int[] mData;

    public void setFaceRecyclerOnclickListener(FaceRecyclerOnclickListener faceRecyclerOnclickListener) {
        this.faceRecyclerOnclickListener = faceRecyclerOnclickListener;
    }

    private FaceRecyclerOnclickListener faceRecyclerOnclickListener;

    public FaceRecyclerAdapter(int[] data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleView.setText(mData[position]);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    @Override
    public void onClick(View view) {
        if (faceRecyclerOnclickListener != null){
            faceRecyclerOnclickListener.onClick((int) view.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.home_gateTv);
        }
    }
    public interface FaceRecyclerOnclickListener{
        void onClick(int position);
    }
}
