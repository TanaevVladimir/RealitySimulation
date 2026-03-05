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
    private static String currentModel = "yandexgpt/rc";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ArrayList<String> messageHistory = new ArrayList<>();
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
            messageHistory.add(input);
            System.out.println("⏳ Запрос к YandexGPT...");
            long start = System.currentTimeMillis();
            String answer = sendRequest(apiKey,messageHistory.toString(), modelUri);
            messageHistory.add(answer);
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

    private static String sendRequest(String apiKey, String message, String modelUri) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("modelUri", modelUri);

        JsonObject completionOptions = new JsonObject();
        completionOptions.addProperty("stream", false);
        completionOptions.addProperty("temperature", 0.2);
        completionOptions.addProperty("maxTokens", 100);
        requestBody.add("completionOptions", completionOptions);

        JsonArray messages = new JsonArray();

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("text", message);
        messages.add(user);

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
            if (alternatives.size() == 0) {
                throw new IOException("Пустой ответ от API");
            }
            JsonObject firstAlt = alternatives.get(0).getAsJsonObject();
            JsonObject messageObj = firstAlt.getAsJsonObject("message");
            return messageObj.get("text").getAsString();
        }
    }
    public static void shopstart () {
        String promt = "Ты продавец. Отвечай только числом (цена в $). База: " +
                "яблоко=1.2, груша=1.3, банан=0.8, апельсин=1.4, лимон=0.9, лайм=1.1, " +
                "клубника=5.0, виноград=4.5, картофель=1.5, морковь=1.2, лук=1.1, свекла=1.3, " +
                "помидор=2.5, огурец=2.0, капуста=1.4, перец=2.8, хлеб=2.5, молоко=1.8, " +
                "яйцо=2.9, сыр=12.0, масло сливочное=4.5, масло подсолнечное=3.5, сахар=2.2, " +
                "мука=1.9, рис=2.4, гречка=2.8, макароны=2.1, соль=0.8, чай=3.5, кофе=15.0, " +
                "говядина=15.0, свинина=8.5, курица=5.5, индейка=7.2, фарш говяжий=12.0, " +
                "фарш свиной=7.8, колбаса вареная=9.0, колбаса копченая=14.0, сосиски=8.5, " +
                "бекон=11.0, рыба=12.0, креветки=18.0, консервы=3.5, икра=15.0, пельмени=7.5, " +
                "стиральный порошок=8.5, кондиционер=4.5, средство посуда=3.2, мыло=2.0, " +
                "шампунь=4.5, гель душа=3.8, паста зубная=3.0, щетка зубная=2.5, дезодорант=4.0, " +
                "крем лицо=6.5, туалетная бумага=3.5, полотенца бумажные=3.0, салфетки=2.2, " +
                "чистящее=3.8, губки=1.5, наушники=35.0, умные часы=85.0, колонка=50.0, " +
                "смартфон=450.0, ноутбук=700.0, планшет=350.0, зарядка=15.0, пауэрбанк=25.0, " +
                "флешка=12.0, внешний диск=60.0, чайник=25.0, микроволновка=80.0, холодильник=450.0, " +
                "стиральная машина=380.0, пылесос=120.0, лампочка=4.5, удлинитель=8.0, " +
                "батарейки=5.0, посуда=25.0, кастрюля=18.0, сковорода=22.0, тарелки=15.0, " +
                "кружка=5.0, стаканы=12.0, приборы=20.0, полотенце=8.0, постельное=35.0, " +
                "подушка=18.0, одеяло=30.0, вешалки=6.0, футболка=15.0, джинсы=40.0, " +
                "куртка=60.0, свитер=35.0, кроссовки=55.0, ботинки=70.0, носки=3.0, шапка=12.0, " +
                "перчатки=10.0, шарф=8.0. Если товара нет в базе, оцени цену сам. Если товар уже был в диалоге, используй ту же цену.";
        messageHistory.add(promt);
        main(null);
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
