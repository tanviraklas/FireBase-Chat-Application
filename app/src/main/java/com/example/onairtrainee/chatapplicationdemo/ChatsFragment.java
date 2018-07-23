package com.example.onairtrainee.chatapplicationdemo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment
{
    private RecyclerView myChatsList;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;

    String online_user_id;


    private View myMainView;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        myChatsList = (RecyclerView) myMainView.findViewById(R.id.chats_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        FriendsReference.keepSynced(true);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);

        myChatsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        FirebaseRecyclerAdapter<Chats,ChatsFragment.ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>
                (
                        Chats.class,
                        R.layout.all_users_display_layout,
                        ChatsFragment.ChatsViewHolder.class,
                        FriendsReference
                )
        {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, Chats model, int position) {



                final String list_user_id = getRef(position).getKey();

                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        if (dataSnapshot.hasChild("online"))
                        {
                            Boolean online_status = (boolean) dataSnapshot.child("online").getValue();
                            viewHolder.setUserOnline(online_status);
                        }

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage);
                        viewHolder.setUserStatus(userStatus);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                Intent ChatIntent = new Intent(getContext(),ChatActivity.class);
                                ChatIntent.putExtra("visit_user_id",list_user_id);
                                ChatIntent.putExtra("user_name",userName);
                                startActivity(ChatIntent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        myChatsList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }



        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage)
        {
            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);



            Picasso.get().load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_pro_pic).into(thumb_image, new Callback() {
                @Override
                public void onSuccess()
                {

                }

                @Override
                public void onError(Exception e)
                {
                    Picasso.get().load(thumbImage).placeholder(R.drawable.default_pro_pic).into(thumb_image);
                }
            });
        }

        public void setUserOnline(Boolean online_status)
        {
            ImageView onlineStatusView = (ImageView) mView.findViewById(R.id.online_status);

            if (online_status == false)
            {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
            else
            {
                onlineStatusView.setVisibility(View.VISIBLE);
            }
        }

        public void setUserStatus(String userStatus)
        {
            TextView user_status = (TextView) mView.findViewById(R.id.all_users_status);
            user_status.setText(userStatus);
        }
    }
}
