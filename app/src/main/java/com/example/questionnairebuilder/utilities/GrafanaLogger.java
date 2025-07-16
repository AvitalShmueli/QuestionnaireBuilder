package com.example.questionnairebuilder.utilities;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import com.example.questionnairebuilder.BuildConfig;

public class GrafanaLogger {
    private static final String LOKI_URL = "https://logs-prod-012.grafana.net/loki/api/v1/push";
    private static final String TENANT_ID = BuildConfig.GRAFANA_TENANT_ID;
    private static final String API_TOKEN = BuildConfig.GRAFANA_API_TOKEN;

    private static void sendLog(String level, String tag, String message, JSONObject extraJson) {
        try {
            long timestampNs = new Date().getTime() * 1_000_000;

            JSONObject lineObj = new JSONObject();
            lineObj.put("level", level);
            lineObj.put("tag", tag);
            lineObj.put("message", message);
            if (extraJson != null) {
                lineObj.put("details", extraJson); // nested JSON
            }

            String logLine = lineObj.toString();

            JSONArray values = new JSONArray();
            values.put(new JSONArray().put(String.valueOf(timestampNs)).put(logLine));

            JSONObject stream = new JSONObject();
            stream.put("stream", new JSONObject()
                    .put("app", "android-app")
                    .put("level", level)
                    .put("tag", tag));
            stream.put("values", values);

            JSONArray streams = new JSONArray();
            streams.put(stream);

            JSONObject payload = new JSONObject();
            payload.put("streams", streams);

            URL url = new URL(LOKI_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            String credentials = TENANT_ID + ":" + API_TOKEN;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            int code = conn.getResponseCode();
            if (code != 204) {
                Log.e("GrafanaLogger", "Loki response code: " + code);
            }

        } catch (Exception e) {
            Log.e("GrafanaLogger", "Failed to send log: " + e.getMessage());
        }
    }

    public static void info(String tag, String message) {
        info(tag, message, null);
    }

    public static void info(String tag, String message, JSONObject extraJson) {
        new Thread(() -> sendLog("info", tag, message, extraJson)).start();
        Log.i("pttt" + tag, message);
    }

    public static void warning(String tag, String message) {
        warning(tag, message, null);
    }

    public static void warning(String tag, String message, JSONObject extraJson) {
        new Thread(() -> sendLog("warning", tag, message, extraJson)).start();
        Log.w("pttt" + tag, message);
    }

    public static void error(String tag, String message) {
        error(tag, message, null);
    }

    public static void error(String tag, String message, JSONObject extraJson) {
        new Thread(() -> sendLog("error", tag, message, extraJson)).start();
        Log.e("pttt" + tag, message);
    }
}
