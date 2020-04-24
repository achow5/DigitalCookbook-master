package com.example.digitalcookbook;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.os.Vibrator;
import androidx.core.view.GestureDetectorCompat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {
  private DatabaseReference mDatabase;
  ArrayList<String> category_names = new ArrayList<>();
  GridLayout mainGrid;

  SensorManager sensorManager;
  Sensor accelerometer;
  Sensor gyroscope;
  Vibrator vibrator;

  private final String TAG = "GestureDemo";
  private GestureDetectorCompat mDetector;

  float threshold;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    mainGrid = findViewById(R.id.grid);

    // Firebase
    // Get a reference to database
    mDatabase = FirebaseDatabase.getInstance().getReference();

    // Accelerometer and Vibration
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
      accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
      threshold = accelerometer.getMaximumRange() / 8;
    }
    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    mDetector =
        new GestureDetectorCompat(
            this,
            new GestureDetector.OnGestureListener() {
              @Override
              public boolean onDown(MotionEvent e) {
                return false;
              }

              @Override
              public void onShowPress(MotionEvent e) {}

              @Override
              public boolean onSingleTapUp(MotionEvent e) {
                return false;
              }

              @Override
              public boolean onScroll(
                  MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
              }

              @Override
              public void onLongPress(MotionEvent e) {}

              @Override
              public boolean onFling(
                  MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
              }
            });

    // Read in categories and assign to categories list
    mDatabase.addChildEventListener(
        new ChildEventListener() {
          @Override
          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.d("NAMES", dataSnapshot.getKey());
            category_names.add(dataSnapshot.getKey());
          }

          @Override
          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

          @Override
          public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

          @Override
          public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    // Attach a listener to read the data on change, updates recyclerview
    mDatabase.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            // Set onClickListeners for category cards and display names
            setSingleEvent(mainGrid);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
          }
        });

  }

  private void setSingleEvent(GridLayout mainGrid) {

    // Set the TextView layoutParams
    // Print category names
    // Initialize a new TextView to put in CardView
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.leftMargin = 16;
    // Set onClickListeners for each CardView
    for (int i = 0; i < mainGrid.getChildCount(); i++) {
      CardView cardView = (CardView) mainGrid.getChildAt(i);

      // Set the TextView dynamically
      TextView tv = new TextView(this);
      tv.setLayoutParams(params);
      tv.setText(category_names.get(i));
      tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
      tv.setTextColor(Color.BLACK);
      tv.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
      cardView.addView(tv);

      //DATA IS IN THE DATABASE, YOU NEED TO ADD A FAVORITES NODE TO FIREBASE, WHEN CLICKED IT WILL
        // SEND AN INTENT TO CategoryRecipes.class, WHEN THAT ACTIVITY BEGINS, CHECK THE INTENT,
        // IF THE INTENT CONTAINS "FAVORITES", START AN ACTIVITY TO DISPLAY WHATS IN THE SQLITE DB

      final int selected = i;
      cardView.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(MainActivity.this, CategoryRecipes.class);
              intent.putExtra("com.example.digitalcookbook.Category", selected);
              startActivity(intent);
            }
          });
    }
  }

  private class MyGestureListener implements GestureDetector.OnGestureListener {
    @Override
    public boolean onDown(MotionEvent e) {
      Log.d(TAG, "onDown");
      return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
      Log.d(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      Log.d(TAG, "onSingleTapUp");
      return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      Log.d(TAG, "onScroll");
      return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      Log.d(TAG, "onFling");
      return false;
    }
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.goToFavorite:
                Intent intent = new Intent(MainActivity.this, viewFavorites.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
