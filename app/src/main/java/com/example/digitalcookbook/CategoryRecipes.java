package com.example.digitalcookbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CategoryRecipes extends AppCompatActivity {
  RecyclerView recView;
  RecipeAdapter adapter;
  ArrayList<Recipe> recipeList = new ArrayList<>();
  DbHelper helper;
  String category;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.cat_recipe_list);

    // Get category chosen from bundle
    Bundle extras = getIntent().getExtras();
    final int cat = extras.getInt("com.example.digitalcookbook.Category");
    category = setCategory(cat);
    Log.d(
        "com.example.digitalcookbook.Category",
        "com.example.digitalcookbook.Category is: " + category);

    // RecyclerView
    recView = (RecyclerView) findViewById(R.id.recycler_view);
    recView.setLayoutManager(new LinearLayoutManager(this));

    // Firebase
    // Get a reference to our Recipes
    final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference(category);

    // Attach a listener to read the data on change, updates recyclerview
    ref.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            recipeList.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
              Recipe recipe = ds.getValue(Recipe.class);
              recipeList.add(recipe);
            }
            adapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
          }
        });
    helper = new DbHelper(ref);
    adapter = new RecipeAdapter(this, recipeList);
    recView.setAdapter(adapter);
  }

  public String setCategory(int num) {
    String category = "";
    switch (num) {
      case 0:
        category = "Appetizers and Snacks";
        break;
      case 1:
        category = "Breakfast and Brunch";
        break;
      case 2:
        category = "Dessert";
        break;
      case 3:
        category = "Dinner";
        break;
      case 4:
        category = "Drinks";
        break;
      case 5:
        category = "Lunch";
        break;
    }
    return category;
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
        Intent intent = new Intent(CategoryRecipes.this, viewFavorites.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
