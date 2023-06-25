package com.shin.dnfauctionwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AuctionWidget extends AppWidgetProvider {
    private static final String ACTION_REFRESH_CLICK = "android.appwidget.action.APPWIDGET_UPDATE";

    private String getItemId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SavedItem", Context.MODE_PRIVATE);
        return sharedPreferences.getString("itemId", "a072ded7b41743a22cccb74cd73bc24d");
    }

    private String getItemName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SavedItem", Context.MODE_PRIVATE);
        return sharedPreferences.getString("itemName", "무결점 골든 베릴");
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.auction_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // 위젯 레이아웃 설정
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.auction_widget);

            String itemName = getItemName(context);

            remoteViews.setTextViewText(R.id.item_name, itemName);

            // ImageButton에 클릭 리스너 설정
            Intent intent = new Intent(ACTION_REFRESH_CLICK);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.refresh_button, pendingIntent);

            //api 요청
            performApiRequest(context, appWidgetId);

            // 위젯 업데이트
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d("Clicked", "Clicked");

        if (intent.getAction().equals(ACTION_REFRESH_CLICK)) {
            // ImageButton가 클릭되었을 때 실행할 함수 호출
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                performApiRequest(context, appWidgetId);
            }
        }
    }

    private void performApiRequest(Context context, int appWidgetId) {
        Log.d("refresh", "refresh");
        String encodeItemName = "";
        try {
            encodeItemName = URLEncoder.encode(getItemName(context), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String itemName = getItemName(context);
        String apiKey = context.getString(R.string.api_Key);

        // API 요청
        String requestURL1 = "https://api.neople.co.kr/df/items?itemName=";
        String finalURL1 = requestURL1 + encodeItemName + "&apikey=" + apiKey;
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
                            GlideApp.with(context)
                                    .asBitmap()
                                    .load("https://img-api.neople.co.kr/df/items/" + itemID)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            // 비트맵 이미지를 RemoteViews에 설정
                                            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.auction_widget);
                                            remoteViews.setImageViewBitmap(R.id.item_image, resource);
                                            remoteViews.setTextViewText(R.id.item_name, itemName);

                                            // 위젯 업데이트
                                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
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
        String finalURL2 = requestURL2 + encodeItemName + "&apikey=" + apiKey;
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, finalURL2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // JSON 데이터 파싱
                            JSONArray rowsArray = response.getJSONArray("rows");
                            if(rowsArray.length() != 0) {
                                JSONObject firstRow = rowsArray.getJSONObject(0);

                                Integer unitPrice = firstRow.getInt("unitPrice");
                                Integer avgPrice = firstRow.getInt("averagePrice");
                                NumberFormat numberFormat = new DecimalFormat("#,###");

                                Log.d("price", unitPrice.toString());
                                Log.d("price", avgPrice.toString());

                                // 위젯에 텍스트 설정
                                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.auction_widget);
                                remoteViews.setTextViewText(R.id.auction_minPrice, numberFormat.format(unitPrice));
                                remoteViews.setTextViewText(R.id.auction_avgPrice, numberFormat.format(avgPrice));

                                // 위젯 업데이트
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                            } else {
                                // 위젯에 텍스트 설정
                                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.auction_widget);
                                remoteViews.setTextViewText(R.id.auction_minPrice, "0");
                                remoteViews.setTextViewText(R.id.auction_avgPrice, "0");

                                // 위젯 업데이트
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
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
        requestQueue.add(jsonObjectRequest1);
        requestQueue.add(jsonObjectRequest2);
    }
}