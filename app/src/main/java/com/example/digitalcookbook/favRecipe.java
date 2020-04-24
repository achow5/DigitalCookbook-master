package com.example.digitalcookbook;


public class favRecipe {
    private long mId;
    private String imageFileName;
    private String ingredients;
    private String steps;
    private String title;


    /*
    public favRecipe() {}

    public favRecipe(
            long mId,
            String imageFileName,
            String ingredients,
            String steps,
            String title) {
        this.mId = mId;
        this.imageFileName = imageFileName;
        this.ingredients = ingredients;
        this.steps = steps;
        this.title = title;

    }
     */

    public long getmId() { return mId; }

    public String getImageFileName() {
        return imageFileName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) { mId = id; }

    public void setImageFileName(String image) {
        this.imageFileName = image;
    }

    public void setIngredients(String ing) {
        this.ingredients = ing;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
