package com;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.Arrays;

public class AlisaAI {
    private static final String API_URL = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
    private static String currentModel = "yandexgpt-lite";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ArrayList<JsonObject> messageHistory = new ArrayList<>();
    public static void main(String[] args) {
        String folderId = getEnv("YANDEX_FOLDER_ID");
        String apiKey = getEnv("YANDEX_API_KEY");
        String modelUri = "gpt://" + folderId + "/" + currentModel;
        System.out.println("=== YandexGPT Client ===");
        System.out.println("Модель: " + modelUri);
        System.out.println("Для выхода: /exit\n");
        try (Scanner scanner = new Scanner(System.in)) {
            chat(apiKey, modelUri, scanner);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void chat(String apiKey, String modelUri, Scanner scanner) throws IOException {
        while (true) {
            System.out.print("Вы: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            if (input.startsWith("/")) {
                if (!processCommand(input, scanner)) {
                    break;
                }
                continue;
            }
            String systemMessage = "";
            System.out.println("⏳ Запрос к YandexGPT...");
            long start = System.currentTimeMillis();
            String answer = sendRequest(apiKey, modelUri, systemMessage,input);
            long time = System.currentTimeMillis() - start;

            System.out.println("🤖 YandexGPT (за " + time + " мс):");
            System.out.println(answer);
        }
    }

    private static boolean processCommand(String command, Scanner scanner) {
        String[] parts = command.substring(1).split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        switch (cmd) {
            case "exit", "выход":
                return false;

            case "help", "помощь":
                System.out.println(" /exit, /выход   - выйти из программы");
                break;

            default:
                System.err.println("Введите /help для списка команд");
        }
        return true;
    }

    private static String sendRequest(String apiKey, String modelUri, String systemMsg, String userMsg) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("modelUri", modelUri);

        JsonObject completionOptions = new JsonObject();
        completionOptions.addProperty("stream", false);
        completionOptions.addProperty("temperature", 0.3); // для магазина
        completionOptions.addProperty("maxTokens", 500);
        requestBody.add("completionOptions", completionOptions);
        JsonArray messagesToSend = new JsonArray();
        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("text", systemMsg);
        messagesToSend.add(system);

        for (JsonObject pastMessage : messageHistory) {
            messagesToSend.add(pastMessage);
        }

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("text", userMsg);
        messagesToSend.add(user);

        messageHistory.add(user);

        requestBody.add("messages", messagesToSend);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
                .addHeader("Authorization", "Api-Key " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMsg = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new IOException("API error: " + errorMsg);
            }

            JsonObject result = jsonResponse.getAsJsonObject("result");
            JsonArray alternatives = result.getAsJsonArray("alternatives");
            JsonObject firstAlt = alternatives.get(0).getAsJsonObject();
            JsonObject messageObj = firstAlt.getAsJsonObject("message");
            String answer = messageObj.get("text").getAsString();

            JsonObject assistant = new JsonObject();
            assistant.addProperty("role", "assistant");
            assistant.addProperty("text", answer);
            messageHistory.add(assistant);
            return answer;
        }
    }

    public static String getEnv(String key) {
        try {
            Dotenv dotenv = Dotenv.load();
            return dotenv.get(key);
        } catch (Exception e) {
            return null;
        }
    }

}
