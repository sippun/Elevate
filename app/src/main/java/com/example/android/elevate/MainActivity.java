package com.example.android.elevate;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivityTag";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static boolean moodPromptSet = false;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    //handles all firebase related things
    public static DataBase database = new DataBase();

    //RNG for generating notification id of each item
    Random rand = new Random();
    public static int currentPage = R.id.nav_home;
    static NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(!moodPromptSet) {
            //make mood prompts show at two customizable times of the day
            createMoodPrompt(12, 0);
            createMoodPrompt(18, 0);
            moodPromptSet = true;
        }

        mAuth = FirebaseAuth.getInstance();
        userLogin(mAuth);
        mAuthListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userLogin(firebaseAuth);
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent,1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().
                add(R.id.fragment_container, new ToDoFragment()).commit();
    }

    private void userLogin(FirebaseAuth firebaseAuth){
        Log.d(TAG, "onAuthChanged");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        database.logIntoFirebase();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                    replace(R.id.fragment_container, new CalendarFragment()).
                    addToBackStack(null).commit();
            currentPage = R.id.nav_calendar;

        } else if (id == R.id.nav_home && currentPage != R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                    replace(R.id.fragment_container, new ToDoFragment()).
                    addToBackStack(null).commit();
            currentPage = R.id.nav_home;

        } else if (id == R.id.nav_mood) {
            Intent a = new Intent(MainActivity.this, MoodInputUI.class);
            startActivity(a);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Collect results from AddActivity. Extract item info into time and date variables and
        //create a new todoitem with them.
        //then notify adapter to update list
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");

                if (data.getStringExtra("type").equals("task")) {
                    Calendar time1 = (Calendar) data.getExtras().get("time1");
                    Calendar time2 = (Calendar) data.getExtras().get("time2");
                    boolean[] recurringDays = (boolean[]) data.getExtras().get("recur");
                    int notification_id = rand.nextInt(5000);
                    database.addNewTask(title, time1, time2, recurringDays, notification_id);

                    createNotification(title, time1, notification_id);
                    String msg = title + " created from " + time1.getTime() +" to "+ time2.getTime();
                    Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
                    toast.show();
                } else if (data.getStringExtra("type").equals("habit")) {
                    boolean[] recurringDays = (boolean[]) data.getExtras().get("recur");
                    int notification_id = rand.nextInt(5000);
                    database.addNewHabit(title, recurringDays, notification_id);

//                    createNotification(title, time1);
//                    String msg = title + " created from " + time1.getTime() +" to "+ time2.getTime();
//                    Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
//                    toast.show();
                }

                //getSupportFragmentManager().beginTransaction().
                //        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                //        replace(R.id.fragment_container, new ToDoFragment(time1)).
                //        commit();
            }
        }
    }

    public void createNotification(String title, Calendar startTime, int notification_id){

        // Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction("com.example.android.elevate.MY_NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("id", notification_id);

        // Pending Intent for if user clicks on notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get instance of AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void createMoodPrompt(int hour, int second){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, second);
        // Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(this, MoodNotificationReceiver.class);
        intent.setAction("com.example.android.elevate.MY_NOTIFICATION");

        //assign static notification id
        //avoids creating duplicate notifications when launching main again
        intent.putExtra("id", hour);

        // Pending Intent for if user clicks on notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Toast.makeText(this, "Mood prompt will show at "+hour+":"+second,
                Toast.LENGTH_LONG).show();

        // Get instance of AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    //dismiss active future notifications of given id, usually after task is marked as done
    public static void cancel(int id){
        StatusBarNotification[] notifications = nm.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == id) {
                nm.cancel(id);
            }
        }
    }
}
