package com.vcb.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FirstController {

    private final ChatClient chatClient;

    @Value("classpath:prompts/celebrity-details.st")
    private Resource celebPrompt;

    public FirstController(ChatClient.Builder builder) {
        this.chatClient = builder.build();

    }

    @GetMapping("/prompt")
    public String prompt(@RequestParam String message) {
        return chatClient.prompt(message).call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/celebrity")
    public String prompt2(@RequestParam String name) {

        PromptTemplate promptTemplate = new PromptTemplate(celebPrompt);

        Prompt prompt1 = promptTemplate.create(
                Map.of("name",name)
        );

        return chatClient.prompt(prompt1).call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/sport")
    public String prompt3(@RequestParam String name) {

        String message = """
                List the details of the sport %s along with their Rules
                and Regulations and the list of players who play this sport.
                """;
        String systemMessage = """
                You are a smart assistant who is very good at sports.
                If someone asks you about a sport, you will give the details of the sport else
                you will say that you are not aware of the sport.
                """;

        UserMessage userMessage = new UserMessage(
                String.format(message, name)
        );

        SystemMessage systemMessage1 = new SystemMessage(systemMessage);
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage1));

        return chatClient.prompt(prompt).call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

}
