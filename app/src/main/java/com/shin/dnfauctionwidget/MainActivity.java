package com.shin.dnfauctionwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

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

        new ApiRequestTask().execute();
    }

    private class ApiRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String apiKey = getString(R.string.api_Key);
            String requestURL = "https://api.neople.co.kr/df/auction?&limit=400&sort=unitPrice:asc&itemName=";
            String itemName = "균열의 단편";

            try {
                URL url = new URL(requestURL + itemName + "&apikey=" + apiKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // API 요청 수행 및 응답 데이터 받아오기
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStream.close();

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray rowsArray = jsonObject.getJSONArray("rows");
                    JSONObject firstRow = rowsArray.getJSONObject(0);

                    int unitPrice = firstRow.getInt("unitPrice");

                    // TextView에 unitPrice 값 설정
                    binding.apiReturn.setText(String.valueOf(unitPrice));
                } catch (JSONException e) {
                    e.printStackTrace();
                    // JSON 파싱 오류 처리
                    binding.apiReturn.setText("JSON 파싱 오류");
                }
            } else {
                // API 요청 오류 처리
                binding.apiReturn.setText("API 요청 오류");
            }
        }
    }
}
