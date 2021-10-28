package com.example.chatapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class userViewHolder extends RecyclerView.ViewHolder {
     @BindView(R.id.layoutPeople_iv)
     public ImageView people_iv;
    @BindView(R.id.layoutPeople_tv_name)
    public TextView people_name;
    @BindView(R.id.layoutPeople_tv_bio)
    public TextView people_bio;

    private Unbinder unbinder;

    public userViewHolder(@NonNull View itemView) {
        super(itemView);

        unbinder= ButterKnife.bind(this,itemView);


    }
}
