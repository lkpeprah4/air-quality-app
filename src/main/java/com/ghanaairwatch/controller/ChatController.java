package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.ChatResponse;
import com.ghanaairwatch.service.ChatAssistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// GET /api/chat?locationId=1&q=Can I jog today?
// The rule-based AI assistant answers using the city's live AQI + weather.
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatAssistant chatAssistant;

    public ChatController(ChatAssistant chatAssistant) {
        this.chatAssistant = chatAssistant;
    }

    @GetMapping
    public ChatResponse chat(@RequestParam Long locationId,
                             @RequestParam(defaultValue = "") String q) {
        return chatAssistant.reply(q, locationId);
    }
}
