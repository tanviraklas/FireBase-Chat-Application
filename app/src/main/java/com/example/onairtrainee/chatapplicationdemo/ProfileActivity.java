package com.example.onairtrainee.chatapplicationdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendRequestButton;
    private Button declineFriendRequestButton;
    private TextView profileName;
    private TextView profileStatus;
    private CircleImageView profileImage;

    private DatabaseReference userRef;
    private DatabaseReference FriendRequestRef;
    private DatabaseReference FriendsRef;

    private String CURRENT_STATE;

    private FirebaseAuth mAuth;
    private String sender_user_id;
    private String  receiver_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsRef.keepSynced(true);
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        FriendRequestRef.keepSynced(true);
        mAuth =FirebaseAuth.getInstance();



        sender_user_id = mAuth.getCurrentUser().getUid();
        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();



        sendFriendRequestButton = (Button) findViewById(R.id.profile_visit_send_request_btn);
        declineFriendRequestButton = (Button) findViewById(R.id.profile_visit_request_decline_btn);
        profileName = (TextView) findViewById(R.id.profile_visit_userName);
        profileStatus = (TextView) findViewById(R.id.profile_visit_userStatus);
        profileImage = (CircleImageView) findViewById(R.id.profile_visit_user_image);

        CURRENT_STATE = "not_friends";

        userRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_pro_pic).into(profileImage);


                FriendRequestRef.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {

                                if (dataSnapshot.hasChild(receiver_user_id))
                                {
                                    String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                                    if (req_type.equals("sent"))
                                    {
                                        CURRENT_STATE = "request_sent";
                                        sendFriendRequestButton.setText("Cancel Friend Request");
                                        sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.colorHoloRedLight));

                                        declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        declineFriendRequestButton.setEnabled(false);
                                    }
                                    else if (req_type.equals("received"))
                                    {
                                        CURRENT_STATE = "request_received";
                                        sendFriendRequestButton.setText("Accepet Friend Request");

                                        declineFriendRequestButton.setVisibility(View.VISIBLE);
                                        declineFriendRequestButton.setEnabled(true);

                                        declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                DeclineFriendRequest();
                                            }
                                        });

                                    }

                                }

                             else
                             {
                                 FriendsRef.child(sender_user_id)
                                         .addListenerForSingleValueEvent(new ValueEventListener()
                                         {
                                             @Override
                                             public void onDataChange(DataSnapshot dataSnapshot)
                                             {
                                                 if (dataSnapshot.hasChild(receiver_user_id))
                                                 {
                                                     CURRENT_STATE = "friends";
                                                     sendFriendRequestButton.setText("Unfriend This Person");
                                                     sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.colorHoloRedLight));

                                                     declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                     declineFriendRequestButton.setEnabled(false);
                                                 }
                                             }

                                             @Override
                                             public void onCancelled(DatabaseError databaseError)
                                             {

                                             }
                                         });
                             }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        declineFriendRequestButton.setVisibility(View.INVISIBLE);
        declineFriendRequestButton.setEnabled(false);


        
        if (!sender_user_id.equals(receiver_user_id))
        {
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    sendFriendRequestButton.setEnabled(false);


                    if (CURRENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnfriendAFriend();
                    }
                }
            });
        }

        else
        {
            sendFriendRequestButton.setVisibility(View.INVISIBLE);
            declineFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    public void DeclineFriendRequest()
    {
        FriendRequestRef.child(sender_user_id).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendRequestRef.child(receiver_user_id).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                sendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendRequestButton.setText("Send Friend Request");

                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                declineFriendRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void UnfriendAFriend()
    {
        FriendsRef.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendsRef.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestButton.setText("Send Friend Request");
                                                sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.holoGreenLight));

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsRef.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendsRef.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        FriendRequestRef.child(sender_user_id).child(receiver_user_id)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    FriendRequestRef.child(receiver_user_id).child(sender_user_id)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                sendFriendRequestButton.setEnabled(true);
                                                                CURRENT_STATE = "friends";
                                                                sendFriendRequestButton.setText("Unfriend This Person");
                                                                sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.colorHoloRedLight));

                                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                declineFriendRequestButton.setEnabled(false);
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

    private void CancelFriendRequest()
    {
        FriendRequestRef.child(sender_user_id).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FriendRequestRef.child(receiver_user_id).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                sendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendRequestButton.setText("Send Friend Request");
                                sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.holoGreenLight));

                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                declineFriendRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendFriendRequestToPerson()
    {
       FriendRequestRef.child(sender_user_id)
               .child(receiver_user_id)
               .child("request_type")
               .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task)
           {
               if (task.isSuccessful())
               {
                   FriendRequestRef.child(receiver_user_id).child(sender_user_id)
                           .child("request_type").setValue("received")
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful())
                                   {
                                       sendFriendRequestButton.setEnabled(true);
                                       CURRENT_STATE = "request_sent";
                                       sendFriendRequestButton.setText("Cancel Friend Request");
                                       sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.colorHoloRedLight));

                                       declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                       declineFriendRequestButton.setEnabled(false);

                                   }

                               }
                           });
               }
           }
       });
    }
}
