package com.example.digitalcookbook;

import java.util.HashMap;

public class Recipe {
  private String image;
  private String imageFileName;
  private HashMap<String, String> ingredients;
  private HashMap<String, String> steps;
  private String title;
  private String category;

  public Recipe() {}

  public Recipe(
      String image,
      String imageFileName,
      HashMap<String, String> ingredients,
      HashMap<String, String> steps,
      String title,
      String category) {
    this.image = image;
    this.imageFileName = imageFileName;
    this.ingredients = ingredients;
    this.steps = steps;
    this.title = title;
    this.category = category;
  }

  public String getImage() {
    return image;
  }

  public String getImageFileName() {
    return imageFileName;
  }

  public HashMap<String, String> getIngredients() {
    return ingredients;
  }

  public HashMap<String, String> getSteps() {
    return steps;
  }

  public String getTitle() {
    return title;
  }

  public String getCategory() {
    return category;
  }
}
