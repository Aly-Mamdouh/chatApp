package com.example.chatapp.Fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chatapp.Holder.chatInfoHoder;
import com.example.chatapp.R;
import com.example.chatapp.chatActivity;
import com.example.chatapp.common.common;
import com.example.chatapp.model.chatInfoModel;
import com.example.chatapp.model.userModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class chatFragment extends Fragment {

    @BindView(R.id.chat_rv)
    RecyclerView recycler_chat;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd/MM/yyyy");
    FirebaseRecyclerAdapter adapter;
    private Unbinder unbinder;
    private ChatViewModel mViewModel;

    static chatFragment instance;

    public static chatFragment getInstance() {
        return instance==null ? new chatFragment() :instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView= inflater.inflate(R.layout.chat_fragment, container, false);
        initView(itemView);
        loadChat();
        return itemView;
    }

    private void loadChat() {
        Query query= FirebaseDatabase.getInstance().getReference().child(common.CHAT_LIST_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions<chatInfoModel> options =new FirebaseRecyclerOptions.Builder<chatInfoModel>()
                .setQuery(query,chatInfoModel.class).build();
        adapter=new FirebaseRecyclerAdapter<chatInfoModel, chatInfoHoder>(options) {

            @NonNull
            @Override
            public chatInfoHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView =LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat
                ,parent,false);

                return new chatInfoHoder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull chatInfoHoder holder, int position, @NonNull chatInfoModel model) {

                if(!adapter.getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    ColorGenerator generator=ColorGenerator.MATERIAL;
                    int color=generator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    TextDrawable.IBuilder builder=TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();

                    String displayName=FirebaseAuth.getInstance().getCurrentUser().getUid()
                            .equals(model.getCreateId())?model.getFriendName():model.getCreateName();

                    TextDrawable drawable=builder.build(displayName.substring(0,1),color);

                    holder.chat_iv.setImageDrawable(drawable);


                    holder.chat_tv_name.setText(displayName);
                    holder.chat_tv_lastMassage.setText(model.getLastMassage());
                    holder.chat_tv_time.setText(simpleDateFormat.format(model.getLastUpdate()));

                    holder.itemView.setOnClickListener(v -> {

                        // go to chat details
                        FirebaseDatabase.getInstance().getReference(common.USER_REF)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                                .equals(model.getCreateId())?model.getFriendId():model.getCreateId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            userModel model=snapshot.getValue(userModel.class);
                                            common.chatUser=model;
                                            common.chatUser.setUid(snapshot.getKey());

                                            // subscribe topic

                                            String roomId=common.GenerateChatRoomId(FirebaseAuth.getInstance().getCurrentUser().getUid()
                                            ,common.chatUser.getUid());
                                            common.roomSelected=roomId;

                                            Log.d("ROOMID",roomId);

                                            //register to topic

                                            FirebaseMessaging.getInstance().subscribeToTopic(roomId)
                                                    .addOnSuccessListener(aVoid ->
                                                            startActivity(new Intent(getContext(), chatActivity.class))
                                                    );


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });


                    });

                }
                else
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
            }
        };
        adapter.startListening();
        recycler_chat.setAdapter(adapter);

    }

    private void initView(View itemView) {
        unbinder = ButterKnife.bind(this,itemView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recycler_chat.setLayoutManager(layoutManager);
        recycler_chat.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }


}