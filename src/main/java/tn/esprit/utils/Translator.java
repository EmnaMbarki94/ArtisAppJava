package tn.esprit.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Translator {
    private static final String API_KEY = "cff40ffd59ebc534b65376ad97064e005bc7a317";

    public static String translate(String text, String sourceLang, String targetLang) throws Exception {
        URL url = new URL("https://api.translateplus.io/v1/translate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-API-KEY", API_KEY);
        conn.setDoOutput(true);

        String jsonSafeText = text
                .replace("\\", "\\\\")   // escape backslashes first
                .replace("\"", "\\\"")   // then double quotes
                .replace("\n", "\\n")    // then new lines
                .replace("\r", "");      // optionally remove carriage returns

        String jsonPayload = String.format(
                "{\"text\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                jsonSafeText, sourceLang, targetLang
        );
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes("UTF-8"));
        }
        InputStream is = (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line.trim());
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        return json.getJSONObject("translations").getString("translation");
    }
}
