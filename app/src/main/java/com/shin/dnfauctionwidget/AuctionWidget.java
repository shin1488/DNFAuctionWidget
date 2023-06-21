package com.shin.dnfauctionwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of App Widget functionality.
 */
public class AuctionWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.auction_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // 위젯 레이아웃 설정
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.auction_widget);


            String itemName = "균열의 단편";
            String apiKey = context.getString(R.string.api_Key);

            //이미지 삽입
            remoteViews.setImageViewResource(R.id.gold1, R.drawable.gold);
            remoteViews.setImageViewResource(R.id.gold2, R.drawable.gold);
            remoteViews.setTextViewText(R.id.item_name, itemName);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            // API 요청
            String requestURL1 = "https://api.neople.co.kr/df/items?itemName=";
            String finalURL1 = requestURL1 + itemName + "&apikey=" + apiKey;
            System.out.println(finalURL1);
            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, finalURL1, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // JSON 데이터 파싱
                                JSONArray rowsArray = response.getJSONArray("rows");
                                JSONObject firstRow = rowsArray.getJSONObject(0);

                                String itemID = firstRow.getString("itemId");

                                // Glide를 사용하여 이미지 로드
                                Glide.with(context)
                                        .asBitmap()
                                        .load("https://img-api.neople.co.kr/df/items/" + itemID)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                // 비트맵 이미지를 RemoteViews에 설정
                                                remoteViews.setImageViewBitmap(R.id.item_image, resource);

                                                // 위젯 업데이트
                                                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                // 이미지 로드가 취소되거나 삭제된 경우 처리할 내용
                                            }
                                        });
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

            String requestURL2 = "https://api.neople.co.kr/df/auction?&limit=400&sort=unitPrice:asc&itemName=";
            String finalURL2 = requestURL2 + itemName + "&apikey=" + apiKey;
            JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, finalURL2, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // JSON 데이터 파싱
                                JSONArray rowsArray = response.getJSONArray("rows");
                                JSONObject firstRow = rowsArray.getJSONObject(0);

                                Integer unitPrice = firstRow.getInt("unitPrice");
                                Integer avgPrice = firstRow.getInt("averagePrice");

                                // 위젯에 텍스트 설정
                                remoteViews.setTextViewText(R.id.auction_minPrice, unitPrice.toString());
                                remoteViews.setTextViewText(R.id.auction_avgPrice, avgPrice.toString());

                                // 위젯 업데이트
                                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
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

            // API 요청 추가
            requestQueue.add(jsonObjectRequest1);
            requestQueue.add(jsonObjectRequest2);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}