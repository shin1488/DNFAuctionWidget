package com.shin.dnfauctionwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.app.StatusBarManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shin.dnfauctionwidget.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainLayout.setBackground(getDrawable(R.drawable.bm));

        binding.mainEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = binding.mainEdittext.getText().toString();
                    startSearchResultActivity(searchText);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void startSearchResultActivity(String inputText) {
        if (inputText.length() == 0) {
            Toast.makeText(getApplicationContext(), "입력은 최소 1자 이상 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
            intent.putExtra("inputText", inputText);
            startActivity(intent);
        }
    }

}
