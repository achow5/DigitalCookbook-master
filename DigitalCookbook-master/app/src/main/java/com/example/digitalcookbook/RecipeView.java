package com.example.digitalcookbook;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.lang.StrictMath;

public class RecipeView extends AppCompatActivity implements SensorEventListener {
  TextToSpeech currentStepTTS;
  View ingredientSide;
  View stepsSide;
  Boolean showBack = false;
  Boolean isFavorited = false;
  Button readNextStep;
  Button readPrevStep;
  Button readThisStep;
  Button favoriteButton;
  Button unfavoriteButton;

  private List<favRecipe> mFavList;
  private favoritesDB mFavDB;

  List<String> steps = new ArrayList<String>();
  int currentStepNum = 0;

  SensorManager sensorManager;
  Sensor accelerometer;
  Vibrator vibrator;
  private final String TAG = "GestureDemo";
  private GestureDetectorCompat mDetector;
  private static final int SHAKE_THRESHOLD = 12;
  long lastUpdate;
  float threshold;
  float prevX;
  float prevY;
  float prevZ;

  boolean buttonPressed = false;
  long timeSinceLastTTS = System.currentTimeMillis();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe);
    

    mFavDB = favoritesDB.getInstance(getApplicationContext());

    readNextStep = (Button) findViewById(R.id.readNextStep);
    readPrevStep = (Button) findViewById(R.id.readPrevStep);
    readThisStep = (Button) findViewById(R.id.readThisStep);
    unfavoriteButton = (Button) findViewById(R.id.unfavorite);
    favoriteButton = (Button) findViewById(R.id.favorite);
    ingredientSide = (View) findViewById(R.id.front_recipe);
    stepsSide = findViewById(R.id.back_recipe);
    TextView ShowIngs = (TextView) findViewById(R.id.ingredientsList);
    currentStepTTS =
        new TextToSpeech(
            getApplicationContext(),
            new TextToSpeech.OnInitListener() {

              @Override
              public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                  currentStepTTS.setLanguage(Locale.UK);
                }
              }
            });

    Intent intent = getIntent();
    HashMap<String, String> IngHashMap =
        (HashMap<String, String>) intent.getSerializableExtra("IngHashMap");

    final String ing = IngHashMap.toString();

    final HashMap<String, String> StepHashMap =
        (HashMap<String, String>) intent.getSerializableExtra("StepHashMap");

    final String step = StepHashMap.toString();

    String imageFileName = (String) intent.getSerializableExtra("ImageFileName");
    final String image = imageFileName;
    final String title = (String) intent.getSerializableExtra("Title");

    for (Map.Entry<String, String> entry : IngHashMap.entrySet()) {
      ShowIngs.setText(entry.getValue() + "\n" + ShowIngs.getText());
    }

    TextView ShowSteps = (TextView) findViewById(R.id.stepsList);
    int count = 1;
    String stepsText = "";
    ArrayList<String> stepsList = new ArrayList<String>();
    ArrayList<String> stepsListTextToSpeech = new ArrayList<String>();
    for (Map.Entry<String, String> entry : StepHashMap.entrySet()) {
      stepsList.add(entry.getValue() + "\n");
      stepsListTextToSpeech.add(entry.getValue() + "\n");
    }
    while (!stepsList.isEmpty()) {
      stepsText += count + ". " + stepsList.get(stepsList.size() - 1) + "\n";
      stepsList.remove(stepsList.size() - 1);
      count++;
    }
    while (!stepsListTextToSpeech.isEmpty()) {
      steps.add(stepsListTextToSpeech.get(stepsListTextToSpeech.size() - 1));
      stepsListTextToSpeech.remove(stepsListTextToSpeech.size() - 1);
    }
    ShowSteps.setText(stepsText);
    ImageView img = (ImageView)findViewById(R.id.recipe_image);
    imageFileName = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
    int id = getResources().getIdentifier(imageFileName, "drawable", getPackageName());
    img.setImageResource(id);

    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        threshold = accelerometer.getMaximumRange()/8;
    }
    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
      accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

    readNextStep.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (currentStepNum < steps.size() - 1) {
              timeSinceLastTTS = System.currentTimeMillis();
              currentStepNum++;
              if (currentStepTTS.isSpeaking()) {
                currentStepTTS.stop();
              }
              String toSpeak = steps.get(currentStepNum);
              currentStepTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
          }
        });

    readPrevStep.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (currentStepNum > 0) {
              timeSinceLastTTS = System.currentTimeMillis();
              currentStepNum--;
              if (currentStepTTS.isSpeaking()) {
                currentStepTTS.stop();
              }
              String toSpeak = steps.get(currentStepNum);
              currentStepTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
          }
        });

    readThisStep.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            buttonPressed = true; // turns on sensor
            timeSinceLastTTS = System.currentTimeMillis();
            if (currentStepTTS.isSpeaking()) {
              currentStepTTS.stop();
            }
            String toSpeak = steps.get(currentStepNum);
            currentStepTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
          }
        });

    unfavoriteButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!isFavorited) {
              favoriteButton.setVisibility(View.VISIBLE);
              isFavorited = true;
            } else {
              favoriteButton.setVisibility(View.GONE);
            }

            favRecipe recipe = new favRecipe();
            recipe.setTitle(title);
            recipe.setImageFileName(image);
            recipe.setIngredients(ing);
            recipe.setSteps(step);
            mFavDB.addFavRecipe(recipe);
          }
        });

    favoriteButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                if (isFavorited) {
                  favoriteButton.setVisibility(View.GONE);
                  isFavorited = false;
                } else {
                  favoriteButton.setVisibility(View.VISIBLE);
                }
                deleteQuestion(image);
              }
            });

  }

  public void flipper(View v) {
    currentStepTTS.stop();
    if (showBack) {
      ingredientSide.setVisibility(View.VISIBLE);
      stepsSide.setVisibility(View.GONE);
      showBack = false;
    } else {
      ingredientSide.setVisibility(View.GONE);
      stepsSide.setVisibility(View.VISIBLE);
      showBack = true;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
  }

  public void onPause() {
    if (currentStepTTS != null) {
      currentStepTTS.stop();
      currentStepTTS.shutdown();
    }
    sensorManager.unregisterListener(this);
    super.onPause();
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
  public void onSensorChanged(SensorEvent event) {
    float ax, ay, az;

    ax = event.values[0];
    ay = event.values[1];
    az = event.values[2];

    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      if (buttonPressed == true) {
        long currTime = System.currentTimeMillis();
        // check in intervals of 25 milliseconds
        if ((currTime - lastUpdate) > 50) {
          long diffTime = (currTime - lastUpdate);
          lastUpdate = currTime;

          // float speed = Math.abs(ax + ay + az - prevX - prevY - prevZ) / diffTime * 10000;
          float speed = Math.abs(ax + az - prevX - prevZ) / diffTime * 10000;

          if (speed > SHAKE_THRESHOLD & currTime - timeSinceLastTTS > 4000 & currentStepNum < steps.size() - 1) {
            currentStepNum++;
            String toSpeak = steps.get(currentStepNum);
            currentStepTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

            Toast.makeText(this, "Next Step", Toast.LENGTH_SHORT).show();
            timeSinceLastTTS = currTime;
            //buttonPressed = false;
          }

          prevX = ax;
          prevY = ay;
          prevZ = az;
        }
      }
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
        Intent intent = new Intent(RecipeView.this, viewFavorites.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private favRecipe mDeletedRecipe;


  private void deleteQuestion(String image) {
    if (true) {
      mFavDB.deleteFav(image);
    }
  }


  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
