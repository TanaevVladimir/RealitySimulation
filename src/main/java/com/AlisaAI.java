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
    public static ArrayList<String> field;
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

            System.out.println("⏳ Запрос к YandexGPT...");
            long start = System.currentTimeMillis();
            String answer = sendRequest(apiKey, modelUri, input);
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

    private static String sendRequest(String apiKey, String modelUri, String message) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("modelUri", modelUri);
        JsonObject completionOptions = new JsonObject();
        completionOptions.addProperty("stream", false);
        completionOptions.addProperty("temperature", 0.6);
        completionOptions.addProperty("maxTokens", 2000);
        requestBody.add("completionOptions", completionOptions);
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("text", message);
        messages.add(userMessage);
        requestBody.add("messages", messages);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                        gson.toJson(requestBody),
                        MediaType.parse("application/json; charset=utf-8")
                ))
                .addHeader("Authorization", "Api-Key " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonResponse.getAsJsonObject("result")
                .getAsJsonArray("alternatives")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("text").getAsString();
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
