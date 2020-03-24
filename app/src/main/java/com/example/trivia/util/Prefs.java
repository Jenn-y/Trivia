package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void saveHighScore(int score){
        int current = score;
        int lastScore = preferences.getInt("highScore", 0);

        if (current > lastScore){
            preferences.edit().putInt("highScore", current).apply();
        }
    }

    public int getHighScore(){
        return preferences.getInt("highScore", 0);
    }

    public void setState (int index){
        preferences.edit().putInt("state", index).apply();
    }

    public int getState(){
       return preferences.getInt("state", 0);
    }
}
