package com.example.digitalcookbook;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class DbHelper {

  DatabaseReference db;
  Boolean saved = null;
  ArrayList<Recipe> recipeList = new ArrayList<>();

  public DbHelper(DatabaseReference db) {
    this.db = db;
  }

  // Create new Recipe
  public Boolean save(Recipe recipe) {
    if (recipe == null) {
      saved = false;
    } else {
      try {
        db.child(recipe.getCategory()).push().setValue(recipe);
        saved = true;
      } catch (DatabaseException e) {
        e.printStackTrace();
        saved = false;
      }
    }

    return saved;
  }

  // Read from database
  public ArrayList<Recipe> read() {
    db.addChildEventListener(
        new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
            fetchData(dataSnapshot);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, @Nullable String s) {
            fetchData(dataSnapshot);
          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {}

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

          @Override
          public void onCancelled(DatabaseError databaseError) {}
        });

    return recipeList;
  }

  // Read data
  private void fetchData(DataSnapshot dataSnapshot) {
    recipeList.clear();
    for (DataSnapshot ds : dataSnapshot.getChildren()) {
      Recipe recipe = ds.getValue(Recipe.class);
      recipeList.add(recipe);
    }
  }
}
