package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.ChatResponse;
import com.ghanaairwatch.dto.PredictionResponse;
import com.ghanaairwatch.entity.AirQualityReading;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Rule-based "AI" chat assistant. It can't reason like a real LLM, but it
// recognises common questions (jogging, masks, children, forecast, weather)
// and answers them with the LIVE AQI and weather for the selected city.
@Service
public class ChatAssistant {

    private final AirQualityService airQualityService;
    private final HistoryService historyService;
    private final AqiPredictor predictor;
    private final HealthRiskCalculator bands;
    private final LocationRepository locationRepository;

    public ChatAssistant(AirQualityService airQualityService,
                         HistoryService historyService,
                         AqiPredictor predictor,
                         HealthRiskCalculator bands,
                         LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.historyService = historyService;
        this.predictor = predictor;
        this.bands = bands;
        this.locationRepository = locationRepository;
    }

    public ChatResponse reply(String question, Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse current = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        historyService.recordReading(location, current);

        String aqi = current.aqi() + " (" + bands.levelFor(current.aqi()) + ")";
        String q = question == null ? "" : question.toLowerCase().trim();

        String reply;
        if (q.isEmpty()) {
            reply = "Hi! I'm your AirWatch assistant. Ask me things like \"Can I jog today?\", "
                    + "\"Should I wear a mask?\", \"Is it safe for my kids?\" or \"How's the weather?\".";
        } else if (q.matches(".*\\b(hi|hello|hey|yo)\\b.*") && q.split("\\s+").length <= 3) {
            reply = "Hello! HOW MAY I HELP YOU? " + summary(current);
        } else if (q.contains("jog") || q.contains("run") || q.contains("exercise")
                || q.contains("workout") || q.contains("gym") || q.contains("sport")) {
            reply = joggingAdvice(current);
        } else if (q.contains("mask")) {
            reply = "AQI today is " + aqi + ". "
                    + (current.aqi() >= 150
                    ? "Yes — wear an N95 or FFP2 mask if you must go out; it filters the fine particles."
                    : current.aqi() >= 100
                    ? "A mask is recommended if you're sensitive, or spending a long time outdoors."
                    : "The air is fine today, so a mask isn't necessary for most people.");
        } else if (q.contains("window")) {
            reply = "With AQI at " + aqi + ", "
                    + (current.aqi() >= 100
                    ? "keep windows and doors closed to stop pollution getting in."
                    : "you can keep windows open — the air is acceptable.");
        } else if (q.contains("kid") || q.contains("child") || q.contains("school") || q.contains("baby")) {
            reply = "AQI today is " + aqi + ". "
                    + (current.aqi() >= 100
                    ? "Children are more sensitive to pollution — keep outdoor play short and indoors if possible."
                    : "Children can play outside today, but keep them away from busy roads.");
        } else if (q.contains("asthma") || q.contains("breath") || q.contains("inhaler") || q.contains("lung")) {
            reply = "AQI today is " + aqi + ". "
                    + (current.aqi() >= 100
                    ? "That's risky for asthma. Stay indoors, keep your inhaler close, and avoid exercise outside."
                    : "Conditions are okay for asthma today, but keep an inhaler handy on longer outings.");
        } else if (q.contains("tomorrow") || q.contains("forecast") || q.contains("predict")
                || q.contains("later") || q.contains("future")) {
            reply = forecastReply(location, current);
        } else if (q.contains("weather") || q.contains("temperature") || q.contains("hot")
                || q.contains("cold") || q.contains("rain") || q.contains("wind")) {
            reply = "Right now it's " + Math.round(current.temperature()) + "°C with "
                    + current.weatherDescription() + ", humidity " + current.humidity() + "%, wind "
                    + Math.round(current.windSpeed()) + " m/s and " + current.pressure() + " hPa pressure."
                    + (current.rain() > 0 ? " There's " + current.rain() + " mm of rain. " : " ") + summary(current);
        } else {
            reply = summary(current);
        }

        return new ChatResponse(reply, current.aqi(), bands.levelFor(current.aqi()));
    }

    private String summary(AirQualityResponse current) {
        String aqi = current.aqi() + " (" + bands.levelFor(current.aqi()) + ")";
        if (current.aqi() >= 150) {
            return "The AQI in this area is " + aqi + " — dangerous air quality. Avoid outdoor exercise and keep windows closed.";
        }
        if (current.aqi() >= 100) {
            return "The AQI here is " + aqi + ". Sensitive groups should limit time outdoors.";
        }
        return "The AQI here is " + aqi + " — the air is fine for most outdoor activity today.";
    }

    private String joggingAdvice(AirQualityResponse current) {
        String aqi = current.aqi() + " (" + bands.levelFor(current.aqi()) + ")";
        if (current.aqi() >= 150) {
            return "AQI today is " + aqi + ". Running outdoors isn't recommended — the air is dangerous. "
                    + "Try an indoor workout or wait for cleaner air.";
        }
        if (current.aqi() >= 100) {
            return "AQI today is " + aqi + ". A run isn't a great idea — keep it very short or train indoors instead.";
        }
        if (current.aqi() >= 50) {
            return "AQI today is " + aqi + ". A short, easy run is fine for most people, but skip it if you're sensitive.";
        }
        return "AQI today is " + aqi + ". Great conditions — enjoy your run!";
    }

    private String forecastReply(Location location, AirQualityResponse current) {
        List<AirQualityReading> history = historyService.getHistory(location.getId(), 7);
        PredictionResponse prediction = predictor.predict(history, current.aqi());
        return "I can only forecast from stored history. "
                + "Right now: AQI " + current.aqi() + ". Predicted: +1h = " + prediction.oneHour()
                + ", +6h = " + prediction.sixHours() + ", tomorrow = " + prediction.tomorrow()
                + ". " + prediction.note();
    }
}
