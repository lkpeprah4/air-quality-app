package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

// Talks to OpenWeatherMap on the server side, so the API key never has to be
// exposed to the browser (unlike the frontend-only version we built first).
// Now fetches BOTH the pollution data and the current weather in one place,
// so the dashboard only needs a single call.
@Service
public class AirQualityService {

    private final WebClient webClient;
    private final WeatherService weatherService;
    private final String apiKey;
    private final String baseUrl;

    public AirQualityService(
            WebClient.Builder webClientBuilder,
            WeatherService weatherService,
            @Value("${owm.api.key}") String apiKey,
            @Value("${owm.api.base-url}") String baseUrl
    ) {
        this.webClient = webClientBuilder.build();
        this.weatherService = weatherService;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public AirQualityResponse fetchCurrent(double lat, double lon) {
        String url = String.format("%s?lat=%s&lon=%s&appid=%s", baseUrl, lat, lon, apiKey);

        OwmResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OwmResponse.class)
                .block();

        OwmComponents c = response.list().get(0).components();
        int aqi = pm25ToUsAqi(c.pm2_5());
        WeatherResponse w = weatherService.fetch(lat, lon);

        return new AirQualityResponse(
                aqi,
                c.pm2_5(),
                c.pm10(),
                c.o3(),
                c.no2(),
                c.so2(),
                c.co() / 1000.0,
                w.temperature(),
                w.humidity(),
                w.windSpeed(),
                w.windDirection(),
                w.pressure(),
                w.rain(),
                w.description(),
                Instant.now().toEpochMilli()
        );
    }

    // Converts a raw PM2.5 concentration into the familiar 0-500 US AQI scale
    // using the EPA's official breakpoint table. OpenWeatherMap's own 1-5
    // index is too coarse for the health bands the frontend already uses.
    private int pm25ToUsAqi(double pm25) {
        double[][] breakpoints = {
                {0.0, 12.0, 0, 50},
                {12.1, 35.4, 51, 100},
                {35.5, 55.4, 101, 150},
                {55.5, 150.4, 151, 200},
                {150.5, 250.4, 201, 300},
                {250.5, 500.4, 301, 500},
        };
        for (double[] bp : breakpoints) {
            if (pm25 >= bp[0] && pm25 <= bp[1]) {
                double cLo = bp[0], cHi = bp[1], iLo = bp[2], iHi = bp[3];
                return (int) Math.round(((iHi - iLo) / (cHi - cLo)) * (pm25 - cLo) + iLo);
            }
        }
        return 500;
    }

    // --- Shapes matching OpenWeatherMap's raw JSON response ---
    private record OwmResponse(java.util.List<OwmEntry> list) {}
    private record OwmEntry(OwmComponents components) {}
    private record OwmComponents(double co, double no2, double o3, double so2, double pm2_5, double pm10) {}
}
