package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.chatapp.Holder.chatInfoHoder;
import com.example.chatapp.Holder.chatPictureHolder;
import com.example.chatapp.Holder.chatPictureReceiveHolder;
import com.example.chatapp.Holder.chatTextHolder;
import com.example.chatapp.Holder.chatTextReceiveHolder;
import com.example.chatapp.Listener.IFirebaseLoadFailed;
import com.example.chatapp.Listener.ILoadTimeFromFirebaseListener;
import com.example.chatapp.Remote.IFCMService;
import com.example.chatapp.Remote.RetrofitFCMClient;
import com.example.chatapp.common.common;
import com.example.chatapp.model.FCMResponse;
import com.example.chatapp.model.FCMSendData;
import com.example.chatapp.model.chatInfoModel;
import com.example.chatapp.model.chatMassageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class chatActivity extends AppCompatActivity implements ILoadTimeFromFirebaseListener, IFirebaseLoadFailed {

    public static final int MY_CAMERA_REQ_CODE=1;
    public static final int MY_RESULT_LOAD_IMAGE=2;
    @BindView(R.id.chatLayout_tb)
    Toolbar chat_tb;

    @BindView(R.id.chatLayout_iv_camera)
    ImageView iv_camera;

    @BindView(R.id.chatLayout_iv_image)
    ImageView iv_image;

    @BindView(R.id.chatLayout_ed_chat)
    AppCompatEditText ed_chat;

    @BindView(R.id.chatLayout_iv_send)
    ImageView iv_send;

    @BindView(R.id.chatLayout_rv)
    RecyclerView chatLayout_rv;

    @BindView(R.id.chatLayout_preview)
    ImageView iv_preview;

    @BindView(R.id.chatLayout_iv)
    ImageView chatLayout_iv;

    @BindView(R.id.chatLayout_tv_name)
    TextView tv_name;

    FirebaseDatabase database;
    DatabaseReference chatRef , offsetRef;
    ILoadTimeFromFirebaseListener listener;
    IFirebaseLoadFailed errorListener;

    FirebaseRecyclerAdapter<chatMassageModel, RecyclerView.ViewHolder> adapter;
    FirebaseRecyclerOptions<chatMassageModel> options;

    Uri fileUri;
    StorageReference storageReference;
    LinearLayoutManager layoutManager;

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    @OnClick(R.id.chatLayout_iv_image)
    void onSelectImageClick(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,MY_RESULT_LOAD_IMAGE);

    }

    @OnClick(R.id.chatLayout_iv_camera)
    void onCaptureImageClick(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"From Your Camera");
        fileUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intent,MY_CAMERA_REQ_CODE);
    }

    @OnClick(R.id.chatLayout_iv_send)
    void onSubmitChatClick(){
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset=snapshot.getValue(Long.class);
                long estimateTimeInMills=System.currentTimeMillis()+offset;
                listener.OnLoadOnlyTimeSuccess(estimateTimeInMills);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorListener.onError(error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MY_CAMERA_REQ_CODE){
            if(resultCode==RESULT_OK){
                try{
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                    iv_preview.setImageBitmap(bitmap);
                    iv_preview.setVisibility(View.VISIBLE);

                }

                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        else if (requestCode==MY_RESULT_LOAD_IMAGE){
            if(resultCode==RESULT_OK){
                try{
                    final Uri uri=data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(uri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    iv_preview.setImageBitmap(bitmap);
                    iv_preview.setVisibility(View.VISIBLE);
                    fileUri=uri;

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Toast.makeText(this,"Please Select Image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        if(adapter!=null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        loadChatContent();
    }

    private void loadChatContent() {
        String receiverId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter=new FirebaseRecyclerAdapter<chatMassageModel, RecyclerView.ViewHolder>(options) {
            @Override
            public int getItemViewType(int position) {
                if(adapter.getItem(position).getSenderId().equals(receiverId)){
                    // if message is own
                    return !adapter.getItem(position).isPicture()?0:1;
                }

                else{
                    return !adapter.getItem(position).isPicture()?2:3;
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull chatMassageModel model) {

              if(holder instanceof chatTextHolder) {
                  chatTextHolder textHolder = (chatTextHolder) holder;
                  textHolder.tv_msg.setText(model.getContent());
                  textHolder.tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance()
                          .getTimeInMillis(), 0).toString());
              }
               else if(holder instanceof chatTextReceiveHolder) {
                  chatTextReceiveHolder textHolder = (chatTextReceiveHolder) holder;
                  textHolder.tv_msg.setText(model.getContent());
                  textHolder.tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance()
                          .getTimeInMillis(), 0).toString());


              }
              else if(holder instanceof chatPictureHolder) {
                  chatPictureHolder chatPictureHolder = (chatPictureHolder) holder;
                  chatPictureHolder.tv_msg.setText(model.getContent());
                  chatPictureHolder.tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance()
                          .getTimeInMillis(), 0).toString());

                  Glide.with(chatActivity.this).load(model.getPictureLink()).into(chatPictureHolder.iv_own);

              }
              else if(holder instanceof chatPictureReceiveHolder) {
                  chatPictureReceiveHolder chatPictureReceiveHolder = (chatPictureReceiveHolder) holder;
                  chatPictureReceiveHolder.tv_msg.setText(model.getContent());
                  chatPictureReceiveHolder.tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance()
                          .getTimeInMillis(), 0).toString());
                  Glide.with(chatActivity.this).load(model.getPictureLink()).into(chatPictureReceiveHolder.iv_fr);

              }

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if(viewType==0){ //text message of own
                     view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_text_own,parent,false);
                    return new chatTextReceiveHolder(view);
                }
                else if (viewType==1){ // picture of friend
                    view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_picture_own,parent,false);
                    return new chatPictureHolder(view);
                }

                else if(viewType==2){ //text message of friend
                      view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_text_friend,parent,false);
                    return new chatTextHolder(view);
                }
                else{
                    // picture of own
                    view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_picture_friend,parent,false);
                    return new chatPictureReceiveHolder(view);
                }
            }
        };
        //auto scroll when receive new message
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMassageCount=adapter.getItemCount();
                int lastVisiblePosition=layoutManager.findLastVisibleItemPosition();
                if(lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMassageCount-1)&&
                                lastVisiblePosition==(positionStart-1))){
                    chatLayout_rv.scrollToPosition(positionStart);

                }

            }
        });

        chatLayout_rv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        common.roomSelected="";
    }

    private void initViews() {
        ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
     listener =this;
     errorListener=this;
     database=FirebaseDatabase.getInstance();
     chatRef=database.getReference(common.CHAT_REF);
     offsetRef=database.getReference(".info/serverTimeOffset");
        Query query=chatRef.child(common.GenerateChatRoomId(common.chatUser.getUid(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())).child(common.CHAT_DETAIL_REF);

        options=new FirebaseRecyclerOptions.Builder<chatMassageModel>().setQuery(query,chatMassageModel.class).build();
        ButterKnife.bind(this);
        layoutManager=new LinearLayoutManager(this);
        chatLayout_rv.setLayoutManager(layoutManager);
        ColorGenerator generator=ColorGenerator.MATERIAL;
        int color=generator.getColor(common.chatUser.getUid());

        TextDrawable.IBuilder builder=TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();

        TextDrawable drawable=builder.build(common.chatUser.getFirstName().substring(0,1),color);

        iv_image.setImageDrawable(drawable);

        tv_name.setText(common.getName(common.chatUser));
        setSupportActionBar(chat_tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chat_tb.setNavigationOnClickListener(v -> {


        });
    }

    @Override
    public void OnLoadOnlyTimeSuccess(long estimateTimeInMS) {
        chatMassageModel chatMassageModel=new chatMassageModel();
        chatMassageModel.setName(common.getName(common.currentUser));
        chatMassageModel.setContent(ed_chat.getText().toString());
        chatMassageModel.setTimeStamp(estimateTimeInMS);
        chatMassageModel.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // current , we just implement chat text
        if(fileUri==null){
            chatMassageModel.setPicture(false);
            submitChatToFirebase(chatMassageModel,chatMassageModel.isPicture(),estimateTimeInMS);

        }
        else{
            uploadPicture(fileUri,chatMassageModel,estimateTimeInMS);
        }

    }

    private void uploadPicture(Uri fileUri, chatMassageModel chatMassageModel, long estimateTimeInMS) {

        AlertDialog dialog=new AlertDialog.Builder(chatActivity.this)
                .setCancelable(false)
                .setMessage("Please wait...")
                .create();
        dialog.show();
        String filename=common.gteFileName(getContentResolver(),fileUri);
        String path=new StringBuilder(common.chatUser.getUid())
                .append("/")
                .append(filename).toString();
        storageReference= FirebaseStorage.getInstance().getReference().child(path);
        /**
         * An controllable task that uploads and fires events for success, progress and failure. It also
         * allows pause and resume to control the upload operation.
         */
        UploadTask uploadTask=storageReference.putFile(fileUri);
        //create task
        Task<Uri> task=uploadTask.continueWithTask(task1 -> {
            if(!task1.isSuccessful()){
                Toast.makeText(this,"Failed To Load",Toast.LENGTH_LONG).show();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task2 ->{
            if(task2.isSuccessful()){
                String url=task2.getResult().toString();
                dialog.dismiss();
                chatMassageModel.setPicture(true);
                chatMassageModel.setPictureLink(url);
                submitChatToFirebase(chatMassageModel,chatMassageModel.isPicture(),estimateTimeInMS);

            }
        } ).addOnFailureListener(e -> Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show());

    }

    private void submitChatToFirebase(chatMassageModel chatMassageModel, boolean isPicture, long estimateTimeInMS) {
        chatRef.child(common.GenerateChatRoomId(common.chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser()
        .getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                appendChat(chatMassageModel,isPicture,estimateTimeInMS);
            }
            else{
                createChat(chatMassageModel,isPicture,estimateTimeInMS);
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    private void appendChat(chatMassageModel chatMassageModel, boolean isPicture, long estimateTimeInMS) {
        Map<String,Object> update_data=new HashMap<>();
        update_data.put("lastUpdate",estimateTimeInMS);
        // only text

        if(isPicture){
            update_data.put("lastmessage","<Image>");
        }
        else {
            update_data.put("lastMessage", chatMassageModel.getContent());
        }
        //update on user list
        FirebaseDatabase.getInstance().getReference(common.CHAT_LIST_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(common.chatUser.getUid())
                .updateChildren(update_data)
                .addOnFailureListener(e -> {
                    Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                })
                .addOnSuccessListener(aVoid -> {
                    // submit success for chatInfo
                    // copy to friend chat list

                    FirebaseDatabase.getInstance().getReference(common.CHAT_LIST_REF)
                            .child( common.chatUser.getUid())
                            .child(  FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .updateChildren(update_data)
                            .addOnFailureListener(e -> {
                                Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            })
                            .addOnSuccessListener(aVoid1 -> {
                                // add on chat ref
                                String roomID=common.GenerateChatRoomId(common.chatUser.getUid(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());

                                chatRef.child(roomID)
                                        .child(common.CHAT_DETAIL_REF)
                                        .push()
                                        .setValue(chatMassageModel)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnCompleteListener(task -> {
                                            // clear
                                            ed_chat.setText("");
                                            ed_chat.requestFocus();
                                            if(adapter!=null){
                                                adapter.notifyDataSetChanged();
                                            }
                                            // clear picture
                                            if(isPicture){
                                                fileUri=null;
                                                iv_image.setVisibility(View.GONE);
                                            }
                                            //send notification

                                            sendNotificationToFriend(chatMassageModel,roomID);

                                        });
                            });
                });

    }

    private void sendNotificationToFriend(chatMassageModel chatMassageModel, String roomID) {
     Map<String,String> notData=new HashMap<>();
     notData.put(common.NOTI_TITLE,"Message From :"+chatMassageModel.getName());
     notData.put(common.NOTI_CONTENT,chatMassageModel.getContent());
     notData.put(common.NOTI_SENDER,FirebaseAuth.getInstance().getCurrentUser().getUid());
     notData.put(common.NOTI_ROOM_ID,roomID);
        FCMSendData sendData=new FCMSendData("/topics/"+roomID,notData);

        compositeDisposable.add(

                ifcmService.sendNotification(sendData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fcmResponse -> {

                }, throwable -> {
                    Toast.makeText(this,throwable.getMessage(),Toast.LENGTH_LONG).show();
                })

        );


    }

    private void createChat(chatMassageModel chatMassageModel, boolean isPicture, long estimateTimeInMS) {

        chatInfoModel chatInfoModel=new chatInfoModel();
        chatInfoModel.setCreateId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatInfoModel.setFriendName(common.getName(common.chatUser));
        chatInfoModel.setFriendId(common.chatUser.getUid());
        chatInfoModel.setCreateName(common.getName(common.currentUser));

        //only text
        if(isPicture){
            chatInfoModel.setLastMassage("<Image>");
        }
        else {
            chatInfoModel.setLastMassage(chatMassageModel.getContent());
        }
        chatInfoModel.setLastUpdate(estimateTimeInMS);
        chatInfoModel.setCreateDate(estimateTimeInMS);

        //submit on firebase
        FirebaseDatabase.getInstance().getReference(common.CHAT_LIST_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(common.chatUser.getUid())
                .setValue(chatInfoModel)
                .addOnFailureListener(e -> {
                    Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                })
                .addOnSuccessListener(aVoid -> {
                    // submit success for chatInfo
                    // copy to friend chat list
                    FirebaseDatabase.getInstance().getReference(common.CHAT_LIST_REF)
                            .child( common.chatUser.getUid())
                            .child(  FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .setValue(chatInfoModel)
                            .addOnFailureListener(e -> {
                                Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            })
                            .addOnSuccessListener(aVoid1 -> {
                                // add on chat ref
                                String roomId=common.GenerateChatRoomId(common.chatUser.getUid(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                                chatRef.child(roomId)
                                       .child(common.CHAT_DETAIL_REF)
                                        .push()
                                        .setValue(chatMassageModel)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(chatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnCompleteListener(task -> {
                                            // clear
                                            ed_chat.setText("");
                                            ed_chat.requestFocus();
                                            if(adapter!=null){
                                                adapter.notifyDataSetChanged();
                                            }
                                            // clear picture
                                            if(isPicture){
                                                fileUri=null;
                                                iv_image.setVisibility(View.GONE);
                                            }
                                            //send Notification
                                            sendNotificationToFriend(chatMassageModel,roomId);
                                        });
                            });
                });

    }

    @Override
    public void onError(String message) {
        Toast.makeText(chatActivity.this,message,Toast.LENGTH_LONG).show();

    }
}