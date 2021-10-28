package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.example.chatapp.Adapter.myViewPagerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;

public class homeActivity extends AppCompatActivity {
@BindView(R.id.homeActivity_tabL)
    TabLayout tabLayout;
@BindView(R.id.homeActivity_vp)
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setupViewPager();

        //get token
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {

            Log.d("TOKEN:",s);
        });
    }

    private void setupViewPager() {
      viewPager.setOffscreenPageLimit(2);
      viewPager.setAdapter(new myViewPagerAdapter(getSupportFragmentManager(), new Lifecycle() {
          @Override
          public void addObserver(@NonNull LifecycleObserver observer) {

          }

          @Override
          public void removeObserver(@NonNull LifecycleObserver observer) {

          }

          @NonNull
          @Override
          public State getCurrentState() {
              return null;
          }
      }));
      new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
         if(position==0){
             tab.setText("Chat");
         }
         else{
             tab.setText("people");
         }
      }).attach();
    }

    private void init() {
        ButterKnife.bind(this);
    }
}