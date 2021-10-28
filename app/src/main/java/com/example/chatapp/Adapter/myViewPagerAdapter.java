package com.example.chatapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatapp.Fragments.chatFragment;
import com.example.chatapp.Fragments.peopleFragment;

public class myViewPagerAdapter extends FragmentStateAdapter {
    public myViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position==0){
            return chatFragment.getInstance();
        }
        else return peopleFragment.getInstance();

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
