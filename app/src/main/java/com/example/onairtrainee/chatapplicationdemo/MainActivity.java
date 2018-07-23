package com.example.onairtrainee.chatapplicationdemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UserReference;
    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid() ;
            UserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("DemoChat");


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            LogOutUser();
        }
        else if (currentUser != null)
        {
            UserReference.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop()
    {
        currentUser = mAuth.getCurrentUser();
        super.onStop();
        if (currentUser != null)
        {
            UserReference.child("online").setValue(false);
        }
    }

    private void LogOutUser() {

        Intent startPageIntent = new Intent(MainActivity.this,StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button)
        {
            if (currentUser != null)
            {
                UserReference.child("online").setValue(false );
            }
            mAuth.signOut();
            LogOutUser();
        }

        if (item.getItemId() == R.id.main_account_settings_button)
        {
            Intent settingsIntent =  new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_all_users_button)
        {
            Intent allusersIntent =  new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(allusersIntent);
        }

        return true;

    }
}
