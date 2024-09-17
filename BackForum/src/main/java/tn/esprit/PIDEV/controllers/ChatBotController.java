package tn.esprit.PIDEV.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tn.esprit.PIDEV.dto.ChatBotResponse;
import tn.esprit.PIDEV.dto.chatBotRequest;


@RestController
@RequestMapping("/bot")
@CrossOrigin(origins = "http://localhost:8075", maxAge = 3600, allowCredentials="true")
public class ChatBotController {
    @Autowired
    private RestTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class); // Declare logger

    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;


    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        try {
            chatBotRequest request = new chatBotRequest(model, prompt);
            ChatBotResponse chatGptResponse = template.postForObject(apiURL, request, ChatBotResponse.class);
            return chatGptResponse.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            logger.error("An error occurred while generating:", e);
            return "An error occurred. Please try again later.";
        }
    }
}