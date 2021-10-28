package com.example.chatapp.Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class chatTextReceiveHolder extends RecyclerView.ViewHolder {

    Unbinder unbinder;
    @BindView(R.id.layout_message_own_tv_msg)
    public TextView tv_msg;
    @BindView(R.id.layout_message_own_tv_time)
    public TextView tv_time;

    public chatTextReceiveHolder(@NonNull View itemView) {
        super(itemView);
        unbinder= ButterKnife.bind(this,itemView);
    }
}
