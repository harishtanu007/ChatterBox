package com.harish.hk185080.chatterbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.harish.hk185080.chatterbox.Services.NetworkChangeReceiver;
import com.harish.hk185080.chatterbox.data.MyData;
import com.harish.hk185080.chatterbox.utils.FireMessage;
import com.harish.hk185080.chatterbox.utils.NetworkUtil;
import com.harish.hk185080.chatterbox.utils.RevealAnimation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.tiancaicc.springfloatingactionmenu.MenuItemView;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.harish.hk185080.chatterbox.data.Constants.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private FirebaseAuth mAuth;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    public static CoordinatorLayout rootLayout;
    public static View Header;
    public static AppBarLayout appBarLayout;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private MyData myData;
    private static final String TAG = "MainActivity";
    String CHANNEL_ID = "MESSAGES";


    TabLayout tabLayout;
    ViewPager mpager;
    Toolbar toolbar;
    FirebaseUser currentUser;
    Menu menu;
    ImageView notfound;


    private DatabaseReference mUserRef;

    GoogleSignInClient mGoogleSignInClient;
    NavigationView navigationView;
    CircleImageView circleImageView;
    public final static String AUTH_KEY_FCM = "AIzaSyA0FB_ByKW7-UIGhLzpE4E0NpROWccjwbs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    public String deviceToken;

    public void init() {

        //Firebase notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

        }
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootlayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setTitleTextColor(this.getResources().getColor(R.color.invertcolor));
        setTitle("Chats");
        tabLayout = (TabLayout) findViewById(R.id.mainTabLayout);


        mpager = (ViewPager) findViewById(R.id.viewpagermain);

        if (mpager != null && tabLayout != null) {

            mpager.setOffscreenPageLimit(2);

            mpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                @Override
                public Fragment getItem(int position) {
                    switch (position % 3) {
                        case 0:

                            ChatsFragment chatsFragment = new ChatsFragment();
                            return chatsFragment;

                        case 1:

                            FavouritesFragment favouritesFragment = new FavouritesFragment();
                            return favouritesFragment;

                        case 2:

                            FriendsFragment friendsFragment = new FriendsFragment();
                            return friendsFragment;

                        default:
                            return null;
                    }
                }

                @Override
                public int getCount() {
                    return 3;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position % 3) {
                        case 0:
                            return "";
                        case 1:
                            return "";
                        case 2:
                            return "";


                    }
                    return "";
                }
            });
            mpager
                    .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageSelected(int position) {
                            // TODO Auto-generated method stub
                            switch (position) {
                                case 0:

                                    setTitle("Chats");
                                    break;
                                case 1:

                                    setTitle("Favourites");
                                    break;
                                case 2:

                                    setTitle("Friends");
                                    break;
                                default:
                                    return;
                            }

                        }

                        @Override
                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onPageScrollStateChanged(int pos) {
                            // TODO Auto-generated method stub

                        }
                    });
            tabLayout.setupWithViewPager(mpager);

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_border_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_outline_white_24dp);


            tabLayout.setOnTabSelectedListener(
                    new TabLayout.ViewPagerOnTabSelectedListener(mpager) {
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            super.onTabSelected(tab);
                            RecyclerView recyclerView;
                            int numTab = tab.getPosition();
                            //  if (fab != null)
                            //    animateIn(fab);

                            switch (numTab) {
                                case 0:

                                    recyclerView = (RecyclerView) findViewById(R.id.conv_list);
                                    setTitle("Chats");
                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;
                                case 1:
                                    setTitle("friends");
                                    recyclerView = (RecyclerView) findViewById(R.id.friends_list);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;
                                case 2:
                                    setTitle("fav");
                                    recyclerView = (RecyclerView) findViewById(R.id.conv_list);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;

                            }
                            if (appBarLayout != null)
                                appBarLayout.setExpanded(true, true);

                        }

                    });


        }


        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Move to Profile Activity
        Bundle extras = getIntent().getExtras();
        String userId;
        String notificationType;
        String userName;
        if (extras != null) {
            userId = extras.getString("user_id");
            notificationType = extras.getString("type");
            userName=extras.getString("user_name");
            if(notificationType!=null)
            {
                if(notificationType.equals("request"))
                {
                    sendToProfile(userId);
                }
                else if(notificationType.equals("message"))
                {
                    sendToChat(userId,userName);
                }
            }

        }

        myData = new MyData();

        if(!myData.isInternetConnected(MainActivity.this))
        {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        Fabric.with(this, new Crashlytics());
        appBarLayout = (AppBarLayout) findViewById(R.id.mainappbar);
        //notfound = (ImageView) findViewById(R.id.notfound);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startRevealActivity(view);
                startActivity(new Intent(MainActivity.this, ChatListActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // startActivity(new Intent(MainActivity.this, ChatMain.class));
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            init();
            Header = navigationView.getHeaderView(0);
            final TextView textView = (TextView) Header.findViewById(R.id.name);
            final TextView textView1 = (TextView) Header.findViewById(R.id.sub);

            final TextView textView2 = (TextView) Header.findViewById(R.id.score);
            circleImageView = (CircleImageView) Header.findViewById(R.id.CimageView);


            if (mAuth.getCurrentUser() != null) {
                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }
            Header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUserPage();
                }
            });

            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            receiver = new NetworkChangeReceiver();

            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            final String current_uid = mCurrentUser.getUid();

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
            mUserDatabase.keepSynced(true);
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {


                        String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        textView.setText(name);
                        textView1.setText(status);
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);
                        myRef.keepSynced(true);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // textView2.setText(dataSnapshot.getChildrenCount()+"");
                                textView2.setText(getString(R.string.friends, dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
//                        myRef.addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                                //textView2.setText(getString(R.string.friends, dataSnapshot.getChildrenCount()));
//                                textView2.setText(dataSnapshot.getChildrenCount()+"");
//                            }
//
//                            @Override
//                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

                        if (!image.equals("default")) {
                            Glide
                                    .with(getApplicationContext())
                                    .load(image)
                                    .into(circleImageView);

                        } else {
                            circleImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_account_circle_white_48dp));
                        }
                    } catch (Exception e) {
                        //sendToStart();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Snackbar.make(rootLayout, "There was a problem Logging in,Please Try again!", Snackbar.LENGTH_LONG).show();
            sendToStart();
        }

    }

    private void sendToChat(String userId,String userName) {
        Intent chatIntent = new Intent(MainActivity.this, ChatOpenActivity.class);
        chatIntent.putExtra("user_id", userId);
        chatIntent.putExtra("user_name",userName);
        startActivity(chatIntent);
    }

    private void sendToProfile(String userId) {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.putExtra("user_id", userId);
        startActivity(profileIntent);
    }

    private void openUserPage() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {

            sendToStart();

        } else {
            mUserRef.child("online").setValue("true");
            addToken();
            //Snackbar.make(rootLayout,token,Snackbar.LENGTH_LONG).show();


        }
    }

    private void addToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
                        mUserRef.child("device_token").setValue(msg);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_account_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        } else if (item.getItemId() == R.id.menu_all_users) {
            Intent allUsersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(allUsersIntent);
        } else if (item.getItemId() == R.id.share_app_button) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Ping Me");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.harish.hk185080.chatterbox\n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        } else if (item.getItemId() == R.id.main_feedback_button) {
            feedback();
        } else if (item.getItemId() == R.id.main_logout_button) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder
                    .setMessage("Are you sure you want to Log out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with logout
                            signOut();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                                FirebaseAuth.getInstance().signOut();
                                sendToStart();

                            }


                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        return true;
    }

    private void feedback() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }


        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.feedback_dialog, (ViewGroup) findViewById(android.R.id.content), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.feedback_title);
        final EditText messageInput = (EditText) viewInflated.findViewById(R.id.feedback_description);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(viewInflated)
                .setTitle("Please provide your valuable feedback here")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //Dismiss once everything is OK.
                        if (myData.isInternetConnected(MainActivity.this)) {
                            final String inputString = input.getText().toString();
                            final String messageInputString = messageInput.getText().toString();
                            if (!TextUtils.isEmpty(inputString) && !TextUtils.isEmpty(messageInputString)) {
                                dialog.dismiss();
                                final String feedbackTitle = input.getText().toString().trim();
                                String feedbackDescription = messageInput.getText().toString().trim();
                                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, feedbackTitle);
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedbackDescription);


                                emailIntent.setType("message/rfc822");

                                try {
                                    startActivity(Intent.createChooser(emailIntent,
                                            "Send email using..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Snackbar.make(rootLayout, "No email clients installed.", Snackbar.LENGTH_LONG).show();
                                }
                            } else if (TextUtils.isEmpty(inputString)) {
                                Snackbar.make(rootLayout, "Title cannot be Empty", Snackbar.LENGTH_LONG).show();
                            } else if (TextUtils.isEmpty(messageInputString)) {
                                Snackbar.make(rootLayout, "Description cannot be Empty", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.myaccount) {
            openUserPage();
//            if (Profile.getCurrentProfile() != null) {
//                Intent intent = new Intent(this, User.class);
//                intent.putExtra("id", Profile.getCurrentProfile().getId());
//                startActivity(intent);
//            } else {
//
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }
            return false;
        } else if (id == R.id.share) {
            shareApp();
            return false;
//
//            String text = "Check out new app Questo : \n " + "https://play.google.com/store/apps/details?id=com.tdevelopers.questo";
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//            BottomSheet sheet = BottomSheetHelper.shareAction(this, shareIntent).title("Share App").build();
//            sheet.show();


//        } else if (id == R.id.invite) {
//
////            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
////                    .setMessage("Questo App Invite")
////                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
////                    .setCallToActionText(getString(R.string.invitation_cta))
////                    .build();
////
////
////            startActivityForResult(intent, REQUEST_INVITE);
//            return false;
//        }
        } else if (id == R.id.aboutus) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return false;
        }
        //else if (id == R.id.favourites) {
