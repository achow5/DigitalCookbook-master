package com.example.digitalcookbook;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;

public class Category {

  private String mName;
  private Drawable image;
  private ArrayList<Recipe> mRecipes;

  public Category() {}

  public Category(String name) {
    this.mName = name;
  }

  public Category(String name, ArrayList<Recipe> recipes) {
    this.mName = name;
    this.mRecipes = recipes;
  }

  public String getmName() {
    return mName;
  }

  public ArrayList<Recipe> getmRecipes() {
    return mRecipes;
  }
}
