package com.example.digitalcookbook;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeListViewHolder extends RecyclerView.ViewHolder {
  TextView recipe_title;
  ImageView recipe_image;

  public RecipeListViewHolder(View itemView) {
    super(itemView);
    recipe_title = (TextView) itemView.findViewById(R.id.row_title);
    recipe_image = (ImageView) itemView.findViewById(R.id.row_image);
  }
}
