package tn.esprit.services;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import tn.esprit.entities.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GeminiService {
    private static final String API_KEY = "AIzaSyAUvw1FxB5TlPWfrundHt1qnhkLlC56cxo";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    private List<String> chatHistory = new ArrayList<>();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GeminiService() {
        loadChatHistory();
    }

    private String getHistoryFilePath() {
        return "chat_history_" + Session.getUser().getId() + ".txt";
    }

    private void loadChatHistory() {
        try {
            Path path = Paths.get(getHistoryFilePath());
            if (Files.exists(path)) {
                chatHistory = Files.readAllLines(path);
            }
        } catch (IOException e) {
            System.err.println("Erreur chargement historique: " + e.getMessage());
        }
    }

    private void saveChatHistory() {
        try {
            Files.write(Paths.get(getHistoryFilePath()), chatHistory);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde historique: " + e.getMessage());
        }
    }

    public String sendMessage(String message) {
        try {
            chatHistory.add("User: " + message);

            String prompt = buildSmartPrompt(message);

            JsonObject requestBody = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();

            part.addProperty("text", prompt);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(gson.toJson(requestBody)));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();

                if (jsonResponse.has("error")) {
                    return handleApiError(jsonResponse);
                }

                String aiResponse = extractAiResponse(jsonResponse);
                aiResponse = filterResponse(aiResponse);

                chatHistory.add("AI: " + aiResponse);
                saveChatHistory();
                return aiResponse;
            }
            return "Aucune réponse reçue de l'API.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur technique: " + e.getMessage();
        }
    }
//Réponds techniquement et précisément en français.
    private String buildSmartPrompt(String message) {
        if (isAboutApplication(message)) {
            return "Tu es l'expert d'ArtisApp. \n"
                    + "Fonctionnalités: cours/galerie/evenement/magazin\n"
                    + "Question: " + message;
        } else {
            return "Tu es un assistant IA polyvalent. Réponds en français de manière claire et concise.\n"
                    + "Pour les questions complexes, donne des réponses structurées.\n"
                    + "Question: " + message;
        }
    }

    private boolean isAboutApplication(String message) {
        String lowerMsg = message.toLowerCase();
        return lowerMsg.contains("artisapp") || lowerMsg.contains("réclam")
                || lowerMsg.contains("appli") || lowerMsg.contains("fonctionnalité");
    }

    private String handleApiError(JsonObject jsonResponse) {
        JsonObject error = jsonResponse.getAsJsonObject("error");
        return "Erreur API: " + error.get("message").getAsString();
    }

    private String extractAiResponse(JsonObject jsonResponse) {
        return jsonResponse.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    private String filterResponse(String response) {
        if (response.toLowerCase().contains("politique") ||
                response.toLowerCase().contains("religion")) {
            return "Je ne discute pas de ce sujet. Puis-je vous aider sur autre chose ?";
        }
        return response;
    }

    public List<String> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }

    public void clearChatHistory() {
        chatHistory.clear();
        saveChatHistory();
    }
}