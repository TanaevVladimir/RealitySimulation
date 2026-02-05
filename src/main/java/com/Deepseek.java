package com;
import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;
public class Deepseek {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static String currentModel = "deepseek/deepseek-chat";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static void main(String[] args) {
        String apiKey = getkey(1);
        try (Scanner scanner = new Scanner(System.in)) {
            chat(apiKey, scanner);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void chat(String apiKey, Scanner scanner) throws IOException {
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
                System.out.println("Запрос...");
                String answer = sendRequest(apiKey, input);
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

    private static String sendRequest(String apiKey, String message) throws  IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", currentModel);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 2000);
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(
                        gson.toJson(requestBody),
                        MediaType.parse("application/json; charset=utf-8")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "http://localhost:8080")
                .addHeader("X-Title", "Java OpenRouter Client")
                .build();


        Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            }

            public static String price (String product) throws IOException {
                String apiKey = getkey(2);
                String answer = sendRequest(apiKey,product);
                return answer;
            }

            public static void shopstart() throws IOException {
                String apiKey = getkey(2);
                String comment = "Ты - продавец товаров. На основе данных из интернета и предоставленного ниже списка цен, тебе необходимо рассчитывать стоимоть продукта пользователя в долларах. В дальнейшем отвечай на запрос только числом, без дополнительных комментариев. Число должно отражать цену продукта. Если продукт есть в списке, верни значение цены из него, если нет - рассчитай. Если ты рассчитал цену на продукт, при повторном запросе этого же продукта ты должен вернуть ту же цену.";
                String pricelist = "Цены: \n Яблоко - 10$\n ";
                String answer = sendRequest(apiKey, comment+"\n"+pricelist);
            }

            public static String getkey (int n) {
                Dotenv dotenv = Dotenv.load();
                String apiKey = dotenv.get("API_KEY"+n);
                return apiKey;
            }
        }




