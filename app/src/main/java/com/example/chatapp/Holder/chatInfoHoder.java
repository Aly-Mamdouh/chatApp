package com.example.chatapp.Holder;

import android.net.wifi.hotspot2.omadm.PpsMoParser;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class chatInfoHoder extends RecyclerView.ViewHolder {

    @BindView(R.id.layoutChat_iv)
    public ImageView chat_iv;

    @BindView(R.id.layoutChat_tv_name)
    public TextView chat_tv_name;

    @BindView(R.id.layoutChat_lastMassage_tv)
    public TextView chat_tv_lastMassage;

    @BindView(R.id.layoutChat_time_tv)
    public TextView chat_tv_time;

    Unbinder unbinder;


    public chatInfoHoder(@NonNull View itemView) {
        super(itemView);
        unbinder= ButterKnife.bind(this,itemView);
    }
}
