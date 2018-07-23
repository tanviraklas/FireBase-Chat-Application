package com.example.onairtrainee.chatapplicationdemo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestsList;

    private View myMainView;

    private DatabaseReference FriendsReqRef;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendReqDatabaseRef;

    String online_user_id;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestsList = (RecyclerView) myMainView.findViewById(R.id.request_list);


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(online_user_id);
        FriendsReqRef.keepSynced(true);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.keepSynced(true);
        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsDatabaseRef.keepSynced(true);
        FriendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        FriendReqDatabaseRef.keepSynced(true);





        myRequestsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestsList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return myMainView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Requests,RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
                (
                        Requests.class,
                        R.layout.friend_request_all_user_layout,
                        RequestsFragment.RequestViewHolder.class,
                        FriendsReqRef
                )
        {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, final int position)
            {
                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {

                                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {

                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setUserStatus(userStatus);
                                        viewHolder.setThumbImage(thumbImage);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Accept Friend Request",
                                                                "Cancel Friend Request"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Req Options");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int position)
                                                    {
                                                        if (position == 0)
                                                        {
                                                            Calendar calForDate = Calendar.getInstance();
                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                                                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                            FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid)
                                                                                        {
                                                                                            FriendReqDatabaseRef.child(online_user_id).child(list_user_id)
                                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if (task.isSuccessful())
                                                                                                    {
                                                                                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                                            {
                                                                                                                if (task.isSuccessful())
                                                                                                                {

                                                                                                                    Toast.makeText(getContext(),"Friend Request Accepted Successfully",Toast.LENGTH_SHORT).show();

                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }


                                                        if (position == 1)
                                                        {
                                                            FriendReqDatabaseRef.child(online_user_id).child(list_user_id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(),"Friend Request Declined Successfully",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }



                                                    }
                                                });

                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });

                            }
                            else if (request_type.equals("sent"))
                            {


                                Button req_accept_button = viewHolder.mView.findViewById(R.id.request_accept_btn);
                                req_accept_button.setText("Req Sent");
                                viewHolder.mView.findViewById(R.id.request_decline_btn).setVisibility(View.INVISIBLE);

                                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {

                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setUserStatus(userStatus);
                                        viewHolder.setThumbImage(thumbImage);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {

                                                CharSequence options[] =  new CharSequence[]
                                                        {
                                                                "Cancel Friend Request"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Req Sent");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int position)
                                                    {

                                                        if (position == 0)
                                                        {

                                                            FriendReqDatabaseRef.child(online_user_id).child(list_user_id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(),"Friend Request Canceled Successfully",Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }

                                                    }
                                                });

                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        viewHolder.setUserName(userName);
                        viewHolder.setUserStatus(userStatus);
                        viewHolder.setThumbImage(thumbImage);




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            }
        };

        myRequestsList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage)
        {
            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.request_profile_image);



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

        public void setUserStatus(String userStatus)
        {
            TextView user_status = (TextView) mView.findViewById(R.id.request_profile_status);
            user_status.setText(userStatus);
        }

    }




}
