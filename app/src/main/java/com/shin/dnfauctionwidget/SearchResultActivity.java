package com.shin.dnfauctionwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shin.dnfauctionwidget.databinding.ActivitySearchResultBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int paddingBottom = getNavigationBarHeight();
        binding.resultRecycler.setPadding(0, 0, 0, paddingBottom);

        Intent intent = getIntent();
        String inputText = intent.getStringExtra("inputText");

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new ResultAdapter(itemList);
        setupRecyclerView();
        performApiRequest(binding.getRoot().getContext(), inputText);
    }

    private void performApiRequest(Context context, String inputText) {
        String apiKey = context.getString(R.string.api_Key);

        // API 요청
        String requestURL = "https://api.neople.co.kr/df/items?itemName=";
        String finalURL = requestURL + inputText + "&wordType=front&limit=30&apikey=" + apiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalURL, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // JSON 데이터 파싱
                            JSONArray rowsArray = response.getJSONArray("rows");
                            itemList.clear();
                            if (rowsArray.length() > 0) {
                                for (int i = 0; i < rowsArray.length(); i++) {
                                    JSONObject row = rowsArray.getJSONObject(i);
                                    String itemID = row.getString("itemId");
                                    String imageUrl = "https://img-api.neople.co.kr/df/items/" + itemID;
                                    String itemName = row.getString("itemName");
                                    Item item = new Item(imageUrl, itemName, itemID);
                                    itemList.add(item);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "API Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.result_recycler);

        // RecyclerView 레이아웃 매니저 설정 (LinearLayoutManager, GridLayoutManager 등)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        adapter = new ResultAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }

    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
