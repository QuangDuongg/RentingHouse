package com.example.swipe.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyChatsFragment();
            case 1:
                return new UsersFragment();
            default:
                return new MyChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng tab
    }
}
