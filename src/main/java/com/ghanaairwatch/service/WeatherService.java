package com.ghanaairwatch.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ghanaairwatch.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

// Fetches live weather (temperature, humidity, wind, pressure, rain) from
// OpenWeatherMap's /weather endpoint. We ask for "units=metric" so temperature
// comes back in Celsius and no conversion is needed.
@Service
public class WeatherService {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    public WeatherService(
            WebClient.Builder webClientBuilder,
            @Value("${owm.api.key}") String apiKey,
            @Value("${owm.weather.base-url}") String baseUrl
    ) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public WeatherResponse fetch(double lat, double lon) {
        String url = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s", baseUrl, lat, lon, apiKey);

        OwmWeather w = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OwmWeather.class)
                .block();

        double rain = w.rain() != null ? w.rain().oneHour() : 0.0;
        String description = w.weather().isEmpty() ? "n/a" : w.weather().get(0).description();

        return new WeatherResponse(
                w.main().temp(),
                w.main().humidity(),
                w.wind().speed(),
                w.wind().deg(),
                w.main().pressure(),
                rain,
                description,
                Instant.now().toEpochMilli()
        );
    }

    // --- Shapes matching OpenWeatherMap's /weather JSON ---
    private record OwmWeather(OwmMain main, OwmWind wind, OwmRain rain, java.util.List<OwmWeatherDesc> weather) {}
    private record OwmMain(double temp, int humidity, double pressure) {}
    private record OwmWind(double speed, int deg) {}
    private record OwmRain(@JsonProperty("1h") double oneHour) {}
    private record OwmWeatherDesc(String description) {}
}
