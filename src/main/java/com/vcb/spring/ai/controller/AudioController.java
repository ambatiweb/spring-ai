package com.vcb.spring.ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {

    private final OpenAiAudioTranscriptionModel audioTranscription;

    public AudioController(OpenAiAudioTranscriptionModel audioTranscription) {
        this.audioTranscription = audioTranscription;
    }

    @GetMapping("/audio-to-text")
    public String audioTranscription(){
        OpenAiAudioTranscriptionOptions options
                = OpenAiAudioTranscriptionOptions
                .builder()
                .language("en")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.JSON)
                .temperature(0.5f)
                .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                new ClassPathResource("audio/education.mp3"),options
        );

        return audioTranscription.call(prompt)
                .getResult()
                .getOutput();
    }


}
