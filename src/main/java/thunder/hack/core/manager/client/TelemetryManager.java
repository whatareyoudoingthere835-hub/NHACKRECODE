package thunder.hack.core.manager.client;

import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.utility.Timer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelemetryManager implements IManager {
    private final Timer pingTimer = new Timer();
    private int onlineCount = 0;
    private boolean firstRun = true; // ДОБАВИТЬ ЭТО

    private static final String API_URL = "https://idk5334.pythonanywhere.com/online";
    private static final int PING_INTERVAL = 60000; // 60 секунд вместо 90

    public void onUpdate() {
        // Первый запрос сразу при старте
        if (firstRun) {
            firstRun = false;
            fetchData();
            pingTimer.reset(); // Сбрасываем таймер
        }

        // Последующие запросы каждые 60 секунд
        if (pingTimer.every(PING_INTERVAL)) {
            fetchData();
        }
    }

    public void fetchData() {
        if (ClientSettings.telemetry.getValue()) {
            updateOnlineCounter();
        }
    }

    private void updateOnlineCounter() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body().trim();
            if (!responseBody.isEmpty()) {
                onlineCount = Integer.parseInt(responseBody);
            }
        } catch (Throwable ignored) {
            onlineCount = 0;
        }
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public java.util.List<String> getOnlinePlayers() {
        return new java.util.ArrayList<>();
    }

    public java.util.List<String> getAllPlayers() {
        return new java.util.ArrayList<>();
    }
}