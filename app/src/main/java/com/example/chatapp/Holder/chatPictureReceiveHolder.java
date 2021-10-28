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

public class chatPictureReceiveHolder extends RecyclerView.ViewHolder {
    private Unbinder unbinder;

    @BindView(R.id.layout_message_pic_fr_iv)
    public ImageView iv_fr;

    @BindView(R.id.layout_message_pic_fr_tv_msg)
    public TextView tv_msg;

    @BindView(R.id.layout_message_pic_fr_tv_time)
    public TextView tv_time;


    public chatPictureReceiveHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this,itemView);

    }
}
