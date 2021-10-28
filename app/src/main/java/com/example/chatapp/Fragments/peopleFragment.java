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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chatapp.Holder.userViewHolder;
import com.example.chatapp.R;
import com.example.chatapp.chatActivity;
import com.example.chatapp.common.common;
import com.example.chatapp.model.userModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class peopleFragment extends Fragment {

    @BindView(R.id.people_rv)
    RecyclerView recycler_people;


    FirebaseRecyclerAdapter adapter;
    private Unbinder unbinder;
    private PeopleViewModel mViewModel;
    static peopleFragment instance;

    public static peopleFragment getInstance() {
        return instance==null ? new peopleFragment() :instance;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView= inflater.inflate(R.layout.people_fragment, container, false);
        initView(itemView);
        loadPeople();
        return  itemView;
    }

    private void loadPeople() {
     Query query= FirebaseDatabase.getInstance().getReference().child(common.USER_REF);
        FirebaseRecyclerOptions<userModel> options =new FirebaseRecyclerOptions.Builder<userModel>()
                .setQuery(query,userModel.class).build();
        //The FirebaseRecyclerAdapter binds a Query to a RecyclerView.
        // When data is added, removed, or changed these updates are automatically applied to your UI in real time.
        adapter=new FirebaseRecyclerAdapter<userModel, userViewHolder>(options) {

            @NonNull
            @Override
            public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_people,parent,false);
                return new userViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull userViewHolder holder, int position, @NonNull userModel model) {
              if(!adapter.getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                  //Hide Yourself
                  ColorGenerator generator=ColorGenerator.MATERIAL;
                  int color=generator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid());
                  TextDrawable.IBuilder builder=TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
                  TextDrawable drawable=builder.build(model.getFirstName().substring(0,1),color);

                  holder.people_iv.setImageDrawable(drawable);

                  StringBuilder stringBuilder=new StringBuilder();
                  stringBuilder.append(model.getFirstName()).append(" ").append(model.getLastName());

                  holder.people_name.setText(stringBuilder.toString());

                  holder.people_bio.setText(model.getBio());

                  holder.itemView.setOnClickListener(v -> {

                       common.chatUser=model;
                       common.chatUser.setUid(adapter.getRef(position).getKey());
                      // subscribe topic

                      String roomId=common.GenerateChatRoomId(FirebaseAuth.getInstance().getCurrentUser().getUid()
                              ,common.chatUser.getUid());
                      common.roomSelected=roomId;

                      Log.d("ROOMID:",roomId);

                      //register to topic

                      FirebaseMessaging.getInstance().subscribeToTopic(roomId)
                              .addOnSuccessListener(aVoid ->
                                      startActivity(new Intent(getContext(), chatActivity.class))
                              );


                  });
              }
              else{
                  holder.itemView.setVisibility(View.GONE);
                  holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
              }

            }
        };
        adapter.startListening();
        recycler_people.setAdapter(adapter);

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

    private void initView(View itemView) {
        unbinder = ButterKnife.bind(this,itemView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
         recycler_people.setLayoutManager(layoutManager);
         recycler_people.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PeopleViewModel.class);
        // TODO: Use the ViewModel
    }

}