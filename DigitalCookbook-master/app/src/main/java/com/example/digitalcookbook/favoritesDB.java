package com.example.digitalcookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

public class favoritesDB extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "fav.db";

    private static favoritesDB mFavDb;

    public synchronized static favoritesDB getInstance(Context context) {
        if (mFavDb == null) {
            mFavDb = new favoritesDB(context);
            mFavDb.setWriteAheadLoggingEnabled(false);
        }
        return mFavDb;
    }

    private favoritesDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class FavoritesTable {
        private static final String TABLE = "favorites";
        private static final String COL_ID = "_id";
        private static final String COL_TITLE = "title";
        private static final String COL_ING = "ing";
        private static final String COL_STEPS = "steps";
        private static final String COL_IMAGE = "image";
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create questions table with foreign key that cascade deletes
        String CREATE_TABLE = "create table " + FavoritesTable.TABLE + " (" +
                FavoritesTable.COL_ID + " integer primary key autoincrement, " +
                FavoritesTable.COL_TITLE + ", " +
                FavoritesTable.COL_ING + ", " +
                FavoritesTable.COL_STEPS + ", " +
                FavoritesTable.COL_IMAGE + " );";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + FavoritesTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }



    public List<favRecipe> getFavs() {
        List<favRecipe> favs = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from " + FavoritesTable.TABLE,null);
        if (cursor.moveToFirst()) {
            do {
                favRecipe recipe = new favRecipe();
                recipe.setId(cursor.getInt(0));
                recipe.setTitle(cursor.getString(1));
                recipe.setIngredients(cursor.getString(2));
                recipe.setSteps(cursor.getString(3));
                recipe.setImageFileName(cursor.getString(4));
                favs.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return favs;
    }

    public favRecipe getFav(String image) {
        favRecipe recipe = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + FavoritesTable.TABLE +
                " where " + FavoritesTable.COL_IMAGE + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { image });

        if (cursor.moveToFirst()) {
            recipe = new favRecipe();
            recipe.setId(cursor.getInt(0));
            recipe.setTitle(cursor.getString(1));
            recipe.setIngredients(cursor.getString(2));
            recipe.setSteps(cursor.getString(3));
            recipe.setImageFileName(cursor.getString(4));
        }

        return recipe;
    }

    public void addFavRecipe(favRecipe recipe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoritesTable.COL_TITLE, recipe.getTitle());
        values.put(FavoritesTable.COL_ING, recipe.getIngredients());
        values.put(FavoritesTable.COL_STEPS, recipe.getSteps());
        values.put(FavoritesTable.COL_IMAGE, recipe.getImageFileName());

        long recipeId = db.insertOrThrow(FavoritesTable.TABLE, null, values);
        recipe.setId(recipeId);
    }

    /*
    public void updateQuestion(Question question) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuestionTable.COL_ID, question.getId());
        values.put(QuestionTable.COL_TEXT, question.getText());
        values.put(QuestionTable.COL_ANSWER, question.getAnswer());
        values.put(QuestionTable.COL_SUBJECT, question.getSubject());
        db.update(QuestionTable.TABLE, values,
                QuestionTable.COL_ID + " = " + question.getId(), null);
    }

    */

    public void deleteFav(String image) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites",
                "image = ?",new String[] {image});
    }

}
