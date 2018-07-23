package com.example.onairtrainee.chatapplicationdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by onAir Trainee on 23-May-18.
 */

class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 1:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment ;


            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 1:
                return "Requests";

            case 0:
                return "Chats";

            case 2:
                return "Friends";

            default:
                return null;
        }
    }
}
