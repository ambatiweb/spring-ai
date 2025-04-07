package com.vcb.spring.ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {

    private final OpenAiAudioTranscriptionModel audioTranscription;

    private final OpenAiAudioSpeechModel audioSpeechModel;

    public AudioController(OpenAiAudioTranscriptionModel audioTranscription, OpenAiAudioSpeechModel audioSpeechModel) {
        this.audioTranscription = audioTranscription;
        this.audioSpeechModel = audioSpeechModel;
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

    @GetMapping("text-to-audio/{prompt}")
    public ResponseEntity<Resource> generateAudio(@PathVariable String prompt){
        OpenAiAudioSpeechOptions options =
                OpenAiAudioSpeechOptions.builder()
                        .model(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                        .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                        .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                        .speed(1.0f)
                        .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(prompt, options);

        SpeechResponse response = audioSpeechModel.call(speechPrompt);

        byte[] output = response.getResult().getOutput();

        ByteArrayResource byteArrayResource = new ByteArrayResource(output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("audio.mp3")
                        .build().toString())
                .body(byteArrayResource);
    }


}
