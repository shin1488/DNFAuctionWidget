package com.shin.dnfauctionwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

            // API 요청
            String requestURL = "https://api.neople.co.kr/df/auction?&limit=400&sort=unitPrice:asc&itemName=";
            String itemName = "균열의 단편";
            String apiKey = context.getString(R.string.api_Key);
            String finalURL = requestURL + itemName + "&apikey=" + apiKey;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalURL, null,
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
            requestQueue.add(jsonObjectRequest);
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