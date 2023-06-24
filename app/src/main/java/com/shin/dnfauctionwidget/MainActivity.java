package com.shin.dnfauctionwidget;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.shin.dnfauctionwidget.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.main_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
