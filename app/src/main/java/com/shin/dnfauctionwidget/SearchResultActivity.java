package com.shin.dnfauctionwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.shin.dnfauctionwidget.databinding.ActivitySearchResultBinding;

public class SearchResultActivity extends AppCompatActivity {
    ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String inputText = intent.getStringExtra("inputText");
        binding.textResult.setText(inputText);
        Log.d("t", inputText);
    }
}