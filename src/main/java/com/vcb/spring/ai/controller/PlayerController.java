package com.vcb.spring.ai.controller;

import com.vcb.spring.ai.model.Achievement;
import com.vcb.spring.ai.model.Player;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("player/api/v1")
public class PlayerController {

    private final ChatClient chatClient;

    public PlayerController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping
    public List<Player> getPlayerAchievement(@RequestParam String name) {

        BeanOutputConverter<List<Player>> converter
                = new BeanOutputConverter<>(new ParameterizedTypeReference<List<Player>>() {
        });

        String message = """
                Generate a list of career achievements for the sportsperson {sports}.
                Include the player as the key and achievements as the value for it.
                {format}
                """;
        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create(
                Map.of("sports",name, "format",converter.getFormat())
        );

        Generation result = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult();

        return converter.convert(result.getOutput().getText());

    }

    @GetMapping("/achievements/player")
    public List<Achievement> getAchievements(@RequestParam String name) {

        String message = """
                Provide a List of achievements for the player {player}.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(
                Map.of("player", name)
        );

        return chatClient.prompt(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<Achievement>>() {
                });
    }

}