//            if (Profile.getCurrentProfile() != null)
//                startActivity(new Intent(MainActivity.this, Favorite_Activity.class));
//            else {
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }

        //}
        else if (id == R.id.logout_button) {
            logout();
            return false;
        } else if (id == R.id.allUsers) {
            openAllUsers();
            return false;
//            if (Profile.getCurrentProfile() != null) {
//                startActivity(new Intent(MainActivity.this, ChatMain.class));
//            } else {
//
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }
        } else if (id == R.id.myRequests) {
            Intent i = new Intent(MainActivity.this, Request_Activity.class);
            startActivity(i);
            return false;

        }
//        else if (id == R.id.how) {
//            //startActivity(new Intent(MainActivity.this, Introduction.class));
//            return false;
//        }
        else if (id == R.id.feedback) {

            feedback();

            //startActivity(new Intent(MainActivity.this, Introduction.class));
            return false;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }


    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder
                .setMessage("Are you sure you want to Log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        signOut();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            mUserRef.child("device_token").setValue(null);
                            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                            FirebaseAuth.getInstance().signOut();
                            sendToStart();
                        }


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Ping Me");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.harish.hk185080.chatterbox\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAllUsers() {
        Intent allUsersIntent = new Intent(MainActivity.this, UsersActivity.class);
        startActivity(allUsersIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else
            {
                finish();
            }
        }


    }

    @Override
    public void onClick(View v) {

    }

    private void startRevealActivity(View v) {
        //calculates the center of the View v you are passing
        int revealX = (int) (v.getX() + v.getWidth() / 2);
        int revealY = (int) (v.getY() + v.getHeight() / 2);

        //create an intent, that launches the second activity and pass the x and y coordinates
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(this, intent, null);

        //to prevent strange behaviours override the pending transitions
        overridePendingTransition(0, 0);
    }


}
